package com.easyschools.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AllUsersFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Context context ;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor ;
    private List<UserData> userDataList ;
    private RecyclerView all_users;
    private Button start_chat ;
    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_all_users, container, false);
        if(getActivity() != null)
        {
            context = getActivity().getApplicationContext();
        }
        apis = new Apis();
        userDataList = new ArrayList<>();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        editor = settings.edit();
        lang = settings.getString("LANG", "");
        all_users =v.findViewById(R.id.all_users);
        getALlUsers();
        start_chat = v.findViewById(R.id.select_users);
        start_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), ChatActivity.class);
                startActivity(i);
            }
        });
            return v ;
    }

    // TODO: Rename method, update argument and hook method into UI event
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


    private void getALlUsers()
    {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET,
                        apis.getUrl()+lang+"/rooms/UserToSend",
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("USERS", response.toString());
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                UserData userData = new UserData();
                                userData.setId(dataObject.getString("id"));
                                userData.setName(dataObject.getString("name"));
                                userData.setImage("https://crm.easyschools.org/uploads/"+
                                        dataObject.getString("image"));
                                userDataList.add(userData);
                            }
                            RecyclerView.Adapter usersAdapter =
                                    new UsersAdapter(userDataList, context);
                            all_users.setAdapter(usersAdapter);
                            RecyclerView.LayoutManager layoutManager
                                    = new LinearLayoutManager(getContext());
                            all_users.setLayoutManager(layoutManager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_STUDENTS", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
