package com.easyschools.teacher;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class GradesAdapter extends
        ExpandableRecyclerViewAdapter<GradeParentViewHolder, GradeChildViewHolder>
{
    public static  List<String> ids = new ArrayList<>();
    public static List<String> grades_data = new ArrayList<>();
    public static List<String> add_grades = new ArrayList<>();

    public GradesAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public GradeParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grade_item, parent, false);

        return new GradeParentViewHolder(view);
    }

    @Override
    public GradeChildViewHolder onCreateChildViewHolder(ViewGroup parent,
                                                        int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grades_child_details, parent, false);
        return new GradeChildViewHolder(view);
    }
    @Override
    public void onBindChildViewHolder(final GradeChildViewHolder holder,
                                      int flatPosition, ExpandableGroup group, int childIndex) {

        GradesDataDetails dataDetails = (GradesDataDetails)
                group.getItems().get(childIndex);
        holder.name.setText(dataDetails.getName());
        holder.score.setText(dataDetails.getScore());
        holder.id.setText(dataDetails.getId());
        holder.add_grade.setText(dataDetails.getAddgrade_id());
//        holder.score.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                Log.d("T",holder.score.getText().toString());
//
//
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.d("SCORE",holder.score.getText().toString());
//                Log.d("ID", holder.id.getText().toString());
//                Log.d("OK", holder.add_grade.getText().toString());
//                ids.add(holder.id.getText().toString());
//                add_grades.add(holder.add_grade.getText().toString());
//                grades_data.add(holder.score.getText().toString());
//            }
//        });
//

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Grades.submit.setVisibility(View.VISIBLE);
            }
        });
        holder.score.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("FOCUS", String.valueOf(hasFocus));
                if (!hasFocus)
                {
                    Log.d("SCORE",holder.score.getText().toString());
                    Log.d("ID", holder.id.getText().toString());
                    Log.d("OK", holder.add_grade.getText().toString());
                    ids.add(holder.id.getText().toString());
                    add_grades.add(holder.add_grade.getText().toString());
                    grades_data.add(holder.score.getText().toString());
                }
            }
        });

    }

    @Override
    public void onBindGroupViewHolder(GradeParentViewHolder holder,
                                      int flatPosition, ExpandableGroup group) {

        Log.d("TITLE", group.getTitle());

        String[] separated = group.getTitle().split(Pattern.quote("+"));;
        holder.setGrade_title(separated[0]);
        holder.setTotal_score(separated[1]);

    }

}
