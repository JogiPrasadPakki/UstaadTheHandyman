package com.ustaadthehandyman.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.models.DBAddress;
import com.ustaadthehandyman.user.util.DBHelper;

import java.util.List;

/**
 * Created by Jogi Prasad Pakki on 16-May-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public  class UserAddressAdapter extends RecyclerView.Adapter<UserAddressAdapter.AddressViewHolder>   {

    private List<DBAddress> addressList;
    private Context context;
    private boolean isSelectable;

    public UserAddressAdapter(List<DBAddress> addressList,Context context,boolean isSelectable) {
        this.addressList = addressList;
        this.context = context;
        this.isSelectable = isSelectable;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_row,parent,false);
        return new  AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddressViewHolder holder, final int position) {
        holder.Name.setText(addressList.get(position).getName());
        holder.Mobile.setText(addressList.get(position).getMobile());
        holder.House.setText(addressList.get(position).getHouse());
        holder.Street.setText(addressList.get(position).getStrret());
        holder.Address.setText(addressList.get(position).getAddress());
        Log.d("Address1", String.valueOf(addressList.get(position).getId()));
        if(isSelectable){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Address2", String.valueOf(addressList.get(position).getId()));
                    Intent intent = new Intent();
                    intent.putExtra("AddressId",addressList.get(position).getId());
                    ((Activity)context).setResult(Activity.RESULT_OK,intent);
                    ((Activity)context).finish();
                }
            });
        }
        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,holder.Edit);
                popupMenu.inflate(R.menu.address_settings);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.menu_address_edit:
                                break;
                            case R.id.menu_address_delete:
                                DBHelper dbHelper = new DBHelper(context);
                                DBAddress address = new DBAddress();
                                address.setId(addressList.get(position).getId());
                                dbHelper.deleteAddress(address);
                                addressList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,addressList.size());
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder{

        TextView Name,Mobile,House,Street,Address;
        Button Edit;

        AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.txt_address_person_name);
            Mobile = itemView.findViewById(R.id.txt_address_person_mobile);
            House = itemView.findViewById(R.id.txt_address_house_no);
            Street= itemView.findViewById(R.id.txt_address_street);
            Address = itemView.findViewById(R.id.txt_address_locality);
            Edit = itemView.findViewById(R.id.btn_edit_address);

        }
    }
}
