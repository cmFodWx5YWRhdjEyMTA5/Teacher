package com.easyschools.teacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Personal extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String EXTRA_MESSAGE = "PERSONAL";
    TextView marital_status , no_children , father_name ,
            mother_name , spouse_name , blood_group , nationality , office_phone_one
            , office_phone_two , mobile , home_phone ,
            employee_email , fax ;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context context ;
    public Personal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_personal, container, false);
        apis = new Apis();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        editor = settings.edit();
        lang = settings.getString("LANG", "");
        marital_status = v.findViewById(R.id.marital_status);
        no_children = v.findViewById(R.id.no_children);
        father_name = v.findViewById(R.id.father_name);
        mother_name = v.findViewById(R.id.mother_name);
        spouse_name = v.findViewById(R.id.spouse_name);
        blood_group = v.findViewById(R.id.blood_group);
        nationality = v.findViewById(R.id.nationality);
        office_phone_one = v.findViewById(R.id.office_phone_one);
        office_phone_two = v.findViewById(R.id.office_phone_two);
        mobile = v.findViewById(R.id.mobile);
        home_phone = v.findViewById(R.id.home_phone);
        employee_email = v.findViewById(R.id.employee_email);
        fax =v.findViewById(R.id.fax);


        return v ;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void getEmployeeData()
    {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        "http://192.168.1.32:5000/api/v1/employees/admission/get-personal-details?employee_id=2",
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GENERAL_DATA", response.toString());
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i =0 ; i < data.length() ; i++)
                            {
                                JSONObject dataObject = data.getJSONObject(i);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_GENERAL_DATA", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
