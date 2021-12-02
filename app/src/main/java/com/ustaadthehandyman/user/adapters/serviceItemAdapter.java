package com.ustaadthehandyman.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.models.SubServiceItems;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Jogi Prasad Pakki on 15-May-18.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018 All reserved by Jogi Prasad Pakki.
 */
public class serviceItemAdapter extends ArrayAdapter<SubServiceItems>implements View.OnClickListener {


    ArrayList<SubServiceItems> dataList;
    Context context;

    public serviceItemAdapter(Context context,int resource,ArrayList<SubServiceItems> data) {
        super(context, resource,data);
        this.dataList = data;
        this.context = context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubServiceItems serviceItems = dataList.get(position);
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.service_item, null);

        ImageView serviceIcon = (ImageView)view.findViewById(R.id.img_service_item_icon);
        TextView serviceName = (TextView)view.findViewById(R.id.txt_service_item);

        serviceIcon.setImageResource(serviceItems.getServiceIcon());
        serviceName.setText(serviceItems.getServiceName());

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
