package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.PlaceOrder;
import com.ustaadthehandyman.user.api.models.body.BodyPlaceOrder;
import com.ustaadthehandyman.user.api.models.response.BookingSuccess;
import com.ustaadthehandyman.user.models.DBAddress;
import com.ustaadthehandyman.user.util.DBHelper;
import com.ustaadthehandyman.user.util.EventHandler;
import com.ustaadthehandyman.user.util.GlobalFields;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.greenrobot.eventbus.EventBus;

public class OrderSummary extends AppCompatActivity {

    TextView serviceType,summaryTime,txtDescriptio,txtAddress,txtUser,txtMobile;
    Button bookService;
    PlaceOrder placeOrder;
    ProgressDialog progressDialog;
    ProgressBar progressBar;

    String address, serviceDecription,serviceId,serviceTimeing;

    DBHelper dbHelper;
    DBAddress dbAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);
        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final long serviceTime = getIntent().getLongExtra("timestamp",0);
        serviceDecription = getIntent().getStringExtra("description");
        final String serviceLocationId = getIntent().getStringExtra("location");
        serviceId  = getIntent().getStringExtra("serviceId");
        serviceTimeing = String.valueOf(serviceTime);

        String ServiceName = serviceId.substring(1,3);
        int serviceTypeID = Integer.parseInt(ServiceName) - 1;

        progressBar = findViewById(R.id.pb_book_order);
        summaryTime = findViewById(R.id.txt_summary_service_time);
        bookService = findViewById(R.id.btn_book_service);
        serviceType = findViewById(R.id.txt_summary_service_type);
        txtAddress=  findViewById(R.id.txt_summary_service_address);
        txtDescriptio = findViewById(R.id.txt_summary_service_decription);
        txtUser = findViewById(R.id.txt_summary_service_user);
        txtMobile = findViewById(R.id.txt_summary_service_mobile);

        dbHelper = new DBHelper(this);
        dbAddress =  dbHelper.getAddress(Integer.parseInt(serviceLocationId));

        String addressLine = dbAddress.getAddress().replace(",","\n");
        if(serviceTime == 0)
            summaryTime.setText("Today");
        else
            summaryTime.setText(GlobalFields.getFullDate(serviceTime));

        summaryTime.setText(GlobalFields.getFullDate(serviceTime));
        serviceType.setText(GlobalFields.Service[serviceTypeID]);
        txtDescriptio.setText(serviceDecription);
        txtUser.setText(dbAddress.getName());
        txtMobile.setText(dbAddress.getMobile());
        txtAddress.setText(addressLine);

        placeOrder = APIInitialize.PostOrder();

        bookService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressDialog = new ProgressDialog(OrderSummary.this,ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Submitting");
                    progressDialog.show();
                    bookOrder();
                }
            }
        });
    }

    private void bookOrder(){

        long currentTime = System.currentTimeMillis()/1000;
        BodyPlaceOrder body = new BodyPlaceOrder();
        body.setUid(FirebaseAuth.getInstance().getUid());
        body.setServiceid(Integer.parseInt(serviceId));
        body.setDescription(serviceDecription);
        body.setTimestamp(serviceTimeing);
        body.setBookingtime(String.valueOf(currentTime));
        body.setLocation(dbAddress.getAddress());
        body.setLatitude(dbAddress.getLatitude());
        body.setLongitude(dbAddress.getLongitude());
        body.setUserName(dbAddress.getName());
        body.setMobile(dbAddress.getMobile());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = preferences.getString("auth-token",null);
        placeOrder.makeOrder("barer "+token,body).enqueue(new Callback<BookingSuccess>() {
            @Override
            public void onResponse(Call<BookingSuccess> call, Response<BookingSuccess> response) {
                Log.d("Booking ID", String.valueOf(response.code()));
                if(response.code() == 200){
                    Log.d("Jogi Prasad Or","sent");
                    Log.d("Booking ID", String.valueOf(response.body().getBookingId()));
                    Toast.makeText(getApplicationContext(),"Your service bocked successfully ",Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressDialog.dismiss();
                    }
                    EventBus.getDefault().postSticky(new EventHandler(124));
                    finish();
                }else{
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(),"Some thing went wrong try again api",Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<BookingSuccess> call, Throwable t) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.d("Jogi Prasad Or","sent");
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
