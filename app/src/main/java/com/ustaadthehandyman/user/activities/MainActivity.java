package com.ustaadthehandyman.user.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.GetUser;
import com.ustaadthehandyman.user.api.LastActive;
import com.ustaadthehandyman.user.api.models.body.LastSeen;
import com.ustaadthehandyman.user.api.models.body.UserId;
import com.ustaadthehandyman.user.api.models.response.UserInfo;
import com.ustaadthehandyman.user.fragments.MainPage;
import com.ustaadthehandyman.user.fragments.OrdersPage;
import com.ustaadthehandyman.user.fragments.UserAccount;
import com.ustaadthehandyman.user.util.Configuration;
import com.ustaadthehandyman.user.util.GlobalFields;
import com.ustaadthehandyman.user.util.NetworkWatcher;
import com.zoho.commons.ChatComponent;
import com.zoho.commons.Color;
import com.zoho.commons.SystemMessage;
import com.zoho.salesiqembed.ZohoSalesIQ;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ustaadthehandyman.user.util.NetworkWatcher.IS_NETWORK_AVAILABLE;

public class MainActivity extends AppCompatActivity implements MainPage.OnFragmentInteractionListener, OrdersPage.OnFragmentInteractionListener, UserAccount.OnFragmentInteractionListener {

    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    LinearLayout currentLocationInfo;
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView txtAddress;

    FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    HashMap<String,Object> firebaseParams;
    String APP_VERSION = "CURRENT_APP_VERSION";
    LastActive lastActive;

    final Fragment mainPage = new MainPage();
    final Fragment ordersPage = new OrdersPage();
    final Fragment accountPage = new UserAccount();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activiePage = mainPage;
    boolean isSetLastSeen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_one);

        //Initialize views
        toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        currentLocationInfo = findViewById(R.id.tc_location);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onClickNavigation);
        txtAddress = findViewById(R.id.txt_current_location);

        //bottom navigation
        fragmentManager.beginTransaction().add(R.id.frame_container, accountPage, "3").hide(accountPage).commit();
        fragmentManager.beginTransaction().add(R.id.frame_container, ordersPage, "2").hide(ordersPage).commit();
        fragmentManager.beginTransaction().add(R.id.frame_container, mainPage, "1").commit();

        Log.d("Time", String.valueOf(System.currentTimeMillis()));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        //Internet connection watche
        if(GlobalFields.isInternetAvailable(this)){

            Lastseen();
        }
        IntentFilter intentFilter = new IntentFilter(NetworkWatcher.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
               if(isNetworkAvailable && firebaseAuth.getCurrentUser() != null){
                  Lastseen();
               }
            }
        }, intentFilter);

        currentLocationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActvity.class);
                startActivityForResult(intent, 101);
            }
        });


        if (!sharedPreferences.getBoolean("isAuthSkipped", false)) {
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        if(sharedPreferences.getString("CurrentVersion",null) != null){
            if(Float.parseFloat(sharedPreferences.getString("CurrentVersion",null)) < Float.parseFloat(GlobalFields.currentVersionName(getApplicationContext()))){
                if(sharedPreferences.getBoolean("UpdateAvailable",false)){
                    Intent intent = new Intent(getApplicationContext(),ForceUpdate.class);
                    startActivity(intent);
                }
            }else {
                editor.remove("CurrentVersion");
                editor.remove("UpdateAvailable");
                editor.apply();
            }
        }
        firebaseParams = new HashMap<>();
        firebaseParams.put(APP_VERSION, GlobalFields.currentVersionName(this));
        firebaseRemoteConfig.setDefaults(firebaseParams);
        firebaseRemoteConfig.setConfigSettingsAsync(
              new   FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build()
        );
        firebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseRemoteConfig.activateFetched();
                    double update_version = firebaseRemoteConfig.getDouble(APP_VERSION);
                    if(update_version > Double.parseDouble(GlobalFields.currentVersionName(getApplicationContext()))){
                        editor.putString("CurrentVersion",GlobalFields.currentVersionName(getApplicationContext()));
                        editor.putBoolean("UpdateAvailable",true);
                        editor.apply();
                    }
                }
            }
        });


        FirebaseMessaging.getInstance().subscribeToTopic("PJP").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("FCM Notification","Subscribed");
                }
            }
        });
        //Zoho Live Chat configurations
        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location;
        if (isNetworkEnabled){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                try {
                    Log.d("latLang", String.valueOf(location.getLatitude()));
                    List<Address> addresses  = null;
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                    String city = addresses.get(0).getAddressLine(0);
                    txtAddress.setText(city);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        if (item.getItemId() == R.id.menu_feed_back) {
            Intent intent = new Intent(getApplicationContext(),Support.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void Lastseen(){
        lastActive = APIInitialize.setLastSeen();
        if(isSetLastSeen == false){
            LastSeen lastSeen = new LastSeen();
            lastSeen.setTime(System.currentTimeMillis()/1000);
            lastActive.LastSeen("barer "+sharedPreferences.getString("auth-token",null),lastSeen).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("LastSeen",String.valueOf(response.code()));
                    isSetLastSeen = true;
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }

    }

    BottomNavigationView.OnNavigationItemSelectedListener onClickNavigation = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_home:
                    currentLocationInfo.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle("");
                    fragmentManager.beginTransaction().hide(activiePage).show(mainPage).commit();
                    activiePage = mainPage;
                    menuItem.setChecked(true);
                    break;
                case R.id.menu_orders:
                    currentLocationInfo.setVisibility(View.GONE);
                    getSupportActionBar().setTitle("My Orders");
                    fragmentManager.beginTransaction().hide(activiePage).show(ordersPage).commit();
                    activiePage = ordersPage;
                    menuItem.setChecked(true);
                    break;
                case R.id.menu_account:
                    currentLocationInfo.setVisibility(View.GONE);
                    getSupportActionBar().setTitle("My Account");
                    fragmentManager.beginTransaction().hide(activiePage).show(accountPage).commit();
                    activiePage = accountPage;
                    menuItem.setChecked(true);
                    break;
            }
            return false;
        }
    };


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            if(resultCode == RESULT_OK){
                assert data != null;
                double latitude = data.getDoubleExtra("Latitude",17.6868);
                double longitude = data.getDoubleExtra("Longitude",83.2185);
                try {
                    List<Address> addresses  = null;
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(latitude,longitude, 1);
                    String city = addresses.get(0).getAddressLine(0);
                    txtAddress.setText(city);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
