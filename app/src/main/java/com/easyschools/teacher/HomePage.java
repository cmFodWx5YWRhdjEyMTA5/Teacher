package com.easyschools.teacher;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RelativeLayout material, homework, grades,
            behavior, attendance, calender, forms, schedule;
    private CircleImageView school_logo ;
    private Context context;

    public HomePage() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        }
        material = v.findViewById(R.id.material);
        homework = v.findViewById(R.id.homework);
        grades = v.findViewById(R.id.grades);
        behavior = v.findViewById(R.id.behavior);
        attendance = v.findViewById(R.id.attendance);
        calender = v.findViewById(R.id.calender);
        forms = v.findViewById(R.id.forms);
        schedule = v.findViewById(R.id.schedule);
        school_logo = v.findViewById(R.id.h);
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectMaterial myFragment = new SubjectMaterial();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        myFragment).addToBackStack(null).commit();
            }
        });
        homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeWork homeWorkFragment = new HomeWork();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        homeWorkFragment).addToBackStack(null).commit();
            }
        });
        grades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Grades gradesFragment = new Grades();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        gradesFragment).addToBackStack(null).commit();
            }
        });
        behavior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Behavior behaviorFragment = new Behavior();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        behaviorFragment).addToBackStack(null).commit();
            }
        });
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Attendance attendanceFragment = new Attendance();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        attendanceFragment).addToBackStack(null).commit();
            }
        });
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calender calendarFragment = new Calender();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        calendarFragment).addToBackStack(null).commit();
            }
        });
        forms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forms forms = new Forms();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        forms).addToBackStack(null).commit();
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Schedule scheduleFragment = new Schedule();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        scheduleFragment).addToBackStack(null).commit();
            }
        });
        return v;
    }
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
}
