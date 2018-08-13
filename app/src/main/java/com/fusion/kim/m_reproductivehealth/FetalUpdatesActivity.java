package com.fusion.kim.m_reproductivehealth;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FetalUpdatesActivity extends AppCompatActivity {

    WebView web;
    private ProgressDialog progressDialog;
    String URL="https://www.babycenter.com/pregnancy-week-by-week";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetal_updates);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Loading Web Page");
        progressDialog.setMessage("Please wait while the page finishes loading...");

        web=findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true); // enable javascript

      web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setBuiltInZoomControls(true);


        web.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(FetalUpdatesActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
               progressDialog.show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();

                String webUrl = web.getUrl();

            }


        });

       web.loadUrl(URL);

    }
}

