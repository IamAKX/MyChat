package com.application.akash.chatdemo.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.application.akash.chatdemo.Model.RoomModel;
import com.application.akash.chatdemo.R;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends ArrayAdapter<RoomModel> {

    private Context mContext;
    private List<RoomModel> roomList = new ArrayList<>();
    public RoomAdapter(@NonNull Context context, @NonNull List<RoomModel> list) {
        super(context, 0, list);
        mContext = context;
        roomList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.chatroom_list,parent,false);

        TextView logo = listItem.findViewById(R.id.roomlogo);
        TextView name = listItem.findViewById(R.id.roomname);
        TextView createdate = listItem.findViewById(R.id.roomcreatedate);

        logo.setText(roomList.get(position).getName().toUpperCase().charAt(0)+"");
        name.setText(roomList.get(position).getName());
        createdate.setText("Created on "+roomList.get(position).getCreateDate());

        return listItem;

    }
}
