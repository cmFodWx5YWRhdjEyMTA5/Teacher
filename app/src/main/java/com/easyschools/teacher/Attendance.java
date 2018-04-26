package com.easyschools.teacher;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Attendance extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> subjects_name, subjects_id, classes_id;
    private Spinner subjects;
    private Context context;
    private RecyclerView students_list;
    private List<StudentData> studentDataList, att_data_list;
    private EditText currentDateText;
    private String currentDate, dateStr, my_date = "";
    private String token = null;
    private String selected_subject_id = null;
    private String selected_class_id = null;
    private String dayStr = "";
    private Button submit;
    private List<String> students_ids;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private CircleImageView school_logo;
    private DatePickerDialog datePickerDialog;
    private RelativeLayout date_layout;
    private java.text.SimpleDateFormat sdf;
    private int dayOfWeek;
    Calendar c;

    public Attendance() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_attendance, container, false);
        apis = new Apis();
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        }
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        token = settings.getString("API_TOKEN", "");
        getSubject();
        school_logo = v.findViewById(R.id.a);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        subjects_id = new ArrayList<>();
        subjects_name = new ArrayList<>();
        classes_id = new ArrayList<>();
        att_data_list = new ArrayList<>();
        subjects = v.findViewById(R.id.attendance_subjects);
        date_layout = v.findViewById(R.id.date_layout);
        currentDateText = v.findViewById(R.id.current_date);
        currentDate = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd",
                new java.util.Date()));
        Log.d("DATE", currentDate);
        submit = v.findViewById(R.id.send_attendance);
        c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        sdf = new java.text.SimpleDateFormat("yyyy/MM/dd");
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        Log.d("NUMBER", String.valueOf(dayOfWeek));
        switch (dayOfWeek) {
            case 7:
                dayStr = "SATURDAY";
                break;
            case 1:
                dayStr = "SUNDAY";
                break;
            case 2:
                dayStr = "MONDAY";
                break;
            case 3:
                dayStr = "TUESDAY";
                break;
            case 4:
                dayStr = "WEDNESDAY";
                break;
            case 5:
                dayStr = "THURSDAY";
                break;
            case 6:
                dayStr = "FRIDAY";
                break;
        }
        currentDateText.setText(dayStr + " " + sdf.format(c.getTime()));
        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        c.set(year, monthOfYear, dayOfMonth);
                        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        switch (dayOfWeek) {
                            case 7:
                                dayStr = "SATURDAY";
                                break;
                            case 1:
                                dayStr = "SUNDAY";
                                break;
                            case 2:
                                dayStr = "MONDAY";
                                break;
                            case 3:
                                dayStr = "TUESDAY";
                                break;
                            case 4:
                                dayStr = "WEDNESDAY";
                                break;
                            case 5:
                                dayStr = "THURSDAY";
                                break;
                            case 6:
                                dayStr = "FRIDAY";
                                break;
                        }
                        currentDateText.setText(dayStr + " " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        dateStr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        Log.d("STR_DATE", dateStr);
                        getAttendance(dateStr, token,
                                subjects_id.get(subjects.getSelectedItemPosition()));
                    }
                }, mYear, mMonth, mDay);


        date_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                datePickerDialog.show();
                return false;
            }
        });

        currentDateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                datePickerDialog.show();
                return false;
            }
        });
        studentDataList = new ArrayList<>();
        students_list = v.findViewById(R.id.attendance_list);
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_subject_id = subjects_id.get(position);
                Log.d("SELECTED_ID", selected_subject_id);
                selected_class_id = classes_id.get(position);
                if (dateStr == null) {
                    my_date = currentDate;
                    Log.d("CURRENT", my_date);
                } else {
                    my_date = dateStr;
                    Log.d("SELECTED_DATE", my_date);
                }
                getAttendance(my_date, token,selected_subject_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (AttendanceAdapter.students_ids.size() != 0) {
                    if (dateStr == null) {
                        my_date = currentDate;
                        Log.d("CURRENT", my_date);
                    } else {
                        my_date = dateStr;
                        Log.d("SELECTED_DATE", my_date);
                    }
                    students_ids = AttendanceAdapter.students_ids;
                    if (att_data_list.size() == 0) {
                        Log.d("CREATE","OK");
                        createAttendance(my_date, token
                                , selected_subject_id, students_ids);
                    } else {
                        Log.d("UPDATE", "test");
                        editAttendance(my_date, token
                                , selected_subject_id, students_ids);
                    }
                }

            }
        });
        return v;
    }

    public void onButtonPressed(Uri uri) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                                JSONObject subjectsObject = dataObject.getJSONObject("subjects");
                                classes_id.add(dataObject.getString("class_id"));
                                subjects_id.add(subjectsObject.getString("id"));
                                subjects_name.add(subjectsObject.getString("subject_name"));
                            }
                            if (getActivity() != null) {
                                SubjectSpinner dataAdapter = new SubjectSpinner(context,
                                        android.R.layout.simple_spinner_item, subjects_name);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                subjects.setAdapter(dataAdapter);
//                                getAttendance(currentDate, token,
//                                        subjects_id.get(subjects.getSelectedItemPosition()));
//                                Log.d("Word", subjects_id.get(subjects.getSelectedItemPosition()));
                            }

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

    private void getALlStudentsOnClass(String class_id) {
        studentDataList.clear();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/teacher/TeacherStudentOnClass?class_id=" + class_id,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("STUDENTS", response.toString());
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                Log.d("DATASSSS", String.valueOf(dataObject));
                                if (dataObject.getJSONObject("users") != null) {
                                    JSONObject users = dataObject.getJSONObject("users");
                                    StudentData studentData = new StudentData();
                                    studentData.setId(users.getString("id"));
                                    studentData.setName(users.getString("name"));
                                    studentData.setAttend_true(false);
                                    studentDataList.add(studentData);
                                }
                            }
                            RecyclerView.Adapter attendanceAdapter =
                                    new AttendanceAdapter(studentDataList, context);
                            students_list.setAdapter(attendanceAdapter);
                            RecyclerView.LayoutManager layoutManager
                                    = new LinearLayoutManager(getContext());
                            students_list.setLayoutManager(layoutManager);

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

    private void createAttendance(final String date, final String api_token,
                                  final String subject_id, final List<String> ids) {
        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl() + lang + "/attendance/createattendance",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AttendanceAdapter.students_ids.clear();
                        students_ids.clear();
                        try {
                            JSONObject res = new JSONObject(response);
                            int scode = res.getInt("scode");
                            if (scode == 200) {
                                Log.d("ATT_RESPONSE", response);
                                Toast.makeText(context,
                                        "Attendance  is created", Toast.LENGTH_SHORT).show();
                                if (getActivity() != null) {
                                    HomePage homeWork = new HomePage();
                                    FragmentTransaction transaction = getActivity().
                                            getSupportFragmentManager().beginTransaction().addToBackStack(null);
                                    transaction.replace(R.id.main_container, homeWork).commit();
                                }
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

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", api_token);
                params.put("absence_date", date);
                params.put("subject_id", subject_id);
                for (int k = 0; k < ids.size(); k++) {
                    params.put("user_id[" + k + "]", ids.get(k));
                }
                Log.d("PARAMS", params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(sr);

    }

    private void getAttendance(final String date, final String api_token,
                               final String subject_id) {

        att_data_list.clear();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/attendance/getattendance?" +
                                "api_token=" + api_token + "&absence_date=" + date + "&subject_id=" + subject_id,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GET_DATA", response.toString());
                        try {
                            int scode = response.getInt("scode");
                            if (scode == 200) {
                                JSONArray dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataObject = dataArray.getJSONObject(i);
                                    if (dataObject.getJSONObject("users") != null) {
                                        JSONObject users = dataObject.getJSONObject("users");
                                        StudentData studentData = new StudentData();
                                        studentData.setId(users.getString("id"));
                                        studentData.setName(users.getString("name"));
                                        studentData.setAttend_true(true);
                                        att_data_list.add(studentData);
                                    }
                                }
                                RecyclerView.Adapter attendanceAdapter =
                                        new AttendanceAdapter(att_data_list, context);
                                students_list.setAdapter(attendanceAdapter);
                                RecyclerView.LayoutManager layoutManager
                                        = new LinearLayoutManager(getContext());
                                students_list.setLayoutManager(layoutManager);
                            } else {
                                Toast.makeText(context, getResources().getString(R.string.add_attenance_msg),
                                        Toast.LENGTH_SHORT).show();
                                getALlStudentsOnClass(selected_class_id);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_att", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void editAttendance(final String date, final String api_token,
                                final String subject_id, final List<String> ids) {
        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl() + lang + "/attendance/editattendance",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("UPDATED", response);
                        AttendanceAdapter.students_ids.clear();
                        students_ids.clear();
                        Toast.makeText(context,
                                getString(R.string.msg_att_update), Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            HomePage homeWork = new HomePage();
                            FragmentTransaction transaction = getActivity().
                                    getSupportFragmentManager().beginTransaction().addToBackStack(null);
                            transaction.replace(R.id.main_container, homeWork).commit();
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
                params.put("api_token", api_token);
                params.put("absence_date", date);
                params.put("subject_id", subject_id);
                for (int k = 0; k < ids.size(); k++) {
                    params.put("user_id[" + k + "]", ids.get(k));
                }
                Log.d("UPDATE_PARAMS", params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(sr);

    }

}
