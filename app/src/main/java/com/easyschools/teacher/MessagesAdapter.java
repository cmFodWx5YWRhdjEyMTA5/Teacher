package com.easyschools.teacher;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.security.acl.LastOwnerException;
import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    Context context;
    List<MessageData> datas;
    private static String id = "";
    private SharedPreferences settings;

    public MessagesAdapter(List<MessageData> messageDataList , Context context ) {
        super();
        this.datas = messageDataList;
        this.context = context;
        settings = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        id = settings.getString("ID","");

    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        MessagesAdapter.ViewHolder viewHolder = new MessagesAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessagesAdapter.ViewHolder holder, int position) {

        MessageData messageData =  datas.get(position);
        holder.msg.setText(messageData.getMsg());
        holder.id.setText(messageData.getId());
        holder.time.setText(messageData.getTime());
        holder.name.setText(messageData.getName());
        if (holder.id.getText().toString().equals(id))
        {
            holder.msg_layout.setGravity(Gravity.START);
            Log.d("LEFT" ,"GRAVITY");
        }
        else
        {
            holder.msg_layout.setGravity(Gravity.END);
            holder.msg_back.setBackgroundResource(R.drawable.gray_rounded);
            Log.d("RIGHT","SJKS");
        }

    }

    @Override
    public int getItemCount() {

        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg , id  , time , name ;
        RelativeLayout msg_layout , msg_back ;
        public ViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.msg);
            id = itemView.findViewById(R.id.id);
            msg_layout = itemView.findViewById(R.id.layout_msg);
            msg_back = itemView.findViewById(R.id.msg_back);
            time =itemView.findViewById(R.id.msg_time);
            name = itemView.findViewById(R.id.sender_name);
        }
    }
}




