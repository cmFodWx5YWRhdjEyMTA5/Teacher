package com.easyschools.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
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

import com.android.volley.NetworkResponse;
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

import static android.app.Activity.RESULT_OK;


public class Behavior extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> subjects_name, subjects_id,classes_id;
    private Spinner subjects;
    private Context context, mContext;
    private RecyclerView students_list;
    private List<StudentData> studentDataList;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Button create_behavior;
    private static int RESULT_LOAD_IMG = 1;
    private String token , selected_class_id , selected_subject_id , school_id ;
    String imgDecodableString;
    Dialog dialog;
    private List<String> weights  , negative_weights;
    CircleImageView icon;
    private String selected_weights ;
    private CircleImageView school_logo;
    public Behavior() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_behavior, container, false);
        apis = new Apis();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        editor = settings.edit();
        token = settings.getString("API_TOKEN", "");
        school_id = settings.getString("SCHOOL_ID", "");
        school_logo = v.findViewById(R.id.b);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        subjects = v.findViewById(R.id.behavior_subjects);
        create_behavior = v.findViewById(R.id.create_behavior);
        subjects_id = new ArrayList<>();
        subjects_name = new ArrayList<>();
        classes_id = new ArrayList<>();
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
            mContext = this.getActivity();
        }
        getSubject();
        studentDataList = new ArrayList<>();
        students_list = v.findViewById(R.id.students_behavior_list);
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.create_behavior_dialog);
        icon = dialog.findViewById(R.id.icon_behavior);
        create_behavior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_class_id = classes_id.get(position);
                selected_subject_id = subjects_id.get(position);
                getALlStudentsOnClass(selected_class_id);
                getBehaviors(selected_class_id, selected_subject_id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }
    private void getSubject() {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/teacher/TeacherSubjectClass?" +
                                "api_token="+token,
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
                            //getALlStudentsOnClass(classes_id.get(subjects.getSelectedItemPosition()+2));
                           // getBehaviors(classes_id.get(subjects.getSelectedItemPosition()+2),
                                //    subjects_id.get(subjects.getSelectedItemPosition()+2));
                            //selected_class_id = classes_id.get(subjects.getSelectedItemPosition()+2);
                            //selected_subject_id = subjects_id.get(subjects.getSelectedItemPosition()+2);

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
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void getALlStudentsOnClass(String class_id) {
        studentDataList.clear();
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/teacher/TeacherStudentOnClass?class_id="+class_id,
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
                                    studentDataList.add(studentData);
                                }
                            }
                            RecyclerView.Adapter behaviorStudentAdapter =
                                    new BehaviorStudentAdapter(studentDataList, context, mContext);
                            students_list.setAdapter(behaviorStudentAdapter);
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
    private void getBehaviors(String class_id , String subject_id) {
        Log.d("URL",apis.getUrl() + lang +
                "/behavior/getAllBehaviorForClassSubject?class_id="+class_id+"&subject_id="+subject_id);
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang +
                                "/behavior/getAllBehaviorForClassSubject?class_id="+class_id+"&subject_id="+subject_id,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("BEHAVIOR", response.toString());
                        try {
                            int scode = response.getInt("scode");
                            if (scode == 200) {
                                JSONArray dataArray = response.getJSONArray("data");
                                editor.putString("BEHAVIOR", String.valueOf(dataArray)).apply();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_BEHAVIOR", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();
                Log.d("dhkf", String.valueOf(selectedImage));
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                icon.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
                Log.d("USER_IMAGE", imgDecodableString);

            } else {
                Log.d("wrong", "hjhkfhd");

            }
        } catch (Exception e) {
            Log.d("exc", e.toString());
        }

    }
    public void showDialog() {
        Button ok = dialog.findViewById(R.id.submit_behavior);
        Button cancel = dialog.findViewById(R.id.cancel_behavior);
        final EditText title = dialog.findViewById(R.id.title_behavior);
        final ImageButton positive = dialog.findViewById(R.id.positive);
        final ImageButton negative = dialog.findViewById(R.id.negative);
        final Spinner grade = dialog.findViewById(R.id.weight_behavior);
        weights = new ArrayList<>();
        weights.add("1");
        weights.add("2");
        weights.add("3");
        weights.add("4");
        weights.add("5");
        negative_weights = new ArrayList<>();
        negative_weights.add("-1");
        negative_weights.add("-2");
        negative_weights.add("-3");
        negative_weights.add("-4");
        negative_weights.add("-5");
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectSpinner dataAdapter = new SubjectSpinner(context,
                        android.R.layout.simple_spinner_item, weights);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                grade.setAdapter(dataAdapter);
                grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selected_weights = weights.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectSpinner dataAdapter = new SubjectSpinner(context,
                        android.R.layout.simple_spinner_item, negative_weights);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                grade.setAdapter(dataAdapter);
                grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selected_weights = negative_weights.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("URL",apis.getUrl() + lang + "/behavior/createBehavior");
                VolleyMultipartRequest multipartRequest = new
                        VolleyMultipartRequest(apis.getUrl() + lang + "/behavior/createBehavior",
                                new Response.Listener<NetworkResponse>() {
                                    @Override
                                    public void onResponse(NetworkResponse response) {
                                        Log.d("RESPONSE", response.toString());
                                        String resultResponse = new String(response.data);
                                        Log.i("RESULT", resultResponse);
                                        try {
                                            JSONObject  data = new JSONObject(resultResponse);
                                            int scode = data.getInt("scode");
                                            if (scode == 200)
                                            {
                                                dialog.dismiss();
                                                Toast.makeText(context,
                                                        getResources().getString(R.string.behavior_created),
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("VOLLEYERROR", error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("class_id", selected_class_id);
                                params.put("subject_id", selected_subject_id);
                                params.put("title", title.getText().toString().trim());
                                params.put("type", "1");
                                params.put("school_id", school_id);
                                params.put("grade" ,selected_weights);
                                Log.d("ADD_NEW_BEHAVIOR", params.toString());
                                return params;
                            }

                            @Override
                            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                                Map<String, DataPart> params = new HashMap<>();
                                params.put("icon", new VolleyMultipartRequest.DataPart(imgDecodableString,
                                        AppHelper.getFileDataFromDrawable(context, icon.getDrawable())));
                                Log.d("PARAMS", params.toString());
                                return params;
                            }
                        };

                VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
