package com.maanavshah.makemoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.maanavshah.makemoney.Helper.SharedConfig;

import java.util.Timer;
import java.util.TimerTask;

public class WatchVideoActivity extends AppCompatActivity implements RewardedVideoAdListener, OnProgressBarListener {

    // private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"; // test
    private static final String AD_UNIT_ID = "ca-app-pub-6248472392921579/2934641521";

    private RewardedVideoAd rewardedVideoAd;
    private Button showVideoButton;
    private NumberProgressBar bnp;
    private TextView tv_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        tv_retry = findViewById(R.id.tv_retry);

        loadRewardedVideoAd();
        Toast.makeText(this, "Loading Reward Video!", Toast.LENGTH_SHORT).show();

        bnp = findViewById(R.id.number_progress_bar);
        bnp.setOnProgressBarListener(this);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bnp.getProgress() < 99) {
                            bnp.incrementProgressBy(1);
                        }
                    }
                });
            }
        }, 1000, 100);

        // Create the "show" button, which shows a rewarded video if one is loaded.
        showVideoButton = findViewById(R.id.show_video_button);
        showVideoButton.setVisibility(View.INVISIBLE);
        showVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedVideo();
            }
        });
    }

    private void showRewardedVideo() {
        showVideoButton.setVisibility(View.INVISIBLE);
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        } else {
            loadRewardedVideoAd();
            Toast.makeText(getApplicationContext(), "Loading Ad!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
            if (tv_retry != null) {
                tv_retry.setVisibility(View.VISIBLE);
            }
            if (bnp != null) {
                bnp.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        bnp.setProgress(100);
        showVideoButton.setVisibility(View.VISIBLE);
        tv_retry.setVisibility(View.INVISIBLE);
        bnp.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Preload the next video ad.
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem reward) {
        String add_coins = SharedConfig.getConfig(getApplicationContext(), "add_coins");
        String email = SharedConfig.getConfig(getApplicationContext(), "email");
        String coins = SharedConfig.getConfig(getApplicationContext(), email);
        SharedConfig.setConfig(getApplicationContext(), email, String.valueOf(Integer.valueOf(coins) + Integer.valueOf(add_coins)));
        Toast.makeText(getApplicationContext(), "Rewarded " + add_coins + " coins! Watch more to win more!", Toast.LENGTH_LONG).show();
        bnp.setProgress(0);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }

    @Override
    public void onProgressChange(int current, int max) {
        if (current == max) {
            bnp.setProgress(0);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        finish();
        rewardedVideoAd = null;
        onDestroy();
        super.onBackPressed();
    }
}
