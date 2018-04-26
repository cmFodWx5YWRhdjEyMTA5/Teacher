package com.easyschools.teacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

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


public class HomeWork extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context context;
    private ArrayList<String> subjects_name, subjects_id , classes_id;
    private Spinner subjects;
    private static String selected_subject_id, token, school_id , selected_class_id ,subject_id =  null;
    private RecyclerView homeworksRecyclerView;
    private List<HomeWorkData> homeWorkDatas;
    private HomeWorkAdapter test;
    public static RelativeLayout homework_relative_layout;
    private Button create_home ;
    private CircleImageView school_logo ;


    public HomeWork() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_work, container, false);
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        }
        apis = new Apis();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        editor = settings.edit();
        token = settings.getString("API_TOKEN", "");
        school_id = settings.getString("SCHOOL_ID" , "");
        lang = settings.getString("LANG", "");
        subjects = v.findViewById(R.id.subjects_homework);
        school_logo = v.findViewById(R.id.home_logo);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        subjects_id = new ArrayList<>();
        subjects_name = new ArrayList<>();
        classes_id = new ArrayList<>();
        getSubject();
        create_home = v.findViewById(R.id.create_homework);
        homeWorkDatas = new ArrayList<>();
        homework_relative_layout = v.findViewById(R.id.homework_relative_layout);
        homeworksRecyclerView = v.findViewById(R.id.homework_list);
        subjects_id = new ArrayList<>();
        subjects_name = new ArrayList<>();
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_subject_id = subjects_id.get(position);
                selected_class_id = classes_id.get(position);
                getAllHomework(selected_subject_id , selected_class_id);
                Log.d("SELECTED_SUBJECT_ID", selected_subject_id + " " + selected_class_id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        create_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("CLASS_ID", selected_class_id).apply();
                editor.putString("SUBJECT_ID", selected_subject_id).apply();
                CreateHomeWork homeWork = new CreateHomeWork();
                FragmentTransaction transaction = getActivity().
                        getSupportFragmentManager().beginTransaction().addToBackStack(null);
                transaction.replace(R.id.main_container, homeWork).commit();
            }
        });
        return v;
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
    private void getSubject() {
        Log.d("URL", apis.getUrl() + lang+"/teacher/TeacherSubjectClass?api_token="+token);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang+"/teacher/TeacherSubjectClass?api_token="+token,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SUBJECTRESPONSE", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                subject_id = dataObject.getString("subject_id");
                                JSONObject subjectsObject = dataObject.getJSONObject("subjects");
                                subjects_id.add(subjectsObject.getString("id"));
                                classes_id.add(dataObject.getString("class_id"));
                                subjects_name.add(subjectsObject.getString("subject_name"));
                            }
                            SubjectSpinner dataAdapter = new SubjectSpinner(context,
                                    android.R.layout.simple_spinner_item, subjects_name);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            subjects.setAdapter(dataAdapter);
//                            getAllHomework(String.valueOf(subjects_id.get(subjects.getSelectedItemPosition()+2)),
//                                    String.valueOf(classes_id.get(subjects.getSelectedItemPosition())));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_SUBJECTS", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    private void getAllHomework(String id , String class_id) {
        homeWorkDatas.clear();
        String url = apis.getUrl() + lang+"/homework/TeacherHomework?school_id="+
                school_id+"&subject_id="+id+"&api_token="+token+"&class_id="+class_id;
        Log.d("HOMEWORKURL", url);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("HOMEWORK", response.toString());
                        try {
                            int scode = response.getInt("scode");
                            if (scode == 200) {
                                homeworksRecyclerView.setVisibility(View.VISIBLE);
                                JSONArray homework_array = response.getJSONArray("data");
                                for (int i = 0; i < homework_array.length(); i++) {
                                    JSONObject homeworkJSONObject = homework_array.getJSONObject(i);
                                    String parent_home_work =
                                            homeworkJSONObject.getString("name");
                                    List<HomeWorkDataDetails> homeWorkDataDetails = new ArrayList<>();
                                    HomeWorkDataDetails homeWorkDetails = new HomeWorkDataDetails();
                                    if (homeworkJSONObject.getString("description") == null) {
                                        homeWorkDetails.setDescription("-");
                                    } else {
                                        homeWorkDetails.setDescription(homeworkJSONObject.getString("description"));
                                    }
                                    if (homeworkJSONObject.getString("score") == null ||
                                            homeworkJSONObject.getString("score").equals("null")) {
                                        homeWorkDetails.setScore("-");
                                    } else {
                                        homeWorkDetails.setScore(homeworkJSONObject.getString("score"));
                                    }
                                    if (homeworkJSONObject.getString("name") == null) {
                                        homeWorkDetails.setTitle("-");
                                    } else {
                                        homeWorkDetails.setTitle(homeworkJSONObject.getString("name"));
                                    }
                                    if (homeworkJSONObject.getString("deadline") == null) {
                                        homeWorkDetails.setDeadline("-");

                                    } else {
                                        homeWorkDetails.setDeadline(homeworkJSONObject.getString("deadline"));

                                    }
                                    if (homeworkJSONObject.getString("weight") == null) {
                                        homeWorkDetails.setWeight("-");

                                    } else {
                                        homeWorkDetails.setWeight(homeworkJSONObject.getString("weight"));

                                    }
                                    homeWorkDetails.setId(homeworkJSONObject.getString("id"));
                                    homeWorkDataDetails.add(homeWorkDetails);
                                    Log.d("HOMEWORKOBJECT", homeworkJSONObject.toString());
                                    editor.putString("FILES", homework_array.toString()).apply();
                                    homeWorkDatas.add(new HomeWorkData(parent_home_work,
                                            homeWorkDataDetails));
                                }
                                test = new HomeWorkAdapter(homeWorkDatas);
                                homeworksRecyclerView.setLayoutManager(new
                                        LinearLayoutManager(context));
                                homeworksRecyclerView.setAdapter(test);
                            }
                            else
                            {
                                Toast.makeText(context,"There is no homework for this subject"
                                        , Toast.LENGTH_SHORT).show();
                                homeworksRecyclerView.setVisibility(View.GONE);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("HOMEWORKERROR", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
