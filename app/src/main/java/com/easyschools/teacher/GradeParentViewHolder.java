package com.easyschools.teacher;


import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class GradeParentViewHolder  extends GroupViewHolder
{
    private  TextView grade_title ;
    private TextView total_score;
    public GradeParentViewHolder(View itemView) {
        super(itemView);
        grade_title = itemView.findViewById(R.id.name);
        total_score = itemView.findViewById(R.id.total_score);
    }


    public TextView getGrade_title() {
        return grade_title;
    }

    public void setGrade_title(String grade_titleStr) {
        grade_title.setText(grade_titleStr);
    }

    public TextView getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_scoreStr) {
       total_score.setText(total_scoreStr);
    }
}

