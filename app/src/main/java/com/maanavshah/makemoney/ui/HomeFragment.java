package com.maanavshah.makemoney.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.maanavshah.makemoney.Adapter.AdMobNativeAdapter;
import com.maanavshah.makemoney.Header;
import com.maanavshah.makemoney.Helper.HttpGetRequest;
import com.maanavshah.makemoney.Helper.SharedConfig;
import com.maanavshah.makemoney.R;
import com.maanavshah.makemoney.ScratchActivity;
import com.maanavshah.makemoney.WatchVideoActivity;
import com.maanavshah.makemoney.WebViewActivity;

import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    public static String GET_COINS_URL = "http://10.0.2.2:3000/api/users/get_coins";

    private View view;
    private String result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);


        HttpGetRequest getRequest = new HttpGetRequest(getContext());
        try {
            result = getRequest.execute(GET_COINS_URL).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (result != null) {
            SharedConfig.setConfig(getContext(), "coins", result);
            Header header = view.findViewById(R.id.header);
            Log.d("COINS1", result);
            header.initHeader(result);
        } else {
            Toast.makeText(getContext(), "User email not found!", Toast.LENGTH_LONG).show();
        }

        AdMobNativeAdapter adMobNativeAdapter = new AdMobNativeAdapter(this, view);
        adMobNativeAdapter.setupNativeAdMob();

        init();
        return view;
    }

    private void init() {
        Button btn_watch_video = view.findViewById(R.id.btn_watch_video);
        btn_watch_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), WatchVideoActivity.class));
            }
        });
        Button btn_scratch_card = view.findViewById(R.id.btn_scratch_card);
        btn_scratch_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), ScratchActivity.class));
            }
        });
        Button btn_web_view = view.findViewById(R.id.btn_web_view);
        btn_web_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), WebViewActivity.class));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
