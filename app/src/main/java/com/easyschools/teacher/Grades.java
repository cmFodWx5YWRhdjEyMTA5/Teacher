package com.easyschools.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Grades extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> subjects_name, subjects_id, classes_id;
    private Spinner subjects;
    private String selected_subject_id, selected_class_id, token;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Context context;
    private GradesAdapter gradesAdapter;
    private  List<GradesData> gradesDataList;
    private RecyclerView gradesRecycleView;
    private Button add_new;
    public  static Button submit;
    private ArrayList<String> type;
    private static String type_selected = "";
    private static List list;
    private CircleImageView school_logo;
    private static List<String> ids, scores, add_grades;
    private Dialog dialog ;

    public Grades() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grades, container, false);
        apis = new Apis();
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        }
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_grade_dialog);
        add_new = v.findViewById(R.id.add_new_grade);
        submit = v.findViewById(R.id.submit);
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        editor = settings.edit();
        lang = settings.getString("LANG", "");
        token = settings.getString("API_TOKEN", "");
        subjects = v.findViewById(R.id.grades_subjects);
        list = new ArrayList();
        subjects_id = new ArrayList<>();
        subjects_name = new ArrayList<>();
        classes_id = new ArrayList<>();
        type = new ArrayList<>();
        school_logo = v.findViewById(R.id.g);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        type.add("Homework");
        type.add("Quiz");
        type.add("Project");
        type.add("Inclass task");
        type.add("Midterm");
        type.add("Final");
        type.add("Other");
        getSubject();
        gradesRecycleView = v.findViewById(R.id.grades_list);
        gradesDataList = new ArrayList<>();
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAllGrades(String.valueOf(classes_id.get(position)),
                        String.valueOf(subjects_id.get(position)));
                selected_class_id = classes_id.get(position);
                selected_subject_id = subjects_id.get(position);
                Log.d("SELECTED", String.valueOf(position));
                submit.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GradesAdapter.ids.size() != 0 && GradesAdapter.grades_data.size()
                        != 0 && GradesAdapter.add_grades.size() != 0) {
                    ids = GradesAdapter.ids;
                    add_grades = GradesAdapter.add_grades;
                    scores = GradesAdapter.grades_data;
                    Log.d("IDS", String.valueOf(ids.size()) +
                            " " + scores.size() + " " + add_grades.size());
                    submit();
                }
            }
        });
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

    private void getSubject() {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/teacher/TeacherSubjectClass?" +
                                "api_token=" + token,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SUBJECTRESPONSE", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                classes_id.add(dataObject.getString("class_id"));
                                JSONObject subjectsObject = dataObject.getJSONObject("subjects");
                                subjects_id.add(subjectsObject.getString("id"));
                                subjects_name.add(subjectsObject.getString("subject_name"));
                            }
                            SubjectSpinner dataAdapter = new SubjectSpinner(context,
                                    android.R.layout.simple_spinner_item, subjects_name);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            subjects.setAdapter(dataAdapter);
                            //Log.d("CLASS",String.valueOf(subjects.getSelectedItemPosition()));
                            //getAllGrades(String.valueOf(classes_id.get(subjects.getSelectedItemPosition())),
                            // String.valueOf(subjects_id.get(subjects.getSelectedItemPosition())));
                            //selected_class_id = classes_id.get(subjects.getSelectedItemPosition());
                            // selected_subject_id = subjects_id.get(subjects.getSelectedItemPosition());
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

    private void getAllGrades(String class_id, String subject_id) {
        gradesDataList.clear();
        String url = apis.getUrl() + lang + "/teacher/TeacherStudentGrades?class_id=" + class_id + "&subject_id=" + subject_id;
        Log.d("URL", url);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GRADE", response.toString());
                        try {
                            JSONArray gradeArray = response.getJSONArray("data");
                            for (int i = 0; i < gradeArray.length(); i++) {
                                JSONObject gradeArrayJSONObject = gradeArray.getJSONObject(i);
                                String parent_grade =
                                        gradeArrayJSONObject.getString("grade_name");
                                String total_score =
                                        gradeArrayJSONObject.getString("total_score");
                                Log.d("DATA", parent_grade + " " + total_score);
                                JSONArray user = gradeArrayJSONObject.getJSONArray("students");
                                Log.d("STUDENTS", String.valueOf(user));
                                List<GradesDataDetails> gradesDataDetails = new ArrayList<>();
                                for (int a = 0; a < user.length(); a++) {
                                    GradesDataDetails dataDetails = new GradesDataDetails();
                                    if (user.length() != 0) {
                                        JSONObject student_data = user.getJSONObject(a);
                                        JSONObject userObject = student_data.getJSONObject("user");
                                        dataDetails.setName(userObject.getString("name"));
                                        dataDetails.setId(userObject.getString("id"));
                                        dataDetails.setScore(student_data.getString("total_score"));
                                        dataDetails.setAddgrade_id(student_data.getString("addgrade_id"));
                                        gradesDataDetails.add(dataDetails);
                                    }
                                }
                                gradesDataList.add(new GradesData(parent_grade + "+" + total_score, gradesDataDetails));
                            }
                            gradesAdapter = new GradesAdapter(gradesDataList);
                            gradesRecycleView.setLayoutManager(new
                                    LinearLayoutManager(context));
                            gradesRecycleView.setAdapter(gradesAdapter);
                            Log.d("LIST" , String.valueOf(gradesDataList.size()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("GRADE_ERROR", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void showDialog() {
        ImageButton ok = dialog.findViewById(R.id.done);
        final ImageButton cancel = dialog.findViewById(R.id.cancel);
        final EditText title = dialog.findViewById(R.id.add_grade_title);
        final EditText score = dialog.findViewById(R.id.add_grade_score);
        final EditText weigth = dialog.findViewById(R.id.weigth);
        final Spinner type_spinner = dialog.findViewById(R.id.type);
        SubjectSpinner dataAdapter = new SubjectSpinner(context,
                android.R.layout.simple_spinner_item, type);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(dataAdapter);
        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type_selected = type.get(position);
                Log.d("TYPE", type_selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText() == null || title.getText().toString().isEmpty()) {
                    title.setText(getResources().getString(R.string.grade_name_validate));
                }
                if (score.getText() == null || score.getText().toString().isEmpty()) {
                    score.setText(getResources().getString(R.string.grade_score_validate));
                }
                if (weigth.getText() == null || weigth.getText().toString().isEmpty()) {
                    weigth.setText(getResources().getString(R.string.grade_weight_validate));
                }
                else {
                    add_new_grade(title.getText().toString().trim(),
                            score.getText().toString().trim(),
                            weigth.getText().toString().trim(), type_selected);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    public void add_new_grade(final String title, final String score,
                              final String weight, final String type) {
        Log.d("GRADE",apis.getUrl() + lang + "/student/grades/create?grade_name="+title.toString()+"&total_score="+score.toString()+
                "&weight="+weight.toString()+"&type="+type.toString()+"&class_id="+selected_class_id.toString()+"&subject_id="+selected_subject_id.toString());
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/student/grades/create?grade_name="+title.toString()+"&total_score="+score.toString()+
                                "&weight="+weight.toString()+"&type="+type.toString()+"&class_id="+selected_class_id.toString()+"&subject_id="+selected_subject_id.toString(),
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CREATED", response.toString());
                        try {
                            int scode = response.getInt("scode");
                            if (scode == 200) {
                                dialog.dismiss();
                                Toast.makeText(context,"Grade is created",Toast.LENGTH_SHORT).show();
                                JSONArray gradeArray = response.getJSONArray("data");
                                for (int i = 0; i < gradeArray.length(); i++) {
                                    JSONObject gradeArrayJSONObject = gradeArray.getJSONObject(i);
                                    String parent_grade =
                                            gradeArrayJSONObject.getString("grade_name");
                                    String total_score =
                                            gradeArrayJSONObject.getString("total_score");
                                    Log.d("DATA", parent_grade + " " + total_score);
                                    JSONArray user = gradeArrayJSONObject.getJSONArray("students");
                                    Log.d("STUDENTS", String.valueOf(user));
                                    List<GradesDataDetails> gradesDataDetails = new ArrayList<>();
                                    for (int a = 0; a < user.length(); a++) {
                                        GradesDataDetails dataDetails = new GradesDataDetails();
                                        if (user.length() != 0) {
                                            JSONObject student_data = user.getJSONObject(a);
                                            JSONObject userObject = student_data.getJSONObject("user");
                                            dataDetails.setName(userObject.getString("name"));
                                            dataDetails.setId(userObject.getString("id"));
                                            dataDetails.setScore(student_data.getString("total_score"));
                                            dataDetails.setAddgrade_id(student_data.getString("addgrade_id"));
                                            gradesDataDetails.add(dataDetails);
                                        }
                                    }
                                    gradesDataList.add(new GradesData(parent_grade + "+" + total_score, gradesDataDetails));
                                }
                                gradesAdapter = new GradesAdapter(gradesDataList);
                                gradesRecycleView.setLayoutManager(new
                                        LinearLayoutManager(context));
                                gradesRecycleView.setAdapter(gradesAdapter);

                                Log.d("LIST" , String.valueOf(gradesDataList.size()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("ERROR", error.toString());
                    }

                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void submit() {
        list.clear();
        for (int i = 0; i < ids.size(); i++) {
            HashMap<String, String> mydata = new HashMap<>();
            mydata.put("user_id", ids.get(i));
            mydata.put("total_score", scores.get(i));
            mydata.put("addgrade_id", add_grades.get(i));
            Log.d("SUMBIT", String.valueOf(mydata.size()));
            list.add(mydata);
        }
        HashMap grades = new HashMap();
        grades.put("grades", list);
        final JSONObject jsonObject = new JSONObject(grades);
        Log.d("LIST", jsonObject.toString());
        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl() + lang + "/teacher/TeacherStudentEditGrade",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        Toast.makeText(context, getResources().getString(R.string.grade_added),
                                Toast.LENGTH_SHORT).show();
                        ids.clear();
                        scores.clear();
                        add_grades.clear();
                        GradesAdapter.ids.clear();
                        GradesAdapter.add_grades.clear();
                        GradesAdapter.grades_data.clear();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("ERROR", error.toString());
            }

        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap map = new HashMap();
                return jsonObject.toString().getBytes();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                Log.d("PARAMS", params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(sr);

    }

}
