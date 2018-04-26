package com.easyschools.teacher;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class GradeChildViewHolder  extends ChildViewHolder
{

    TextView name , id , add_grade;
    EditText score ;

    public GradeChildViewHolder(View itemView)
    {
        super(itemView);
        id = itemView.findViewById(R.id.id);
        name = itemView.findViewById(R.id.student_grade_name);
        score = itemView.findViewById(R.id.student_score);
        add_grade = itemView.findViewById(R.id.add_grade);
    }

    public TextView getName() {
        return name;
    }

    public void setName(String nameStr) {
        name.setText(nameStr);
    }

    public EditText getScore() {
        return score;
    }

    public void setScore(String scoreStr) {
        score.setText(scoreStr);
    }

    public TextView getId() {
        return id;
    }

    public void setId(String idStr) {
       id.setText(idStr);
    }

    public TextView getAdd_grade() {
        return add_grade;
    }

    public void setAdd_grade(String add_gradeStr) {
        add_grade.setText(add_gradeStr);
    }
}
