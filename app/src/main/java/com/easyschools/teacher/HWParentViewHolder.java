package com.easyschools.teacher;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;


public class HWParentViewHolder extends GroupViewHolder
{
    private final TextView homework_title ;
    public HWParentViewHolder(View itemView) {
        super(itemView);
        homework_title = itemView.findViewById(R.id.homework_title);
    }

    public TextView getHomework_title() {
        return homework_title;
    }

    public void setHomework_title(String title)
    {
        homework_title.setText(title);
    }


}
