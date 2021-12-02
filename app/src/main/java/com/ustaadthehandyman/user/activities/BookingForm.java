package com.ustaadthehandyman.user.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.models.DBAddress;
import com.ustaadthehandyman.user.util.DBHelper;
import com.ustaadthehandyman.user.util.GlobalFields;
import com.zoho.salesiqembed.ZohoSalesIQ;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class BookingForm extends AppCompatActivity {

    TextView selectedDate,selectedTime, whatWeOffer,selectedPerson,selectedMobile,selectedAddress;
    EditText additionalDetails;
    RelativeLayout btnDate,btnTime;
    LinearLayout customTimeView;
    CardView selectedAddressList;
    Button goToSummery, addAddress;
    ImageButton removeAddress;
    RadioGroup timeSelection;

    int selectedServiceTime;
    String addressId = null;
    String serviceDepartmentId,serviceCatgeory,serviceId;
    String serviceDate,serviceTime;

    Calendar timestampCalender;

    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);
        serviceDepartmentId = getIntent().getStringExtra("serviceDepartmentId");
        String ServiceName = serviceDepartmentId.substring(1,3);
        int serviceId = Integer.parseInt(ServiceName) - 1;

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(GlobalFields.Service[serviceId]);
        }

        selectedDate = findViewById(R.id.txt_selected_date);
        selectedTime = findViewById(R.id.txt_selected_time);
        goToSummery = findViewById(R.id.btn_go_to_summary);
        whatWeOffer = findViewById(R.id.txt_what_we_offer);
        additionalDetails = findViewById(R.id.txt_service_additional_details);
        btnDate = findViewById(R.id.rl_service_date);
        btnTime = findViewById(R.id.rl_service_time);
        selectedAddressList = findViewById(R.id.card_order_address);
        removeAddress = findViewById(R.id.ib_order_address_close);
        selectedPerson = findViewById(R.id.txt_order_person);
        selectedMobile = findViewById(R.id.txt_order_mobile);
        selectedAddress = findViewById(R.id.txt_order_address);
        addAddress = findViewById(R.id.btn_add_address);
        timeSelection = findViewById(R.id.rg_select_time);
        customTimeView = findViewById(R.id.ll_custom_time);

        timestampCalender = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        //Service Data


        selectedDate.setText( DateFormat.format("MMMM dd, yyyy", new Date()));
        selectedTime.setText(DateFormat.format("hh:mm a", new Date()));


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("JogiPrasad btn" ,"true");
                new DatePickerDialog(BookingForm.this,onDateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE)).show();

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(BookingForm.this,onTimeSetListener, calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),false).show();
            }
        });

        timeSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_today:
                        customTimeView.setVisibility(View.GONE);
                        selectedServiceTime = 1;
                    break;
                    case R.id.rb_custom:
                        customTimeView.setVisibility(View.VISIBLE);
                        selectedServiceTime = 2;
                        break;
                }
            }
        });
        /////////////Address get Location or pick address
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookingForm.this,Address.class);
                intent.putExtra("isSelectable",true);
                startActivityForResult(intent,103);
            }
        });
        removeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressId = null;
                addAddress.setVisibility(View.VISIBLE);
                selectedAddressList.setVisibility(View.GONE);
            }
        });

        //Go to booking Page
        goToSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(additionalDetails.getText().toString().isEmpty()){
                    additionalDetails.setError("This field required");
                }else if(addressId == null){
                   Toast.makeText(getApplicationContext(),"Please choose address",Toast.LENGTH_SHORT).show();
               } else{
                    Intent intent;
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        intent = new Intent(getApplicationContext(), OrderSummary.class);
                        intent.putExtra("location",addressId);
                        intent.putExtra("description",additionalDetails.getText().toString().trim());
                        intent.putExtra("serviceId",serviceDepartmentId);

                        if(selectedServiceTime == 2){
                            intent.putExtra("timestamp",timestampCalender.getTimeInMillis());
                        }
                        startActivity(intent);
                    }else{
                        intent = new Intent(getApplicationContext(), AuthActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 103){
            if(resultCode == RESULT_OK){
                addAddress.setVisibility(View.GONE);
                selectedAddressList.setVisibility(View.VISIBLE);
                int id =  data.getIntExtra("AddressId",0);
                DBHelper dbHelper = new DBHelper(BookingForm.this);
                DBAddress dbAddress =  dbHelper.getAddress(id);
                selectedPerson.setText(dbAddress.getName());
                selectedMobile.setText(dbAddress.getMobile());
                selectedAddress.setText(dbAddress.getAddress());
                addressId = String.valueOf(dbAddress.getId());
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

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int y, int m, int d) {
                    serviceDate = MONTHS[m]+" "+String.valueOf(d)+", " +String.valueOf(y);
                    selectedDate.setText(serviceDate);
                    timestampCalender.set(y,m,d);
                }
            };
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int h, int m) {

            String am_pm = "";

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, h);
            datetime.set(Calendar.MINUTE, m);

            timestampCalender.set(Calendar.HOUR,h);
            timestampCalender.set(Calendar.MINUTE,m);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
            serviceTime = strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm;

            selectedTime.setText(serviceTime);
        }
    };

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("services.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
