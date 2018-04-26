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

public class GeneralDetails extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String EXTRA_MESSAGE = "GENERAL";
    TextView employee_number , joining_date , department ,
            category , position , grade , title , manager
            , gender , status , qualifications ,
            experience , info , biometric_id ;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context context ;
    public GeneralDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general_details, container, false);
        if (getActivity() != null)
        {
            context = getActivity().getApplicationContext();
        }
        apis = new Apis();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        editor = settings.edit();
        lang = settings.getString("LANG", "");
        employee_number = v.findViewById(R.id.employee_number);
        joining_date = v.findViewById(R.id.joining_date);
        department = v.findViewById(R.id.department);
        category = v.findViewById(R.id.category);
        position = v.findViewById(R.id.position);
        grade = v.findViewById(R.id.grade);
        title = v.findViewById(R.id.job_title);
        manager = v.findViewById(R.id.manage_name);
        gender = v.findViewById(R.id.gender);
        status = v.findViewById(R.id.status);
        qualifications = v.findViewById(R.id.qualifications);
        experience = v.findViewById(R.id.total_experience);
        info =v.findViewById(R.id.experience_info);
        biometric_id = v.findViewById(R.id.biometric_id);
        getEmployeeData();
        return v ;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public static final GeneralDetails newInstance(String message)
    {

        GeneralDetails f = new GeneralDetails();

        Bundle bdl = new Bundle(1);

        bdl.putString(EXTRA_MESSAGE, message);

        f.setArguments(bdl);

        return f;

    }
    public void getEmployeeData()
    {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        "http://192.168.1.32:5000/api/v1/employees/admission/get-general-details?employee_id=2",
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GENERAL_DATA", response.toString());
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for (int i =0 ; i < data.length() ; i++)
                            {
                                JSONObject dataObject = data.getJSONObject(i);
                                employee_number.setText(dataObject.getString("code"));
                                joining_date.setText(dataObject.getString("joining_date"));
                                department.setText(dataObject.getString("employees_department_id"));
                                category.setText(dataObject.getString("employees_category_id"));
                                position.setText(dataObject.getString("employees_position_id"));
                                grade.setText(dataObject.getString("employees_grade_id"));
                                title.setText(dataObject.getString("job_title"));
                                gender.setText(dataObject.getString("gender"));
                                qualifications.setText(dataObject.getString("qualification"));
                                experience.setText(dataObject.getString("total_experience"));
                                info.setText(dataObject.getString("experience_info"));
                                biometric_id.setText(dataObject.getString("biometric_id"));
                                //status.setText(dataObject.getString(""));
                                //manager.setText(dataObject.getString(""));
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


