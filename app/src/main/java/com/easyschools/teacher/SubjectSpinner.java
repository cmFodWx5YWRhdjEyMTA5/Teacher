package com.easyschools.teacher;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class SubjectSpinner extends ArrayAdapter {

    private Typeface custom_font;
    private Context context;
    private List<String> list;


    public SubjectSpinner(Context context, int textViewResourceId , List<String> list) {
        super(context, textViewResourceId, list);
        this.context = context;
        this.list = list;
        custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/regular.ttf");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/thin.ttf");
        v.setTypeface(custom_font, Typeface.BOLD);
        v.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        return v;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(custom_font, Typeface.NORMAL);
        v.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        return v;
    }
}
