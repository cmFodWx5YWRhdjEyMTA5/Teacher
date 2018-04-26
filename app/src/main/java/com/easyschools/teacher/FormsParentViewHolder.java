package com.easyschools.teacher;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

/**
 * Created by vestment17 on 21/02/18.
 */

public class FormsParentViewHolder extends GroupViewHolder
{
    private  TextView event_title ;
    public FormsParentViewHolder(View itemView) {
        super(itemView);
        event_title = itemView.findViewById(R.id.form_title);
    }


    public TextView getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title_str) {
        event_title.setText(event_title_str);
    }
}
