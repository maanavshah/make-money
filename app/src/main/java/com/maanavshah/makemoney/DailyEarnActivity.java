package com.maanavshah.makemoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DailyEarnActivity extends AppCompatActivity implements RewardedVideoAdListener, OnProgressBarListener {

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private RewardedVideoAd rewardedVideoAd;
    private View tv_retry;
    private NumberProgressBar bnp;
    private Button showVideoButton;
    private Date yesterday_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_earn);

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
                        } else {
                            cancel();
                        }
                    }
                });
            }
        }, 1000, 100);

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
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
    }

    @Override
    public void onRewarded(RewardItem reward) {
        add_daily_reward();
    }

    private void add_daily_reward() {
        String daily_reward_date = SharedConfig.getConfig(getApplicationContext(), "daily_reward_date");
        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String today_date = dateFormat.format(current_date);
        if (daily_reward_date == null || daily_reward_date.isEmpty()) {
            SharedConfig.setConfig(getApplicationContext(), "daily_reward_date", today_date);
        } else {
            try {
                yesterday_date = dateFormat.parse(daily_reward_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!yesterday_date.before(current_date)) {
                SharedConfig.setConfig(getApplicationContext(), "daily_reward_date", today_date);
                add_reward_coins();
            } else {
                Toast.makeText(getApplicationContext(), "Daily reward already collected!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void add_reward_coins() {
        String add_coins = SharedConfig.getConfig(getApplicationContext(), "add_coins");
        String email = SharedConfig.getConfig(getApplicationContext(), "email");
        String coins = SharedConfig.getConfig(getApplicationContext(), email);
        String total_coins = String.valueOf(Integer.valueOf(coins) + Integer.valueOf(add_coins));
        Toast.makeText(getApplicationContext(), "Daily reward awarded!", Toast.LENGTH_LONG).show();
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
