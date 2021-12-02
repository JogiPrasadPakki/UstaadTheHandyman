package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.adapters.serviceItemAdapter;
import com.ustaadthehandyman.user.util.GlobalFields;
import com.ustaadthehandyman.user.models.SubServiceItems;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class serviceList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);
        final String serviceId = getIntent().getStringExtra("serviceId");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(serviceId);
        }

        Log.d("JogiPrasad ServiceId",serviceId);

        ListView listView = (ListView)findViewById(R.id.service_item_list);
        ArrayList<SubServiceItems> serviceList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(GlobalFields.loadJSONFromAsset(getApplicationContext()));
            JSONObject serviceData = jsonObject.getJSONObject(serviceId).getJSONObject("Services");
            for(Iterator<String> iter = serviceData.keys(); iter.hasNext();) {
                String key = iter.next();
                String icon = serviceData.getJSONObject(key).getString("icon");
                int resourceId = getResources().getIdentifier(icon, "drawable",getPackageName());
                serviceList.add(new SubServiceItems(key,resourceId));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View v = adapterView.getChildAt(i);
                String item = ((TextView)v.findViewById(R.id.txt_service_item)).getText().toString();
                Log.d("JogiPrasadSubID",item);
                Intent intent = new Intent(getApplicationContext(), BookingForm.class);
                intent.putExtra("serviceId",serviceId);
                intent.putExtra("subSeviceId",item);
                startActivity(intent);
            }
        });

        ArrayAdapter<SubServiceItems> adapter = new serviceItemAdapter(this,0,serviceList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
            //overridePendingTransition(R.anim.slide_left_to_right ,R.anim.slide_right_to_left);
        }

        return super.onOptionsItemSelected(item);
    }

}
