package com.maanavshah.makemoney;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mrgames13.jimdo.splashscreen.App.SplashScreenBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SplashScreenBuilder.Companion.getInstance(this)
                .setVideo(R.raw.splash_animation)
                .setImage(R.drawable.app_icon)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SplashScreenBuilder.Companion.getSPLASH_SCREEN_FINISHED()) {
            if (resultCode == RESULT_OK) {
                // SplashScreen finished without manual canceling
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                // SplashScreen finished through manual canceling
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
