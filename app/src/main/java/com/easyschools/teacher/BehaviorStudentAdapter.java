package com.easyschools.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BehaviorStudentAdapter extends RecyclerView.Adapter<BehaviorStudentAdapter.ViewHolder> {
    Context context, mcontext;
    List<StudentData> datas;
    private String data = "";
    private SharedPreferences settings;
    private List<BehaviorData> behaviorDataList;
    private List<BehaviorData> studentBehaviorList;
    private Typeface custom_font;
    private Apis apis;
    private static String lang = "";
    private String token , BEHDATA = "";
    private SharedPreferences.Editor editor;

    public BehaviorStudentAdapter(List<StudentData> studentDataList, Context context, Context mcontext) {
        super();
        this.datas = studentDataList;
        this.context = context;
        this.mcontext = mcontext;
        apis = new Apis();
        settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        data = settings.getString("BEHAVIOR", "");
        custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/thin.ttf");
        behaviorDataList = new ArrayList<>();
        studentBehaviorList = new ArrayList<>();
        lang = settings.getString("LANG", "");
        token = settings.getString("API_TOKEN", "");
        editor = settings.edit();
    }

    @Override
    public BehaviorStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_item_behavior, parent, false);
        BehaviorStudentAdapter.ViewHolder viewHolder = new BehaviorStudentAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BehaviorStudentAdapter.ViewHolder holder, int position) {

        StudentData studentData = datas.get(position);
        holder.name.setText(studentData.getName());
        holder.id.setText(studentData.getId());
        holder.name.setTypeface(custom_font);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStudentBehaviors(holder.id.getText().toString(),
                        holder.name.getText().toString());

                Log.d("WOIUOI", holder.id.getText().toString());
            }
        });
        holder.add_to_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behaviorDataList.clear();
                final Dialog dialog = new Dialog(mcontext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new
                        ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.behavior_dialog);
                RecyclerView behaviors_list = dialog.findViewById(R.id.behavior_list);
                Button cancel = dialog.findViewById(R.id.dismiss);
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        BehaviorData behaviorData = new BehaviorData();
                        behaviorData.setId(jsonObject.getString("id"));
                        behaviorData.setTitle(jsonObject.getString("title"));
                        behaviorData.setGrade(jsonObject.getString("grade"));
                        Log.d("ICON", "https://crm.easyschools.org/uploads/" + jsonObject.getString("icon"));
                        behaviorData.setIcon("https://crm.easyschools.org/uploads/" + jsonObject.getString("icon"));
                        behaviorDataList.add(behaviorData);
                    }
                    Log.d("DATA", String.valueOf(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RecyclerView.Adapter adapter = new BehaviorAdapter(behaviorDataList,
                        context, holder.id.getText().toString(),
                        holder.name.getText().toString());
                behaviors_list.setAdapter(adapter);
                RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                behaviors_list.setLayoutManager(layout);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                });
                dialog.show();

            }
        });
    }


    @Override
    public int getItemCount() {

        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, id;
        ImageButton add_to_student;

        public ViewHolder(View itemView) {
            super(itemView);
            add_to_student = itemView.findViewById(R.id.add_to_student);
            name = itemView.findViewById(R.id.behavior_student_name);
            id = itemView.findViewById(R.id.behavior_student_id);
        }
    }

    private void getStudentBehaviors(String id, final String name) {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/student/studentBehavior?" +
                                "api_token="+token+"&student_id="+id,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Student_behavior", response.toString());
                        try {
                            int scode = response.getInt("scode");
                            if (scode == 200) {
                                JSONArray data = response.getJSONArray("data");
                                Log.d("BE_DATA", String.valueOf(data));
                                editor.putString("BEHDATA", String.valueOf(data)).apply();
                                showDialog();
                            }
                            else
                            {
                                Toast.makeText(context,context.getResources().getString(R.string.no_behaviors)+
                                        " "+ name , Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.toString());
                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void showDialog() {
        studentBehaviorList.clear();
        int total_score =0 ;
        final Dialog dialog = new Dialog(mcontext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.student_behavior_data);
        RecyclerView behaviors_list = dialog.findViewById(R.id.student_behavior_list);
        Button cancel = dialog.findViewById(R.id.s);
        TextView total = dialog.findViewById(R.id.total);
        try {
            BEHDATA = settings.getString("BEHDATA","");
            JSONArray jsonArray = new JSONArray(BEHDATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BehaviorData behaviorData = new BehaviorData();
                behaviorData.setId(jsonObject.getString("id"));
                behaviorData.setTitle(jsonObject.getString("title"));
                behaviorData.setDegree(jsonObject.getString("grade"));
                behaviorData.setIcon("https://crm.easyschools.org/uploads/" + jsonObject.getString("icon"));
                JSONArray student_behavior = jsonObject.getJSONArray("student_behavior");
                for (int k = 0 ; k < student_behavior.length() ; k++)
                {
                    JSONObject student = student_behavior.getJSONObject(k);
                    behaviorData.setTotal_of_behavior(student.getString("total"));
                    total_score += student.getInt("total");
                    Log.d("SCORE_TOTAL", String.valueOf(total_score));
                }
                studentBehaviorList.add(behaviorData);
            }
            Log.d("DATA", String.valueOf(jsonArray));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecyclerView.Adapter adapter = new StudentDataBehaviorAdapter(studentBehaviorList, context);
        behaviors_list.setAdapter(adapter);
        total.setText(String.valueOf(total_score));
        behaviors_list.setLayoutManager(new GridLayoutManager(context, 3));
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                });
        dialog.show();

    }
}