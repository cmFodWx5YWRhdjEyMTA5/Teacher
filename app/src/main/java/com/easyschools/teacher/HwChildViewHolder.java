package com.easyschools.teacher;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;



public class HwChildViewHolder extends ChildViewHolder
{
    TextView description  , title , deadline , id , score , weight   ;
    RelativeLayout download_layout ;


    public HwChildViewHolder(View itemView)
    {
        super(itemView);
        description =  itemView.findViewById(R.id.homework_desc);
        title = itemView.findViewById(R.id.homework_title);
        deadline = itemView.findViewById(R.id.deadline);
        id = itemView.findViewById(R.id.id);
        score = itemView.findViewById(R.id.get_score);
        weight = itemView.findViewById(R.id.get_weight_value);
        download_layout = itemView.findViewById(R.id.download_files_homework);
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description.setText(desc);
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(String title_str) {
        title.setText(title_str);
    }

    public TextView getDeadline() {
        return deadline;
    }

    public void setDeadline(String date) {
        deadline.setText(date);
    }

    public TextView getId() {
        return id;
    }

    public void setId(String homework_id) {
        id.setText(homework_id);
    }

    public TextView getScore() {
        return score;
    }

    public void setScore(String scoreStr ) {
        score.setText(scoreStr);
    }

    public TextView getWeight() {
        return weight;
    }

    public void setWeight(String weightStr) {
        weight.setText(weightStr);
    }
}
