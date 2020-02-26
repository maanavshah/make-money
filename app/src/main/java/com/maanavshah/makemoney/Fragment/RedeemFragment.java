package com.maanavshah.makemoney.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.maanavshah.makemoney.Adapter.AdMobNativeAdapter;
import com.maanavshah.makemoney.Header;
import com.maanavshah.makemoney.Helper.HttpGetRequest;
import com.maanavshah.makemoney.Helper.SharedConfig;
import com.maanavshah.makemoney.NavigationActivity;
import com.maanavshah.makemoney.R;

import java.util.concurrent.ExecutionException;

public class RedeemFragment extends Fragment {

    //    private static final String REDEEM_COINS_URL = "http://10.0.2.2:3000/api/users/redeem_coins";
    private static final String REDEEM_COINS_URL = "https://makemoneyadmin.herokuapp.com/api/users/redeem_coins";

    private EditText et_coins;
    private String mobile_number;
    private EditText et_mobile_number;
    private String coins;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_redeem, container, false);

        AdMobNativeAdapter adMobNativeAdapter = new AdMobNativeAdapter(this, view);
        adMobNativeAdapter.setupNativeAdMob();

        final String email = SharedConfig.getConfig(getContext(), "email");
        coins = SharedConfig.getConfig(getContext(), email);
        Header header = view.findViewById(R.id.header);
        header.initHeader(coins);

        et_coins = view.findViewById(R.id.et_coins);
        Button btn_redeem = view.findViewById(R.id.btn_redeem);
        et_mobile_number = view.findViewById(R.id.et_mobile_number);

        btn_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_coins = et_coins.getText().toString();
                mobile_number = et_mobile_number.getText().toString();
                if (input_coins.isEmpty()) {
                    et_coins.setError("Enter coins");
                } else if (mobile_number.isEmpty() || !android.util.Patterns.PHONE.matcher(mobile_number).matches() || mobile_number.length() != 10) {
                    et_mobile_number.setError("Enter valid phone number");
                    Toast.makeText(getContext(), "Please enter a valid phone number to collect reward!", Toast.LENGTH_LONG).show();
                } else {
                    Integer request_coins = Integer.valueOf(input_coins);
                    Integer current_coins = Integer.valueOf(coins);
                    if (request_coins > 0 && request_coins <= current_coins) {
                        StringBuilder stringBuilder = new StringBuilder(REDEEM_COINS_URL);
                        stringBuilder.append("?collect_coins=").append(input_coins);
                        stringBuilder.append("&mobile_number=").append(mobile_number);
                        stringBuilder.append("&email=").append(email);
                        HttpGetRequest getRequest = new HttpGetRequest();
                        try {
                            String result = getRequest.execute(stringBuilder.toString()).get();
                            if (result == null || result.isEmpty()) {
                                Toast.makeText(getContext(), "Error submitting redeem request!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Redeem request submitted successfully!", Toast.LENGTH_LONG).show();
                                Log.d("Reducing value to", String.valueOf(Integer.valueOf(coins) - request_coins));
                                SharedConfig.setConfig(getContext(), email, String.valueOf(Integer.valueOf(coins) - request_coins));
                                startActivity(new Intent(getContext(), NavigationActivity.class));
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), "You do not have sufficient balance", Toast.LENGTH_LONG).show();
                        et_coins.setError("Insufficient coins");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
