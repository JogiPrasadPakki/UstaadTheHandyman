package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.util.GlobalFields;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Browser extends AppCompatActivity {

    WebView webView;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    int InvoiceId,type;
    WebLoader webLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        type = getIntent().getIntExtra("browserType",1);
        InvoiceId = getIntent().getIntExtra("InvoiceId",0);
        progressBar = findViewById(R.id.pb_webview);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressDialog.dismiss();
                }
            }
        });
        switch (type){
            case 1:
                //Privay
                getSupportActionBar().setTitle("Privacy");
                webLoader = new WebLoader();
                webLoader.execute("https://ustaadthehandyman.com/privacy/");
                break;
            case 2:
                //Terms
                getSupportActionBar().setTitle("Terms & Conditions");
                webLoader = new WebLoader();
                webLoader.execute("https://ustaadthehandyman.com/terms/");
                break;
            case 3:
                //About
                getSupportActionBar().setTitle("Privacy Policy");
                webLoader = new WebLoader();
                webLoader.execute("https://ustaadthehandyman.com/about/");
                break;
            case 4:
                //Invoice
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressDialog = new ProgressDialog(Browser.this, ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Loading");
                    progressDialog.show();
                }
                getSupportActionBar().setTitle("Invoice");
                webView.loadUrl(GlobalFields.BackendUrl+"/api/service/invoice?id="+InvoiceId);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(type == 4){
            getMenuInflater().inflate(R.menu.invoice_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_print:
                printInvoice();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void printInvoice() {

        //create object of print manager in your device
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        //create object of print adapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        //provide name to your newly generated pdf file
        String jobName = getString(R.string.app_name) + InvoiceId;

        //open print dialog
        assert printManager != null;
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    private class WebLoader extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressDialog = new ProgressDialog(Browser.this, ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Loading");
                progressDialog.show();
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            try {
                Document document = Jsoup.connect(url).get();
                Elements data = document.select("div#page-content");
                return  data.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            webView.loadData(s,"text/html","utf-8");
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressDialog.dismiss();
            }
        }

    }
}
