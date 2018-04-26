package com.easyschools.teacher;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDataBehaviorAdapter extends RecyclerView.Adapter<StudentDataBehaviorAdapter.ViewHolder>
{
    private final Typeface custom_font;
    private final Context context;
    private List<BehaviorData> behaviorData ;


    public StudentDataBehaviorAdapter(List<BehaviorData> behaviorDataList , Context context)
    {
        super();
        this.context = context;
        this.behaviorData = behaviorDataList;
        custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/regular.ttf");
    }

    @Override
    public StudentDataBehaviorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_behavior_data_item, parent, false);
        return new StudentDataBehaviorAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StudentDataBehaviorAdapter.ViewHolder holder, int position)
    {
        BehaviorData behaviorData1 = behaviorData.get(position);
        holder.degree.setText(behaviorData1.getDegree());
        holder.title.setText(behaviorData1.getTitle());
        holder.id.setText(behaviorData1.getId());
        holder.total_behavior_degree.setText(behaviorData1.getTotal_of_behavior());
        Picasso.with(context).load(behaviorData1.getIcon()).
                into(holder.behavior_image);
        holder.title.setTypeface(custom_font);
        holder.degree.setTypeface(custom_font);
        holder.total_behavior_degree.setTypeface(custom_font);
    }

    @Override
    public int getItemCount() {
        return behaviorData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView  degree , title , id ,total_behavior_degree ;
        CircleImageView behavior_image;


        ViewHolder(View itemView)
        {
            super(itemView);
            degree = itemView.findViewById(R.id.degree);
            total_behavior_degree = itemView.findViewById(R.id.student_degree);
            title = itemView.findViewById(R.id.s_behavior_title);
            id = itemView.findViewById(R.id.s_behavior_id);
            behavior_image = itemView.findViewById(R.id.s_behavior_icon);

        }
    }
}
