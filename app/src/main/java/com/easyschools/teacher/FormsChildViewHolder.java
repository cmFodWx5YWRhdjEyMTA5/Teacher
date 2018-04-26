package com.easyschools.teacher;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * Created by vestment17 on 21/02/18.
 */

public class FormsChildViewHolder extends ChildViewHolder
{
    TextView description , id ;
    Button deny , accept ;
    ImageButton comment ;
    EditText write_comment ;

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public FormsChildViewHolder(View itemView) {
        super(itemView);
        description = itemView.findViewById(R.id.form_description);
        deny = itemView.findViewById(R.id.deny_btn);
        accept = itemView.findViewById(R.id.accept_btn);
        comment =  itemView.findViewById(R.id.comment_btn);
        write_comment = itemView.findViewById(R.id.write_comment);
        id = itemView.findViewById(R.id.form_id);
    }

    public TextView getId() {
        return id;
    }

    public void setId(TextView id) {
        this.id = id;
    }
}
