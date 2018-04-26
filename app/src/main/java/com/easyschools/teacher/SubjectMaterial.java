package com.easyschools.teacher;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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


public class SubjectMaterial extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageButton left, right;
    private Typeface custom_font;
    private Apis apis;
    private static String lang = "";
    private RelativeLayout material_layout;
    SharedPreferences settings;
    private ArrayList<String> subjects_name, subjects_id;
    private Spinner subjects;
    private List<MaterialData> materialDatas;
    private TextView title, description, date,
            answers_questions, id, week_number;
    private ImageView pdf, image, audio, doc;
    private int count = 0;
    //private List<String> filesUrl;
    private List<String> imagesFiles;
    private List<String> docFiles;
    private List<String> pdfFiles;
    private List<String> audioFiles;
    private List<String> files_ex ;
    private  String token , selected_subject_id ;
    private Context context ;
    private Button add_material ;
    private CircleImageView school_logo ;


    public SubjectMaterial() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subject_material, container, false);
        materialDatas = new ArrayList<>();
        settings = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        apis = new Apis();
        token = settings.getString("API_TOKEN", "");
        left = v.findViewById(R.id.left_btn);
        right = v.findViewById(R.id.right_btn);
        if (getActivity() != null)
        {
            context = getActivity().getApplicationContext();
        }
        add_material = v.findViewById(R.id.add_material);
        add_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMaterial material = new AddMaterial();
                FragmentTransaction transaction = getActivity().
                        getSupportFragmentManager().beginTransaction().addToBackStack(null);
                transaction.replace(R.id.main_container, material).commit();
            }
        });
        subjects = v.findViewById(R.id.subjects_material);
        getSubject();
        school_logo = v.findViewById(R.id.material_logo);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_subject_id = subjects_id.get(position);
                getMaterial(selected_subject_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        files_ex = new ArrayList<>();
        subjects_id = new ArrayList<>();
        subjects_name = new ArrayList<>();
        title = v.findViewById(R.id.material_title);
        description = v.findViewById(R.id.material_description);
        date = v.findViewById(R.id.added_date);
        week_number = v.findViewById(R.id.week_number);
        answers_questions = v.findViewById(R.id.answer_questions_material);
        id = v.findViewById(R.id.id);
        imagesFiles = new ArrayList<>();
        docFiles = new ArrayList<>();
        pdfFiles = new ArrayList<>();
        audioFiles = new ArrayList<>();
        pdf = v.findViewById(R.id.pdf_material);
        doc = v.findViewById(R.id.doc_material);
        image = v.findViewById(R.id.image_material);
        audio = v.findViewById(R.id.audio_material);
        left.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {


                if (count >= 0  && count < materialDatas.size()) {
                    title.setText(materialDatas.get(count).getTitle());
                    date.setText(materialDatas.get(count).getDate());
                    description.setText(materialDatas.get(count).getDescription());
                    answers_questions.setText(materialDatas.get(count).getQuestions());
                    id.setText(materialDatas.get(count).getId());
                    setFiles(materialDatas.get(count).getFilesUrl(),
                            materialDatas.get(count).getFiles_extensions(),
                            materialDatas.get(count).getType());
                    week_number.setText(materialDatas.get(count).getWeek_number());
                    count--;
                    Log.d("LEFT_COUNT", String.valueOf(count));


                }


            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (count >= 0 && count < materialDatas.size()) {
                    Log.d("RIGHT_COUNT", String.valueOf(count));
                    title.setText(materialDatas.get(count).getTitle());
                    date.setText(materialDatas.get(count).getDate());
                    description.setText(materialDatas.get(count).getDescription());
                    answers_questions.setText(materialDatas.get(count).getQuestions());
                    id.setText(materialDatas.get(count).getId());
                    week_number.setText(materialDatas.get(count).getWeek_number());
                    setFiles(materialDatas.get(count).getFilesUrl(),
                            materialDatas.get(count).getFiles_extensions(),materialDatas.get(count).getType());

                    count++;
                    Log.d("before", String.valueOf(count));

                }

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
                                //getMaterial(String.valueOf(subjects_id.get(subjects.getSelectedItemPosition()+2)));
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

    private void getMaterial( String idStr ) {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, apis.getUrl()+lang+
                        "/material/getMaterialSubject?subject_id="+idStr+"&api_token="+token
                        , null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MATERIAL_RESPONSE", response.toString());
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                MaterialData materialData = new MaterialData();
                                List<String> filesUrl =new ArrayList<>();
                                List<String> filesExtensions = new ArrayList<>();
                                List<String> type = new ArrayList<>();
                                materialData.setDate(dataObject.getString("created_at"));
                                materialData.setDescription(dataObject.getString("description"));
                                materialData.setId(dataObject.getString("id"));
                                //materialData.setQuestions(dataObject.getString("q_link"));
                                materialData.setTitle(dataObject.getString("title"));
                                materialData.setWeek_number(dataObject.getString("week_num"));
                                JSONArray filesArray = dataObject.getJSONArray("material_file");
                                for (int m = 0; m < filesArray.length(); m++)
                                {
                                    JSONObject filesObject = filesArray.getJSONObject(m);
                                    filesUrl.add(filesObject.getString("file"));
                                    type.add(filesObject.getString("type"));
                                    filesExtensions.add(filesObject.getString("extension"));
                                    materialData.setFilesUrl(filesUrl);
                                    materialData.setFiles_extensions(filesExtensions);
                                    materialData.setType(type);
                                }
                                materialDatas.add(materialData);
                            }
                            //if (materialDatas.size() != 0 || materialDatas.size()) {
                                title.setText(materialDatas.get(count).getTitle());
                                date.setText(materialDatas.get(count).getDate());
                                description.setText(materialDatas.get(count).getDescription());
                                answers_questions.setText(materialDatas.get(count).getQuestions());
                                id.setText(materialDatas.get(count).getId());
                                week_number.setText(materialDatas.get(count).getWeek_number());
                                setFiles(materialDatas.get(count).getFilesUrl(),
                                        materialDatas.get(count).getFiles_extensions(),
                                        materialDatas.get(count).getType()
                                );

                                Log.d("MATERIAL", String.valueOf(materialDatas.size()));
                            //}
//                            Log.d("W", materialDatas.get(count).getFilesUrl().get(0));
//                            Log.d("W", materialDatas.get(count).getFilesUrl().get(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_MATERIAL", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public void setFiles(List<String> files_urls_list ,
                         List<String> files_Extensions
            ,  List<String> files_type) {
        this.files_ex = files_Extensions;
        docFiles.clear();
        pdfFiles.clear();
        imagesFiles.clear();
        audioFiles.clear();
        for (int m = 0; m < files_urls_list.size(); m++) {
            if (files_type.get(m).equals("doc")) {
                docFiles.add(files_urls_list.get(m));
                Log.d("DOC", String.valueOf(docFiles.size()));

            }
            if (files_type.get(m).equals("pdf")) {
                pdfFiles.add(files_urls_list.get(m));
                Log.d("PDF", String.valueOf(pdfFiles.size()));

            }
            if (files_type.get(m).equals("image")) {
                imagesFiles.add(files_urls_list.get(m));
                Log.d("IMAGES", String.valueOf(imagesFiles.size()));
            }
            if (files_type.get(m).equals("video")) {
                audioFiles.add(files_urls_list.get(m));
                Log.d("VIDEO", String.valueOf(audioFiles.size()));
            }

        }
        if (docFiles.isEmpty()) {
            doc.setClickable(false);
            doc.setAlpha(0.2f);
        }
        if (imagesFiles.isEmpty()) {
            image.setClickable(false);
            image.setAlpha(0.2f);
        }
        if (pdfFiles.isEmpty()) {
            pdf.setClickable(false);
            pdf.setAlpha(0.2f);

        }
        if (audioFiles.isEmpty()) {
            audio.setClickable(false);
            audio.setAlpha(0.2f);
        }

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(pdfFiles);
            }
        });
        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(docFiles);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(imagesFiles);
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(audioFiles);
            }
        });

    }
    private void downloadFile(List<String> url) {
        for (int i = 0; i < url.size(); i++) {
            String extension = url.get(i).substring(url.get(i).lastIndexOf("."));
            Log.d("URLS", url.get(i) + " " + extension);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.get(i)));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle("Subject Material files ");
            request.setDescription("EasySchools");
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Material/" + "file" + "." +
                    extension);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            assert manager != null;
            manager.enqueue(request);
        }

    }
}
