package com.easyschools.teacher;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
   // private final Typeface custom_font;
    private final Context context;
    private  List<EventData> eventDatas ;

    public EventAdapter(List<EventData> eventDataList, Context context) {
        super();
        this.context = context;
        this.eventDatas = eventDataList;
        //custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/overpass-mono-light.otf");
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventAdapter.ViewHolder holder, int position) {
        EventData eventData = eventDatas.get(position);
        holder.name.setText(eventData.getName());
        holder.time.setText(eventData.getTime());
        holder.desc.setText(eventData.getDesc());
        holder.day_no.setText(eventData.getDay_no());
        holder.day_txt.setText(eventData.getDay_txt());

//        holder.name.setTypeface(custom_font);
//        holder.time.setTypeface(custom_font);
//        holder.desc.setTypeface(custom_font);
//        holder.day_no.setTypeface(custom_font);
//        holder.day_txt.setTypeface(custom_font);

    }

    @Override
    public int getItemCount() {
        return eventDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name , time , desc , day_no , day_txt;
        ViewHolder(View itemView)
        {
            super(itemView);
            name =  itemView.findViewById(R.id.event_title);
            time = itemView.findViewById(R.id.event_time);
            desc = itemView.findViewById(R.id.event_description);
            day_no = itemView.findViewById(R.id.number);
            day_txt = itemView.findViewById(R.id.event_day);

        }
    }
}

