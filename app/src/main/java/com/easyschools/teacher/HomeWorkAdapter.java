package com.easyschools.teacher;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeWorkAdapter extends
        ExpandableRecyclerViewAdapter<HWParentViewHolder, HwChildViewHolder> {
    private Context context;
    private SharedPreferences settings;
    private List<String> filesExtensions;
    private List<String> imagesFiles;
    private List<String> docFiles;
    private List<String> pdfFiles;
    private List<String> filesUrl;
    private List<String> audioFiles;
    public static final int PICK_FILE = 1;
    private PopupWindow mPopupWindow;
    private ViewGroup parent = null;


    public HomeWorkAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public HWParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homework_item, parent, false);
        context = parent.getContext();
        this.parent = parent;
        settings = context.getSharedPreferences("DATA",
                Context.MODE_PRIVATE);
        this.parent = parent;
        filesExtensions = new ArrayList<>();
        imagesFiles = new ArrayList<>();
        docFiles = new ArrayList<>();
        pdfFiles = new ArrayList<>();
        filesUrl = new ArrayList<>();
        audioFiles = new ArrayList<>();
        return new HWParentViewHolder(view);
    }

    @Override
    public HwChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homework_details_item, parent, false);
        return new HwChildViewHolder(view);

    }

    @Override
    public void onBindChildViewHolder(final HwChildViewHolder holder, final int flatPosition,
                                      final ExpandableGroup group, final int childIndex) {

        HomeWorkDataDetails homeWorkDataDetails = (HomeWorkDataDetails)
                group.getItems().get(childIndex);
        holder.setDescription(homeWorkDataDetails.getDescription());
        holder.setTitle(homeWorkDataDetails.getTitle());
        holder.setDeadline(homeWorkDataDetails.getDeadline());
        holder.setId(homeWorkDataDetails.getId());
        holder.setScore(homeWorkDataDetails.getScore());
        holder.setWeight(homeWorkDataDetails.getWeight()+" %");
        holder.download_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFiles(holder.getId().getText().toString());
                if (!filesUrl.isEmpty()) {
                    Log.d("NOT_EMPTY", "DONE");
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.download_dialog, parent, false);
                    mPopupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    final ImageView pdf = customView.findViewById(R.id.pdf);
                    final ImageView doc = customView.findViewById(R.id.doc);
                    final ImageView pic = customView.findViewById(R.id.image);
                    final ImageView mp = customView.findViewById(R.id.mp4);
                    final TextView count = customView.findViewById(R.id.count);
                    if (docFiles.isEmpty()) {
                        doc.setClickable(false);
                        doc.setAlpha(0.2f);
                    }
                    if (imagesFiles.isEmpty()) {
                        pic.setClickable(false);
                        pic.setAlpha(0.2f);
                    }
                    if (pdfFiles.isEmpty()) {
                        pdf.setClickable(false);
                        pdf.setAlpha(0.2f);

                    }
                    if (audioFiles.isEmpty()) {
                        mp.setClickable(false);
                        mp.setAlpha(0.2f);
                    }

                    pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            count.setText(String.valueOf(pdfFiles.size()));
                            downloadFile(pdfFiles,
                                    holder.getTitle().getText().toString()
                            );
                        }
                    });
                    doc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            count.setText(String.valueOf(docFiles.size()));
                            downloadFile(docFiles,
                                    holder.getTitle().getText().toString()
                            );
                        }
                    });
                    pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            count.setText(String.valueOf(imagesFiles.size()));
                            downloadFile(imagesFiles,
                                    holder.getTitle().getText().toString()
                            );
                        }
                    });
                    mp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            count.setText(String.valueOf(audioFiles.size()));
                            downloadFile(audioFiles,
                                    holder.getTitle().getText().toString()
                            );
                        }
                    });
                    HomeWork.homework_relative_layout.setAlpha(0.2f);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setAnimationStyle(R.anim.slide_up);
                    mPopupWindow.showAtLocation(HomeWork.homework_relative_layout, Gravity.CENTER, 0, 0);
                    mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            HomeWork.homework_relative_layout.setAlpha(1f);
                            mp.setAlpha(1f);
                            pic.setAlpha(1f);
                            doc.setAlpha(1f);
                            pdf.setAlpha(1f);
                            docFiles.clear();
                            pdfFiles.clear();
                            imagesFiles.clear();
                            audioFiles.clear();
                            filesUrl.clear();
                        }
                    });

                } else {
                    Log.d("EMPTY", "NO");
                    Toast.makeText(context, context.getResources().getString(R.string.no_homework_files),
                            Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    @Override
    public void onBindGroupViewHolder(HWParentViewHolder holder, int flatPosition, ExpandableGroup group) {

        holder.setHomework_title(group.getTitle());
    }


    private void downloadFile(List<String> url, String title) {
        for (int i = 0; i < url.size(); i++) {
            String extension = url.get(i).substring(url.get(i).lastIndexOf("."));
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.get(i)));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle(title);
            request.setDescription("EasySchools");
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/HomeWork/" + title + "." +
                    extension);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            assert manager != null;
            manager.enqueue(request);

        }

    }

    private void getFiles(String homework_id) {
        String files_str = settings.getString("FILES", "");
        try {
            JSONArray homework_array = new JSONArray(files_str);
            for (int i = 0; i < homework_array.length(); i++) {
                JSONObject homework_object = homework_array.getJSONObject(i);
                String home_id = homework_object.getString("id");
                if (homework_id.equals(home_id)) {
                    JSONArray filesArray = homework_object.getJSONArray("files");
                    for (int m = 0; m < filesArray.length(); m++) {
                        JSONObject filesObject = filesArray.getJSONObject(m);
                        filesUrl.add(filesObject.getString("file"));
                        filesExtensions.add(filesObject.getString("extension"));
                        String extension = filesObject.getString("type");
                        if (extension.equals("doc")) {
                            docFiles.add(filesObject.getString("file"));
                            Log.d("DOC", String.valueOf(docFiles.size()));

                        }
                        if (extension.equals("pdf")) {
                            pdfFiles.add(filesObject.getString("file"));
                            Log.d("PDF", String.valueOf(pdfFiles.size()));

                        }
                        if (extension.equals("image")) {
                            imagesFiles.add(filesObject.getString("file"));
                            Log.d("IMAGES", String.valueOf(imagesFiles.size()));
                        }
                        if (extension.equals("video")) {
                            audioFiles.add(filesObject.getString("file"));
                            Log.d("VIDEO", String.valueOf(audioFiles.size()));
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


