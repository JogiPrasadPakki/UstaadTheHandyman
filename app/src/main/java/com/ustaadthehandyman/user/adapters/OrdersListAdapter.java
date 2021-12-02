package com.ustaadthehandyman.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.activities.OrderInfo;
import com.ustaadthehandyman.user.api.models.response.Orders;
import com.ustaadthehandyman.user.util.GlobalFields;

import java.util.List;

/**
 * Created by Jogi Prasad Pakki on 15-May-18.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018 All reserved by Jogi Prasad Pakki.
 */
public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> implements View.OnClickListener {

   private List<Orders> orders;
   private Context context;

    public OrdersListAdapter(List<Orders> orders) {
        this.orders = orders;
    }

    @Override
    public void onClick(View view) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.serviceDescription.setText(orders.get(position).getDescription());
        Log.d("Status",String.valueOf(orders.get(position).getStatus()));
        switch (orders.get(position).getStatus()){
            case 1:
                holder.serviceStatus.setText("Pending");
                holder.serviceStatus.setTextColor(Color.parseColor("#FF6E00"));
                break;
            case 2:
                holder.serviceStatus.setText("Progressing");
                holder.serviceStatus.setTextColor(Color.parseColor("#FFB500"));
                break;
            case 3:
                holder.serviceStatus.setText("Finished");
                holder.serviceStatus.setTextColor(Color.parseColor("#36A21F"));
                break;
        }
        holder.bookingDate.setText(GlobalFields.getSimpleDate(Long.parseLong(orders.get(position).getBookingTime())));
        String serviceId = String.valueOf(orders.get(position).getServiceID());
        String SerlizeId = serviceId.substring(1,3);
        holder.serviceName.setText(GlobalFields.Service[Integer.parseInt(SerlizeId) - 1]);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderInfo.class);
                intent.putExtra("order-id",orders.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView serviceName,serviceDescription,bookingDate,serviceStatus;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_rv_user_orders);
            serviceName = itemView.findViewById(R.id.txt_rv_service_name);
            serviceDescription = itemView.findViewById(R.id.txt_rv_service_description);
            bookingDate = itemView.findViewById(R.id.txt_rv_service_date);
            serviceStatus = itemView.findViewById(R.id.txt_rv_service_status);
        }
    }
}
