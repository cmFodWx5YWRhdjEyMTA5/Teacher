package com.easyschools.teacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends Fragment {

    private OnFragmentInteractionListener mListener;
    EditText name, phone_number, address, email, school_name,
            teacher_classes, teacher_subjects;
    SharedPreferences settings;
    private Typeface custom_font;
    private Apis apis;
    private static String lang = "";
    private RelativeLayout profile_layout;
    private String student_id = null;
    private TextView change_password;
    private String token = "";
    Boolean firstClick = true;
    private CircleImageView student_image;
    private ImageView edit_profile, edit_image;
    private String imgDecodableString, subjectString, classesString = null;
    private static int RESULT_LOAD_IMG = 1;
    private static List<String> subjectStr, classeStr;
    private Context context;
    MyPageAdapter pageAdapter;
    private TabLayout tabLayout;
    ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.info,
            R.drawable.moreinfo,
            R.drawable.notebook};

    public MyProfile() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        apis = new Apis();
        settings = getContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        lang = settings.getString("LANG", "");
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        }
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = v.findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
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
    private void setupViewPager(ViewPager viewPager) {
        MyPageAdapter adapter = new MyPageAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new GeneralDetails(), "General Info");
        adapter.addFragment(new Personal(), "Personal & Contact");
        adapter.addFragment(new Report(), "Address & More");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
}
