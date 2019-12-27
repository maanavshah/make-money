package com.maanavshah.makemoney;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maanavshah.makemoney.Helper.HttpGetRequest;
import com.maanavshah.makemoney.Helper.SharedConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

//    public static String LOGIN_REQUEST_URL = "http://10.0.2.2:3000/api/users/sign_in";
//    public static String SET_COINS_URL = "http://10.0.2.2:3000/api/users/set_coins";
//    public static String GET_COINS_URL = "http://10.0.2.2:3000/api/users/get_coins";
    public static String LOGIN_REQUEST_URL = "https://makemoneyadmin.herokuapp.com/api/users/sign_in";
    public static String SET_COINS_URL = "https://makemoneyadmin.herokuapp.com/api/users/set_coins";
    public static String GET_COINS_URL = "https://makemoneyadmin.herokuapp.com/api/users/get_coins";



    private EditText et_email;
    private EditText et_password;
    private String email;
    private String password;
    private StringBuilder stringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.login_email);
        et_password = findViewById(R.id.login_password);

        Button new_register = findViewById(R.id.button_new_register);
        new_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                finish();
            }
        });
        Button login = findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    sendLoginRequest();
                }
            }
        });
    }

    private void sendLoginRequest() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(LOGIN_REQUEST_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    getInputData();

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("email", email);
                    jsonParam.put("password", password);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    String responseCode = String.valueOf(conn.getResponseCode());

                    InputStream inputStream;
                    if (responseCode.equals("200")) {
                        inputStream = conn.getInputStream();
                    } else {
                        inputStream = conn.getErrorStream();
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    final StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    conn.disconnect();

                    if (responseCode.equals("200")) {
                        SharedConfig.setConfig(getApplicationContext(), "email", email);
                        String coins = SharedConfig.getConfig(getApplicationContext(), email);
                        if (coins.isEmpty()) {
                            stringBuilder = new StringBuilder(GET_COINS_URL);
                            stringBuilder.append("?email=");
                        } else {
                            stringBuilder = new StringBuilder(SET_COINS_URL);
                            stringBuilder.append("?coins=");
                            stringBuilder.append(URLEncoder.encode(coins, "UTF-8"));
                            stringBuilder.append("&email=");
                        }

                        stringBuilder.append(URLEncoder.encode(email, "UTF-8"));
                        HttpGetRequest getRequest = new HttpGetRequest();
                        coins = getRequest.execute(stringBuilder.toString()).get();

                        if (coins != null) {
                            SharedConfig.setConfig(getApplicationContext(), email, coins);
                        } else {
                            Toast.makeText(getApplicationContext(), "User email not found!", Toast.LENGTH_LONG).show();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private boolean isValid() {
        getInputData();
        boolean flag = true;
        if (isEmptyText(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email id!", Toast.LENGTH_SHORT).show();
            et_email.setError("Email is required");
            flag = false;
        }
        if (isEmptyText(password)) {
            et_password.setError("Password is required");
            flag = false;
        }
        return flag;
    }

    private boolean isEmptyText(String text) {
        return TextUtils.isEmpty(text);
    }

    private void getInputData() {
        email = et_email.getText().toString();
        password = et_password.getText().toString();
    }
}
