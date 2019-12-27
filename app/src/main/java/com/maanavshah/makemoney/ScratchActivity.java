package com.maanavshah.makemoney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import in.myinnos.androidscratchcard.ScratchCard;

public class ScratchActivity extends AppCompatActivity implements RewardedVideoAdListener, OnProgressBarListener {

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private RewardedVideoAd rewardedVideoAd;
    private Button retryButton;
    private ScratchCard mScratchCard;
    private TextView tv_scratch_card;
    private NumberProgressBar bnp;
    private int randomNumber;
    private TextView tv_retry;
    private boolean showOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        Toast.makeText(this, "Loading Reward Video!", Toast.LENGTH_SHORT).show();

        tv_retry = findViewById(R.id.tv_retry);
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

        tv_scratch_card = findViewById(R.id.tv_scratch_card);
        mScratchCard = findViewById(R.id.view_scratch_card);
        showOnce = true;

        Random random = new Random();
        randomNumber = random.nextInt(10);
        tv_scratch_card.setText(randomNumber + " COINS");

        mScratchCard.setOnScratchListener(new ScratchCard.OnScratchListener() {
            @Override
            public void onScratch(ScratchCard scratchCard, float visiblePercent) {
                if (visiblePercent > 0.1) {
                    loadRewardedVideoAd();
                    mScratchCard.setVisibility(View.GONE);
                    add_reward_coins();
                    retryButton.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_scratch_card.setVisibility(View.GONE);
        mScratchCard.setVisibility(View.GONE);

        retryButton = findViewById(R.id.btn_show_reward);
        retryButton.setVisibility(View.GONE);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedVideo();
                Random random = new Random();
                randomNumber = random.nextInt(10);
                tv_scratch_card.setText(randomNumber + " COINS");
                tv_scratch_card.setVisibility(View.VISIBLE);
                mScratchCard.setVisibility(View.VISIBLE);
                mScratchCard.setBackgroundColor(0xFFC0C0C0);
            }
        });
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        bnp.setProgress(100);
        if (showOnce) {
            showRewardedVideo();
            showOnce = false;
        }
    }

    private void showRewardedVideo() {
        tv_scratch_card.setVisibility(View.GONE);
        mScratchCard.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);

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
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        tv_scratch_card.setVisibility(View.VISIBLE);
        mScratchCard.setVisibility(View.VISIBLE);
        bnp.setVisibility(View.GONE);
        tv_retry.setVisibility(View.GONE);
    }

    @Override
    public void onRewarded(RewardItem reward) {
        bnp.setVisibility(View.VISIBLE);
        tv_retry.setVisibility(View.VISIBLE);
        bnp.setProgress(0);
    }

    private void add_reward_coins() {
        String email = SharedConfig.getConfig(getApplicationContext(), "email");
        String coins = SharedConfig.getConfig(getApplicationContext(), email);
        String total_coins = String.valueOf(Integer.valueOf(coins) + randomNumber);
        Toast.makeText(getApplicationContext(), "Scratch Coins awarded: " + randomNumber + "!", Toast.LENGTH_LONG).show();
        SharedConfig.setConfig(getApplicationContext(), email, total_coins);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        finish();
        super.onBackPressed();
    }
}
