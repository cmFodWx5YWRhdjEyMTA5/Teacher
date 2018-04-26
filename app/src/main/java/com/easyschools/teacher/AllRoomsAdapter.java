package com.easyschools.teacher;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllRoomsAdapter extends RecyclerView.Adapter<AllRoomsAdapter.ViewHolder> {

    Context context;
    Context mcontext ;
    List<RoomsData> datas;

    public AllRoomsAdapter(List<RoomsData> roomsDataList, Context context , Context mcontext) {
        super();
        this.datas = roomsDataList;
        this.context = context;
        this.mcontext = mcontext ;
    }

    @Override
    public AllRoomsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rooms, parent, false);
        AllRoomsAdapter.ViewHolder viewHolder = new AllRoomsAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AllRoomsAdapter.ViewHolder holder, int position) {

        RoomsData roomsData =  datas.get(position);
        holder.name.setText(roomsData.getName());
        holder.id.setText(roomsData.getId());
        Picasso.with(context).load(roomsData.getPicture())
                .into(holder.user_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ROOM_ID" , holder.id.getText().toString() );
                intent.putExtra("TO_BE_SEND", holder.user_id.getText().toString());
                context.startActivity(intent);
            }
        });
        holder.user_id.setText(roomsData.getUser_id());
    }

    @Override
    public int getItemCount() {

        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name , id , user_id ;
        CircleImageView user_image ;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.room_name);
            id = itemView.findViewById(R.id.room_id);
            user_image = itemView.findViewById(R.id.person_image);
            user_id = itemView.findViewById(R.id.user_id_to_send);
        }
    }
}




