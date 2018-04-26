package com.easyschools.teacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {
    private Context context;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private EditText msg;
    private ImageButton send;
    private static String room_id = "";
    private RecyclerView messages;
    private List<MessageData> messageDataList;
    private RecyclerView.Adapter messagesAdapter;
    private static boolean firstTime = false;
    private static String user_id, to_send, name = "";
    private static List<String> temp_id;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getApplicationContext();
        mSocket = app.getSocket();
        mSocket.connect();
        setContentView(R.layout.activity_chat);
        apis = new Apis();
        settings = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        name = settings.getString("NAME", "");
        Log.d("NAME", name);
        user_id = settings.getString("ID", "");
        send = findViewById(R.id.send_btn);
        msg = findViewById(R.id.message);
        messages = findViewById(R.id.messages);
        messageDataList = new ArrayList<>();
        temp_id = new ArrayList<>();
        if (UsersAdapter.users_ids == null) {
            if (getIntent().getStringExtra("TO_BE_SEND") != null) {
                to_send = getIntent().getStringExtra("TO_BE_SEND");
                temp_id.add(to_send);
                Log.d("RIGHT", to_send);
            }
        } else {
            temp_id = UsersAdapter.users_ids;
        }
        if (getIntent().getStringExtra("ROOM_ID") != null) {
            firstTime = false;
            room_id = getIntent().getStringExtra("ROOM_ID");
            getMsg(room_id);
        } else {
            firstTime = true;
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstTime) {
                    Log.d("isfirsttime", String.valueOf(firstTime));
                    createRoom();
                } else {
                    Log.d("notfirsttime", String.valueOf(firstTime));
                    sendMessage(msg.getText().toString());
                    saveMessageToApi(msg.getText().toString());

                }
            }
        });
        mSocket.on("message", handleIncomingMessages);
    }

    private void createRoom() {
        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl() + lang + "/rooms/createroom",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            msg.setText("");
                            JSONObject msgObject = new JSONObject(response);
                            JSONObject mssg = msgObject.getJSONObject("message");
                            MessageData messageData = new MessageData();
                            messageData.setMsg(mssg.getString("body"));
                            messageData.setTime(mssg.getString("created_at"));
                            JSONObject user = mssg.getJSONObject("user");
                            messageData.setName(user.getString("name"));
                            messageDataList.add(messageData);
                            messagesAdapter =
                                    new MessagesAdapter(messageDataList, ChatActivity.this);
                            messages.setAdapter(messagesAdapter);
                            RecyclerView.LayoutManager layoutManager
                                    = new LinearLayoutManager(ChatActivity.this);
                            messages.setLayoutManager(layoutManager);
                            messages.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("ERROR", error.toString());
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", user_id);
                params.put("body", msg.getText().toString());
                for (int k = 0; k < temp_id.size(); k++) {
                    Log.d("USER_ID", temp_id.get(k));
                    params.put("user_id[" + k + "]", temp_id.get(k));
                }
                Log.d("PARAMS", params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }

    private void getMsg(String room_id) {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/rooms/roommessages?room_id=" + room_id,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Messages", response.toString());
                        try {

                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                JSONObject user = dataObject.getJSONObject("user");
                                MessageData messageData = new MessageData();
                                messageData.setMsg(dataObject.getString("body"));
                                messageData.setId(dataObject.getString("user_id"));
                                messageData.setName(user.getString("name"));
                                messageData.setTime(dataObject.getString("created_at"));
                                messageDataList.add(messageData);
                            }
                            messagesAdapter =
                                    new MessagesAdapter(messageDataList, ChatActivity.this);
                            messages.setAdapter(messagesAdapter);
                            RecyclerView.LayoutManager layoutManager
                                    = new LinearLayoutManager(ChatActivity.this);
                            messages.setLayoutManager(layoutManager);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MESSAGES_ERROR", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    private void sendMessage(final String body) {
        JSONObject sendText = new JSONObject();
        try {
            sendText.put("data", body);
            //sendText.put("name" , name);
            Log.d("FINE", String.valueOf(sendText));
            mSocket.emit("message", sendText);
        } catch (JSONException e) {
            Log.d("ERROR", e.getMessage());
        }
    }

    private void saveMessageToApi(final String body) {
        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl() + lang + "/rooms/createroom",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        try {
                            msg.setText("");
                            JSONObject msgObject = new JSONObject(response);
                            JSONObject mssg = msgObject.getJSONObject("message");
                            MessageData messageData = new MessageData();
                            messageData.setMsg(mssg.getString("body"));
                            messageData.setTime(mssg.getString("created_at"));
                            JSONObject user = mssg.getJSONObject("user");
                            messageData.setName(user.getString("name"));
                            messageDataList.add(messageData);
//                            messagesAdapter =
//                                    new MessagesAdapter(messageDataList, ChatActivity.this);
//                            messages.setAdapter(messagesAdapter);
//                            RecyclerView.LayoutManager layoutManager
//                                    = new LinearLayoutManager(ChatActivity.this);
//                            messages.setLayoutManager(layoutManager);
                            messagesAdapter.notifyDataSetChanged();
                            messages.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("ERROR", error.toString());
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", user_id);
                params.put("room_id", room_id);
                params.put("body", body);
                for (int k = 0; k < temp_id.size(); k++) {
                    Log.d("TEMP_ID", temp_id.get(k));
                    params.put("user_id[" + 0 + "]", temp_id.get(k));
                }
                Log.d("PARAMS", params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }


//    private Emitter.Listener handleIncomingMessages = new Emitter.Listener(){
//        @Override
//        public void call(final Object... args){
//            ChatActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//
//                    //String imageText;
//                    try {
//                        String message = data.getString("data");
//                        Log.d("DHD", message);
//                        //Toast.makeText(ChattingActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                        addMessage(message);
//
//                    } catch (JSONException e) {
//                        // return;
//                    }
//                }
//            });
//        }
//    };

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String s = args[0].toString();
                        JSONObject json = new JSONObject(s);
                        String message = json.getString("body");
                        //String name = json.getString("name");
                        Log.d("MESSAGE", message);
                        addMessage(message, name);
                    } catch (JSONException e) {
                        // return;
                    }
                }

            });
        }
    };

    private void addMessage(String message, String name) {
        MessageData messageData = new MessageData();
        messageData.setMsg(message);
        messageData.setName(name);
        messageDataList.add(messageData);
        if (messagesAdapter == null) {
            messagesAdapter =
                    new MessagesAdapter(messageDataList, ChatActivity.this);
        } else {
            messagesAdapter.notifyItemInserted(messageDataList.size() - 1);
        }
        scrollToBottom();
    }

    private void scrollToBottom() {
        messages.scrollToPosition(messagesAdapter.getItemCount() - 1);
    }
}
