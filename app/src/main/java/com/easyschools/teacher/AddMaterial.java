package com.easyschools.teacher;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddMaterial extends Fragment {

    private Typeface custom_font;
    private OnFragmentInteractionListener mListener;
    private ArrayList<String> subjects_name, subjects_id;
    private Spinner subjects, weeks;
    private List<String> weeks_list ;
    private static String selected_week = "";
    private Context context;
    private Button create_material_btn;
    private byte[] data_file;
    private List<byte[]> files_bytes;
    private List<String> pathList;
    private String path;
    private ImageButton upload_files;
    public static final int PICK_FILE = 1;
    private EditText material_title, description;
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private  String token , selected_subject_id ;
    private CircleImageView school_logo ;
    public AddMaterial() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_material, container, false);
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        }
        apis = new Apis();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        subjects_id = new ArrayList<>();
        subjects_name = new ArrayList<>();
        weeks_list = new ArrayList();
        pathList = new ArrayList<>();
        files_bytes = new ArrayList<>();
        school_logo = v.findViewById(R.id.m);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        weeks_list.add("Week 1");
        weeks_list.add("Week 2");
        weeks_list.add("Week 3");
        weeks_list.add("Week 4");
        weeks_list.add("Week 5");
        weeks_list.add("Week 6");
        weeks_list.add("Week 7");
        token = settings.getString("API_TOKEN", "");
        upload_files = v.findViewById(R.id.upload_material_files);
        material_title = v.findViewById(R.id.material_title);
        description = v.findViewById(R.id.material_description);
        upload_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mimeTypes =
                        {"image/*", "application/*|text/*"};
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    StringBuilder mimeTypesStr = new StringBuilder();
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr.append(mimeType).append("|");
                    }
                    intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
                AddMaterial.this.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICK_FILE);
            }
        });
        subjects = v.findViewById(R.id.material_create_subjects);
        weeks = v.findViewById(R.id.week_spinner);
        SubjectSpinner dataAdapter = new SubjectSpinner(context,
                android.R.layout.simple_spinner_item, weeks_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weeks.setAdapter(dataAdapter);
        weeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_week = weeks_list.get(position);
                Log.d("WEEK", selected_week);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        create_material_btn = v.findViewById(R.id.create_material);
        create_material_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_material(material_title.getText().toString().trim(), description.getText().toString().trim()
                 , files_bytes);
            }
        });
        getSubject();
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_subject_id = subjects_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v ;
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
                        apis.getUrl()+lang+"/teacher/TeacherSubjectClass?" +
                                "api_token="+token,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SUBJECTRESPONSE", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                JSONObject subjectsObject = dataObject.getJSONObject("subjects");
                                subjects_id.add(subjectsObject.getString("id"));
                                subjects_name.add(subjectsObject.getString("subject_name"));
                            }
                            if (getActivity() != null) {
                                SubjectSpinner dataAdapter = new SubjectSpinner(context,
                                        android.R.layout.simple_spinner_item, subjects_name);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                subjects.setAdapter(dataAdapter);
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
    private void create_material(final String title, final String description, final List<byte[]> path_List)
    {
        VolleyMultipartRequest multipartRequest = new
                VolleyMultipartRequest(apis.getUrl()+lang+"/material/createMaterial",
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                Log.d("ADD_MATERIAL", response.toString());
                                String resultResponse = new String(response.data);
                                Log.i("RESULT", resultResponse);
                                Toast.makeText(getActivity().getApplicationContext(),
                                        getResources().getString(R.string.material_created),
                                        Toast.LENGTH_SHORT).show();

                                if (getActivity() != null) {
                                    SubjectMaterial subjectMaterial = new SubjectMaterial();
                                    FragmentTransaction transaction = getActivity().
                                            getSupportFragmentManager().beginTransaction().addToBackStack(null);
                                    transaction.replace(R.id.main_container, subjectMaterial).commit();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d("VOLLEYERROR", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("api_token", token);
                        params.put("title", title);
                        params.put("description", description);
                        params.put("week_num", selected_week);
                        params.put("subject_id", selected_subject_id);
                        Log.d("DATA", params.toString());
                        return params;
                    }

                    @Override
                    protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        for (int k = 0; k < path_List.size(); k++) {
                            params.put("file[" + k + "]",
                                    new VolleyMultipartRequest.DataPart(
                                            "file", path_List.get(k)));
                            Log.d("FILE", String.valueOf(path_List.get(k)));
                        }
                        Log.d("PARAMS", params.toString());
                        return params;
                    }
                };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Log.d("ssss", "sss");
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                if (clipData.getItemCount() > 1) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        try {
                            Uri uri = clipData.getItemAt(i).getUri();
                            path = PathUtil.getPath(getContext(), uri);
                            data_file = readBytesFromFile(path);
                            files_bytes.add(data_file);
                            pathList.add(path);
                            Log.d("PATH", path);
                            Log.d("URIS", uri.toString());
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    if (pathList != null) {
                        Log.d("OK", String.valueOf(files_bytes.size()));
                    }
                }
            }else {
                    try {
                        Uri uri = data.getData();
                        data_file = readBytesFromFile(PathUtil.getPath(getContext(), uri));
                        Log.d("ONE_FILE", String.valueOf(data_file));
                        files_bytes.add(data_file);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    private static byte[] readBytesFromFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }
}
