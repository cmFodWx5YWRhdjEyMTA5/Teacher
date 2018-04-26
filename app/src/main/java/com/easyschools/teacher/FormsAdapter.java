package com.easyschools.teacher;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by vestment17 on 21/02/18.
 */

public class FormsAdapter extends
        ExpandableRecyclerViewAdapter<FormsParentViewHolder,
                FormsChildViewHolder> {
    private ViewGroup parent = null;
    private Context context;
//    private Typeface custom_font;

    public FormsAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public FormsParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forms_item, parent, false);
        context = parent.getContext();
//        custom_font = Typeface.createFromAsset(parent.getContext().getAssets(),
//                "fonts/overpass-mono-light.otf");
        this.parent = parent;
        return new FormsParentViewHolder(view);
    }

    @Override
    public FormsChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.form_child_item, parent, false);
        return new FormsChildViewHolder(view);

    }

    @Override
    public void onBindChildViewHolder(final FormsChildViewHolder holder, final int flatPosition,
                                      final ExpandableGroup group, final int childIndex) {

        FormDataDetails formDataDetails = (FormDataDetails) group.getItems().get(childIndex);
        holder.id.setText(formDataDetails.getId());
        holder.description.setText(formDataDetails.getDescription());
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.write_comment.setVisibility(View.VISIBLE);
            }
        });
       // holder.description.setTypeface(custom_font);

    }
    @Override
    public void onBindGroupViewHolder(FormsParentViewHolder holder, int flatPosition, ExpandableGroup group) {
//        holder.setEvent_title(group.getTitle());
//        holder.getEvent_title().setTypeface(custom_font);
    }
}