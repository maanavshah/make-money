package com.maanavshah.makemoney;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class WatchVideoActivity extends AppCompatActivity implements RewardedVideoAdListener {


    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private RewardedVideoAd rewardedVideoAd;
    private Button retryButton;
    private Button showVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        loadRewardedVideoAd();
        Toast.makeText(this, "Loading Reward Video", Toast.LENGTH_SHORT).show();

        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Show another add", Toast.LENGTH_SHORT).show();
            }
        });

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
        }
    }


    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }


    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        showVideoButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();

        // Preload the next video ad.
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this,
                String.format(" onRewarded! currency: %s amount: %d", reward.getType(),
                        reward.getAmount()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }
}
