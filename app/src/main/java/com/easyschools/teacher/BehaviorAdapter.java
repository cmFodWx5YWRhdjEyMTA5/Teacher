package com.easyschools.teacher;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BehaviorAdapter  extends RecyclerView.Adapter<BehaviorAdapter.ViewHolder>
{
    private final Context context;
    private List<BehaviorData> behaviorData ;
    private String student_id = "" ;
    private String student_name = "" ;
    private Apis apis ;
    private SharedPreferences settings;
    private String lang = "";
    private Typeface custom_font;


    public BehaviorAdapter(List<BehaviorData> behaviorDataList ,
                           Context context , String student_id , String student_name)
    {
        super();
        this.context = context;
        this.behaviorData = behaviorDataList;
        this.student_id = student_id;
        this.student_name =  student_name;
        apis = new Apis();
        settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/thin.ttf");

    }

    @Override
    public BehaviorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.behavior_item, parent, false);
        return new BehaviorAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BehaviorAdapter.ViewHolder holder, int position)
    {
        BehaviorData behaviorData1 = behaviorData.get(position);
        holder.title.setText(behaviorData1.getTitle());
        holder.title.setTypeface(custom_font);
        holder.id.setText(behaviorData1.getId());
        holder.total_behavior_degree.setText(behaviorData1.getGrade());
        Picasso.with(context).load(behaviorData1.getIcon()).
                into(holder.behavior_image);
        holder.behavior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendBehavior(student_id , holder.id.getText().toString() ,
                        holder.total_behavior_degree.getText().toString());
            }
        });


    }

    @Override
    public int getItemCount() {
        return behaviorData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView   title , id ,total_behavior_degree ;
        CircleImageView behavior_image;
        RelativeLayout behavior ;


        ViewHolder(View itemView)
        {
            super(itemView);
            total_behavior_degree = itemView.findViewById(R.id.total_degree);
            title = itemView.findViewById(R.id.behavior_title);
            id = itemView.findViewById(R.id.behavior_id);
            behavior_image = itemView.findViewById(R.id.behavior_icon);
            behavior = itemView.findViewById(R.id.behavior_layout);

        }
    }

    private void sendBehavior(final String student_id  ,
                              final String behavior_id , final String degree)
    {

        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl()+lang+"/behavior/addStudentDegree",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        Toast.makeText(context ,   context.getResources().getString(R.string.behavior_added)+" "+ student_name,
                                Toast.LENGTH_SHORT).show();

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
                params.put("user_id", student_id);
                params.put("behavior_id", behavior_id);
                params.put("degree", degree);
                Log.d("PARAMS", params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(sr);

    }
}
