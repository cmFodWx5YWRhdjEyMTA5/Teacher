package com.easyschools.teacher;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    Context context;
    List<StudentData> datas;
    public  static  List<String> students_ids =  new ArrayList<>();
    private Typeface custom_font;


    public AttendanceAdapter(List<StudentData> studentDataList, Context context) {
        super();
        this.datas = studentDataList;
        this.context = context;
        custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/thin.ttf");
    }

    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_student_item, parent, false);
        AttendanceAdapter.ViewHolder viewHolder = new AttendanceAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AttendanceAdapter.ViewHolder holder, int position) {

        StudentData studentData =  datas.get(position);
        holder.name.setText(studentData.getName());
        holder.name.setTypeface(custom_font);
        holder.id.setText(studentData.getId());
        holder.attend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    Log.d("ID_STUDENT", holder.id.getText().toString());
                    students_ids.add(holder.id.getText().toString());
                    holder.attend.setClickable(false);
                }
            }
        });
//        if (studentData.getAttend_true())
//        {
//            holder.attend.setChecked(true);
//        }


    }

    @Override
    public int getItemCount() {

        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name , id ;
        RadioButton attend ;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.attendance_student_name);
            id = itemView.findViewById(R.id.attendance_student_id);
            attend = itemView.findViewById(R.id.attend);
        }
    }
}

