package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.OrderDetails;
import com.ustaadthehandyman.user.api.models.body.Id;
import com.ustaadthehandyman.user.api.models.response.BookingSuccess;
import com.ustaadthehandyman.user.util.GlobalFields;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderInfo extends AppCompatActivity {

    TextView BookingId,ServiceName,ServiceTime,BookingTime,Description,Name,Mobile,Address,Status,ClearedTime,Price,Invoice;
    LinearLayout ClearedLayout;
    OrderDetails orderDetails;
    SharedPreferences preference;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Order info");
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressDialog = new ProgressDialog(OrderInfo.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }
        final int orderId = getIntent().getIntExtra("order-id",0);
        BookingId = findViewById(R.id.txt_history_service_booking_id);
        ServiceName = findViewById(R.id.txt_history_service_type);
        ServiceTime = findViewById(R.id.txt_history_service_time);
        BookingTime = findViewById(R.id.txt_history_service_booking_time);
        Description = findViewById(R.id.txt_history_service_decription);
        Name = findViewById(R.id.txt_history_service_user);
        Mobile = findViewById(R.id.txt_history_service_mobile);
        Address = findViewById(R.id.txt_history_service_address);
        Status = findViewById(R.id.txt_history_service_status);
        ClearedTime = findViewById(R.id.txt_history_service_cleared_time);
        Price = findViewById(R.id.txt_history_service_price);
        Invoice = findViewById(R.id.txt_invoice);
        ClearedLayout = findViewById(R.id.li_order_info_cleared);
        progressBar = findViewById(R.id.pb_view_order);
        orderDetails = APIInitialize.GetOrderInfo();

        preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Id id = new Id();
        id.setId(orderId);
        orderDetails.getInfo("barer "+preference.getString("auth-token",null),id).enqueue(new Callback<List<com.ustaadthehandyman.user.api.models.response.OrderInfo>>() {
            @Override
            public void onResponse(Call<List<com.ustaadthehandyman.user.api.models.response.OrderInfo>> call, Response<List<com.ustaadthehandyman.user.api.models.response.OrderInfo>> response) {
                if(response.code() == 200){
                    com.ustaadthehandyman.user.api.models.response.OrderInfo orderInfo = response.body().get(0);
                    BookingId.setText(String.valueOf(orderInfo.getId()));
                    String serviceId = String.valueOf(orderInfo.getServiceID());
                    String SerlizeId = serviceId.substring(1,3);
                    ServiceName.setText(GlobalFields.Service[Integer.parseInt(SerlizeId) - 1]);
                    ServiceTime.setText(GlobalFields.getFullDate(orderInfo.getTimestamp()));
                    BookingTime.setText(GlobalFields.getFullDate(orderInfo.getBookingTime()));
                    Description.setText(orderInfo.getDescription());
                    Mobile.setText(orderInfo.getMobile());
                    String address = orderInfo.getAddress().replace(",","\n");
                    Address.setText(address);
                    Name.setText(orderInfo.getUserName());
                    switch (orderInfo.getStatus()){
                        case 1:
                            Status.setText("Pending");
                            Status.setTextColor(Color.parseColor("#FF6E00"));
                            break;
                        case 2:
                            Status.setText("Progressing");
                            Status.setTextColor(Color.parseColor("#FFB500"));
                            break;
                        case 3:
                            Status.setText("Finished");
                            Status.setTextColor(Color.parseColor("#36A21F"));
                            break;
                    }
                    if(orderInfo.getStatus() == 3){
                        ClearedLayout.setVisibility(View.VISIBLE);
                        Price.setText(String.valueOf(orderInfo.getPrice()));
                        ClearedTime.setText(GlobalFields.getFullDate(orderInfo.getClearedTime()));
                        Invoice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(),Browser.class);
                                intent.putExtra("browserType",4);
                                intent.putExtra("InvoiceId",orderId);
                                startActivity(intent);
                            }
                        });
                    }

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.ustaadthehandyman.user.api.models.response.OrderInfo>> call, Throwable t) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
