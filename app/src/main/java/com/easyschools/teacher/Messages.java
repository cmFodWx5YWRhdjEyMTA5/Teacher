package com.easyschools.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messages extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageButton new_msg ;
    private Context context , mcontext;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor ;
    private List<RoomsData>  roomsDataList ;
    private RecyclerView roomsRecycle;
    private String user_id = "";
    private CircleImageView school_logo ;

    public Messages() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
    ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_messages, container, false);
        new_msg = v.findViewById(R.id.start_new_msg);
        if(getActivity() != null)
        {
            context = getActivity().getApplicationContext();
            mcontext = getActivity();
        }
        apis = new Apis();
        school_logo = v.findViewById(R.id.m_logo);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        roomsDataList = new ArrayList<>();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        editor = settings.edit();
        lang = settings.getString("LANG", "");
        user_id = settings.getString("ID","");
        new_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllUsersFragment usersFragment = new AllUsersFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction().addToBackStack(null);
                transaction.replace(R.id.main_container, usersFragment).commit();
            }
        });
        roomsRecycle = v.findViewById(R.id.all_rooms);
        getALlRooms();
        return v;
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void getALlRooms()
    {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl()+lang+"/rooms/getuserroom?user_id="+user_id,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Rooms", response.toString());
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                JSONObject rooms = dataObject.getJSONObject("room");
                                RoomsData roomsData = new RoomsData();
                                roomsData.setId(rooms.getString("id"));
                                roomsData.setName(rooms.getString("name"));
                                roomsData.setUser_id(dataObject.getString("user_id"));
                                Log.d("USER_ID", dataObject.getString("user_id"));
                                roomsDataList.add(roomsData);
                            }
                            RecyclerView.Adapter allRoomsAdapter =
                                    new AllRoomsAdapter(roomsDataList, context , mcontext);
                            roomsRecycle.setAdapter(allRoomsAdapter);
                            RecyclerView.LayoutManager layoutManager
                                    = new LinearLayoutManager(getContext());
                            roomsRecycle.setLayoutManager(layoutManager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ROOMS_ERROR", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
