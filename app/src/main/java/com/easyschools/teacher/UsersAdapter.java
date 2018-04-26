package com.easyschools.teacher;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    Context context;
    List<UserData> datas;
    public static List<String> users_ids ;

    public UsersAdapter(List<UserData> userDataList, Context context) {
        super();
        this.datas = userDataList;
        this.context = context;
        users_ids = new ArrayList<>();
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        UsersAdapter.ViewHolder viewHolder = new UsersAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.ViewHolder holder, int position) {

        UserData userData =  datas.get(position);
        holder.name.setText(userData.getName());
        holder.id.setText(userData.getId());
        Picasso.with(context).load(userData.getImage())
                .into(holder.user_image);
        holder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    Log.d("IDS", holder.id.getText().toString());
                    users_ids.add(holder.id.getText().toString());
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name , id ;
        CheckBox select ;
        CircleImageView user_image ;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            id = itemView.findViewById(R.id.user_id);
            select = itemView.findViewById(R.id.select_user);
            user_image = itemView.findViewById(R.id.user_image);
        }
    }
}


