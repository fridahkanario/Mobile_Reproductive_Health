package com.fusion.kim.m_reproductivehealth;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AntenatalCareActivity extends AppCompatActivity {

    //declaration of the layout variables
    private static WebView mWebview;
    private String url="https://www.pregnancybirthbaby.org.au/antenatal-care-and-classes";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antenatal_care);


        //Show the user that the page is loading
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Loading Web Page");
        progressDialog.setMessage("Please wait while the page finishes loading...");


        //load the web URL
        mWebview =findViewById(R.id.web);
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setBuiltInZoomControls(true);


        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(AntenatalCareActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();

                String webUrl = mWebview.getUrl();

            }


        });

        mWebview.loadUrl(url);

    }

}
