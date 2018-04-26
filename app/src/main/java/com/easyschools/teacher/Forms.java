package com.easyschools.teacher;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Forms.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Forms extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView forms_list_view;
    RecyclerView.Adapter forms_adapter;
    List<FormData> formDataList;
    private static String parent_home_work = "Event title";
    List<FormDataDetails> formDataDetailsList;
    private Context context ;
    private CircleImageView school_logo ;


    public Forms() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forms, container, false);
        if (getActivity() != null)
        {
            context = getActivity().getApplicationContext();
        }
        school_logo = v.findViewById(R.id.f);
        Log.d("LOGO", Login.apis.getSchool_logo());
        Picasso.with(context).load(Login.apis.getSchool_logo()).into(school_logo);
        formDataList = new ArrayList<>();
        formDataDetailsList = new ArrayList<>();
        forms_list_view = v.findViewById(R.id.forms_list);
        FormDataDetails formDataDetails = new FormDataDetails();
        formDataDetails.setDescription("There are almost 3 millions Android apps shipped to Play Market. " +
                "This number grows everyday and I bet that 98% of them are using widgets which are " +
                "responsible to show scrollable items to the users.");
        formDataDetails.setId("14");
        formDataDetailsList.add(formDataDetails);
        formDataList.add(new FormData(parent_home_work, formDataDetailsList));
        forms_adapter = new FormsAdapter(formDataList);
        forms_list_view.setLayoutManager(new LinearLayoutManager(context));
        forms_list_view.setAdapter(forms_adapter);
        return v ;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
