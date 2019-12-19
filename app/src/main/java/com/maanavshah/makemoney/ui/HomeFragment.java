package com.maanavshah.makemoney.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.maanavshah.makemoney.Adapter.AdMobNativeAdapter;
import com.maanavshah.makemoney.R;
import com.maanavshah.makemoney.WatchVideoActivity;

public class HomeFragment extends Fragment {

    private Button btn_watch_video;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        AdMobNativeAdapter adMobNativeAdapter = new AdMobNativeAdapter(this, view);
        adMobNativeAdapter.setupNativeAdMob();

        init();
        return view;
    }

    private void init() {
        btn_watch_video = view.findViewById(R.id.btn_watch_video);
        btn_watch_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), WatchVideoActivity.class));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
    }
}
