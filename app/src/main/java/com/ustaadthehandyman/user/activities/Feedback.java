package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.SendFeedback;
import com.ustaadthehandyman.user.util.GlobalFields;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    ProgressDialog progressDialog;
    List<String> serviceList;
    EditText  feedbackContent;
    Spinner serviceItems;
    ProgressBar progressBar;
    Button sendFeedBack;
    RatingBar ratingBar;

    FirebaseAuth firebaseAuth;
    SendFeedback feedbackService;

    int serviceItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Feed back");
        }
        serviceList = new ArrayList<>();
        feedbackContent = findViewById(R.id.et_feedback);
        serviceItems = findViewById(R.id.sp_feedback);
        sendFeedBack  = findViewById(R.id.btn_send_feedback);
        progressBar = findViewById(R.id.pb_feedback);
        ratingBar = findViewById(R.id.feedback_rating);

        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);
        firebaseAuth = FirebaseAuth.getInstance();
        feedbackService = APIInitialize.PostFeedback();

       serviceList.add("AC Services");
       serviceList.add("Carpenter Services");
       serviceList.add("CC Tv Services");
       serviceList.add("Computer Service");
       serviceList.add("Electrical Services");
       serviceList.add("Home appliance Services");
       serviceList.add("Logistic services");
       serviceList.add("Mobile services");
       serviceList.add("Painting Services");
       serviceList.add("Plumbing Services");
       serviceList.add("Solar Panel Services");
       serviceList.add("Worker Services");

       final int[] serviceIDS = new int[]{3041,3071,3011,3081,3021,3061,3111,3101,3031,3051,3091,3121};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item
                , serviceList);
        serviceItems.setAdapter(dataAdapter);

        serviceItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                serviceItem = serviceIDS[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (feedbackContent.getText().toString().isEmpty()) {
                    feedbackContent.setError("This field requird");
                } else if (ratingBar.getRating() == 0) {
                    Toast.makeText(getApplicationContext(), "Please give rating", Toast.LENGTH_SHORT).show();
                } else {

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressDialog = new ProgressDialog(Feedback.this, ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Submitting feedback");
                        progressDialog.show();
                    }
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    com.ustaadthehandyman.user.api.models.body.Feedback feedback = new com.ustaadthehandyman.user.api.models.body.Feedback();
                    feedback.setUid(firebaseAuth.getUid());
                    feedback.setDescription(feedbackContent.getText().toString().trim());
                    feedback.setRating(Math.round(ratingBar.getRating()));
                    feedback.setServiceid(serviceItem);
                    feedback.setTimestamp(timeStamp);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String tokenValue = preferences.getString("auth-token", null);
                    feedbackService.sendFeedback("barer " + tokenValue, feedback).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            Log.d("API", String.valueOf(response.code()));
                            if (response.code() == 200) {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getApplicationContext(), "Thank you for submitting feedback", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    progressDialog.dismiss();
                                }
                                Log.d("API", response.errorBody().toString());
                                Toast.makeText(getApplicationContext(), "Some thing went wrong try again", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "Some thing went wrong try again", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
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
