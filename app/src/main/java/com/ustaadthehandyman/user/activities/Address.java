package com.ustaadthehandyman.user.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.adapters.UserAddressAdapter;
import com.ustaadthehandyman.user.models.DBAddress;
import com.ustaadthehandyman.user.util.DBHelper;
import com.zoho.salesiqembed.ZohoSalesIQ;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Address extends AppCompatActivity {

    RecyclerView recyclerView;

    double latitude,longitude;
    String addressLine;
    TextInputEditText name,mobile;
    TextView geoAddress;
    LinearLayout addressView,addressAdd;
    String Name,Mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        boolean isSelectable = getIntent().getBooleanExtra("isSelectable",false);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if(isSelectable)
                getSupportActionBar().setTitle("Pick address");
            else
                getSupportActionBar().setTitle("My address");
        }
        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);

        recyclerView = findViewById(R.id.rv_user_address);
        final Button addAddress = findViewById(R.id.btn_add_address);
        Button setAddress = findViewById(R.id.btn_set_address);
        name = findViewById(R.id.et_person_name);
        mobile = findViewById(R.id.et_person_nmobile);
        addressView = findViewById(R.id.ll_address_view);
        addressAdd = findViewById(R.id.ll_address_add);
        geoAddress = findViewById(R.id.txt_geo_address);


        final List<DBAddress> addressList;
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        addressList =  dbHelper.getAllAddress();

        final UserAddressAdapter addressAdapter = new UserAddressAdapter(addressList,this,isSelectable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addressAdapter);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(Address.this,MapActvity.class);
                startActivityForResult(intent,102);
            }
        });
        setAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = name.getText().toString().trim();
                Mobile = mobile.getText().toString().trim();
                if(Name.isEmpty()){
                    name.setError("Please enter your name");
                }else if(Mobile.length() <10 || Mobile.length() > 10){
                    mobile.setError("Please enter valid mobile no");
                } else {
                    DBHelper dbHelper = new DBHelper(Address.this);
                    DBAddress address = new DBAddress();
                    address.setName(name.getText().toString().trim());
                    address.setMobile(mobile.getText().toString().trim());
                    address.setAddress(addressLine);
                    address.setLatitude(latitude);
                    address.setLongitude(longitude);
                    int lastId = (int) dbHelper.putAddress(address);
                    address.setId(lastId);
                    addressList.add(address);
                    addressAdapter.notifyItemInserted(addressList.size()-1);
                    addressView.setVisibility(View.VISIBLE);
                    addressAdd.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102){
            if(resultCode == RESULT_OK){
                addressView.setVisibility(View.GONE);
                addressAdd.setVisibility(View.VISIBLE);
                assert data != null;
                latitude = data.getDoubleExtra("Latitude",17.6868);
                longitude = data.getDoubleExtra("Longitude",83.2185);
                try {
                    List<android.location.Address> addresses  = null;
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(latitude,longitude, 1);
                    addressLine = addresses.get(0).getAddressLine(0);
                    geoAddress.setText(addressLine);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
