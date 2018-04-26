package com.easyschools.teacher;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

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

public class CreateHomeWork extends Fragment {

    private OnFragmentInteractionListener mListener;
    private byte[] data_file;
    private List<byte[]> files_bytes;
    private String path , subject_id , token , class_id;
    private Button create_btn;
    private EditText homework_name, description,
            deadline_value, score_value, weigth_value;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat sdf;
    private String dateStr = "";
    private Apis apis;
    private static String lang = "";
    private SharedPreferences settings;
    private LinearLayout add_questions;
    public static final int PICK_FILE = 1;
    private List<String> pathList;
    private CircleImageView school_logo ;
    private Context context ;


    public CreateHomeWork() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_home_work, container, false);
        apis = new Apis();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        if(getActivity() != null)
        {
            context = getActivity().getApplicationContext();
        }
        school_logo = v.findViewById(R.id.create_logo);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        lang = settings.getString("LANG", "");
        token = settings.getString("API_TOKEN", "");
        subject_id = settings.getString("SUBJECT_ID","");
        class_id = settings.getString("CLASS_ID","");
        files_bytes = new ArrayList<>();
        create_btn = v.findViewById(R.id.create_btn);
        homework_name = v.findViewById(R.id.homework_name);
        description = v.findViewById(R.id.description);
        deadline_value = v.findViewById(R.id.deadline_value);
        score_value = v.findViewById(R.id.score_value);
        weigth_value = v.findViewById(R.id.weigth_value);
        add_questions = v.findViewById(R.id.add_questions);
        pathList = new ArrayList<>();
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (files_bytes.size() == 0)
                    {
                        createHomeworkWithoutFiles(homework_name.getText().toString().trim(),
                                description.getText().toString().trim(),
                                weigth_value.getText().toString().trim(),
                                score_value.getText().toString().trim(),
                                dateStr);
                    }
                    else {
                        create_homeworkWithFile(homework_name.getText().toString().trim(),
                                description.getText().toString().trim(),
                                weigth_value.getText().toString().trim(),
                                score_value.getText().toString().trim(),
                                dateStr, files_bytes);
                    }
                }
            }
        });
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        sdf = new SimpleDateFormat("yyyy/MM/dd");
        deadline_value.setText(sdf.format(c.getTime()));
        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        deadline_value.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        dateStr = deadline_value.getText().toString();

                    }
                }, mYear, mMonth, mDay);
        deadline_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog.show();
            }
        });
        add_questions.setOnClickListener(new View.OnClickListener() {
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
                CreateHomeWork.this.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICK_FILE);

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

    public void create_homeworkWithFile(final String title, final String description,
                                        final String weight, final String score,
                                        final String deadline, final List<byte[]> path_List) {

        VolleyMultipartRequest multipartRequest = new
                VolleyMultipartRequest(apis.getUrl()+lang+"/homework/create/createhomework",
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                Log.d("RESPONSE", response.toString());
                                String resultResponse = new String(response.data);
                                Log.i("RESULT", resultResponse);
                                Toast.makeText(getActivity().getApplicationContext(),
                                        getResources().getString(R.string.home_created),
                                        Toast.LENGTH_SHORT).show();

                                if (getActivity() != null) {
                                    HomeWork homeWork = new HomeWork();
                                    FragmentTransaction transaction = getActivity().
                                            getSupportFragmentManager().beginTransaction().addToBackStack(null);
                                    transaction.replace(R.id.main_container, homeWork).commit();
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
                        params.put("name", title);
                        params.put("description", description);
                        params.put("score", score);
                        params.put("weight", weight);
                        params.put("deadline", deadline);
                        params.put("subject_id", subject_id);
                        params.put("class_id", class_id);
                        Log.d("DATA", params.toString());
                        return params;
                    }

                    @Override
                    protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        for (int k = 0; k < path_List.size(); k++) {
                            params.put("howeworkfile[" + k + "]",
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

    public void createHomeworkWithoutFiles(final String title, final String description,
                                           final String weight, final String score,
                                           final String deadline) {
        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl()+lang+"/homework/create/createhomework",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.home_created),
                                Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            HomeWork homeWork = new HomeWork();
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
                params.put("api_token", token);
                params.put("name", title);
                params.put("description", description);
                params.put("score", score);
                params.put("weight", weight);
                params.put("deadline", deadline);
                params.put("subject_id", subject_id);
                params.put("class_id", class_id);
                Log.d("PARAMS", params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(sr);
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

    private boolean validation()
    {
        if (homework_name.getText() == null || homework_name.getText().toString().isEmpty())
        {
            Toast.makeText(context,"Please enter homework name",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.getText() == null || description.getText().toString().isEmpty())
        {
            Toast.makeText(context,"Please enter homework description",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (weigth_value.getText() == null || weigth_value.getText().toString().isEmpty())
        {
            Toast.makeText(context,"Please enter homework weight",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (score_value.getText() == null || score_value.getText().toString().isEmpty())
        {
            Toast.makeText(context,"Please enter homework score",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (deadline_value.getText() == null || deadline_value.getText().toString().isEmpty())
        {
            Toast.makeText(context,"Please enter homework deadline",Toast.LENGTH_SHORT).show();
            return false;
        }
//        if ()
//        {
//            Toast.makeText(context,"Please enter homework name",Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

}

