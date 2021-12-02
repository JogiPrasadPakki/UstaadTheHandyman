package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.util.EventHandler;
import com.ustaadthehandyman.user.util.GlobalFields;

import org.greenrobot.eventbus.EventBus;

public class About extends AppCompatActivity {

    LinearLayout logout,Terms,Privacy,About;
    TextView AppVersion;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("About");
        }
        logout = findViewById(R.id.ll_account_logout);
        Terms = findViewById(R.id.ll_about_terms);
        Privacy = findViewById(R.id.ll_about_privacy);
        About = findViewById(R.id.ll_about_about_us);
        AppVersion = findViewById(R.id.txt_about_app_version);

        intent = new Intent(getApplicationContext(), Browser.class);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            logout.setVisibility(View.GONE);
        }else {
            logout.setVisibility(View.VISIBLE);
        }
        String version = "App Version "+GlobalFields.currentVersionName(this);
        AppVersion.setText(version);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                editor.remove("auth-token");
                editor.apply();
                EventBus.getDefault().postSticky(new EventHandler(GlobalFields.Logoutsuccess));
                if(firebaseAuth.getCurrentUser() == null){
                    logout.setVisibility(View.GONE);
                }else {
                    logout.setVisibility(View.VISIBLE);
                }

            }
        });
        Terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("browserType",2);
                startActivity(intent);
            }
        });
        Privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("browserType",1);
                startActivity(intent);
            }
        });
        About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("browserType",3);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
