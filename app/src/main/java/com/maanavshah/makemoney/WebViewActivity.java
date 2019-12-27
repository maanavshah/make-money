package com.maanavshah.makemoney;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maanavshah.makemoney.Helper.HttpGetRequest;
import com.maanavshah.makemoney.Helper.SharedConfig;

import java.util.concurrent.ExecutionException;

import im.delight.android.webview.AdvancedWebView;

public class WebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    //    private static final String GET_CONFIG = "http://10.0.2.2:3000/api/users/get_config";
    private static final String GET_CONFIG = "https://makemoneyadmin.herokuapp.com/api/users/get_config";

    private AdvancedWebView mWebView;
    private Handler handler1;
    private Runnable eachMinute;
    private String webview_interval;
    private String add_coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        StringBuilder stringBuilder = new StringBuilder(GET_CONFIG);
        stringBuilder.append("?name=webview_url");
        HttpGetRequest getRequest = new HttpGetRequest();
        String webview_url = null;
        try {
            webview_url = getRequest.execute(stringBuilder.toString()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (webview_url == null) {
            webview_url = "http://www.google.com/";
        }

        mWebView = findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.loadUrl(webview_url);

        stringBuilder = new StringBuilder(GET_CONFIG);
        stringBuilder.append("?name=webview_interval");
        getRequest = new HttpGetRequest();
        webview_interval = null;
        try {
            webview_interval = getRequest.execute(stringBuilder.toString()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        add_coins = SharedConfig.getConfig(getApplicationContext(), "add_coins");

        if (webview_interval != null) {
            HandlerThread hThread = new HandlerThread("HandlerThread");
            hThread.start();
            handler1 = new Handler(hThread.getLooper());
            final long oneMinuteMs = Integer.valueOf(webview_interval) * 1000;

            eachMinute = new Runnable() {
                @Override
                public void run() {
                    Log.d("INCREASING", "Each minute task executing");
                    String email = SharedConfig.getConfig(getApplicationContext(), "email");
                    String coins = SharedConfig.getConfig(getApplicationContext(), email);
                    coins = String.valueOf(Integer.valueOf(coins) + Integer.valueOf(add_coins));
                    Log.d("INCREASING", coins);
                    SharedConfig.setConfig(getApplicationContext(), email, coins);
                    handler1.postDelayed(this, oneMinuteMs);
                }
            };
            handler1.postDelayed(eachMinute, oneMinuteMs);
        } else {
            Toast.makeText(getApplicationContext(), "Unable to fetch reward limit. Please try again later!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        stopExecuting();
        mWebView.onPause();
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        super.onPause();
    }

    private void stopExecuting() {
        if (webview_interval != null) {
            handler1.removeCallbacks(eachMinute);
        }
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }
}
