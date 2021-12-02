package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ustaadthehandyman.user.R;
import com.zoho.commons.ChatComponent;
import com.zoho.commons.Color;
import com.zoho.commons.SystemMessage;
import com.zoho.salesiqembed.ZohoSalesIQ;

public class Support extends AppCompatActivity {

    Button chat, call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Support");
        }
        chat = findViewById(R.id.btn_support_chat);
        call = findViewById(R.id.btn_support_call);


        //Zoho Live Chat configurations
        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);
        ZohoSalesIQ.Chat.setThemeColor("colorPrimary", new Color(255, 192, 0));
        ZohoSalesIQ.Chat.setVisibility(ChatComponent.Operator_Image, true);
        ZohoSalesIQ.Chat.setMessage(SystemMessage.RATING_AND_FEEDBACK_COMPLETED, "Thanks for your Using our services");

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZohoSalesIQ.Chat.open();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ "18002090470"));
                startActivity(intent);
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
