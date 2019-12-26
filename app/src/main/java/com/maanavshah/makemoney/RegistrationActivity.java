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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrationActivity extends AppCompatActivity {


    //    TODO::MAANAV:: Change this url
    public static String REGISTER_REQUEST = "http://10.0.2.2:3000/api/users";
//    public static String REGISTER_REQUEST = "https://makemoneyadmin.herokuapp.com/api/users";

    private EditText et_first_name;
    private EditText et_last_name;
    private EditText et_email;
    private EditText et_password;
    private EditText et_confirm_password;
    private Button register;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        et_first_name = findViewById(R.id.register_first_name);
        et_last_name = findViewById(R.id.register_last_name);
        et_email = findViewById(R.id.register_email);
        et_password = findViewById(R.id.register_password);
        et_confirm_password = findViewById(R.id.register_password_confirm);

        register = findViewById(R.id.button_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    sendRegisterRequest();
                }
            }
        });
    }

    private boolean isValid() {
        getInputData();
        boolean flag = true;
        if (isEmptyText(first_name)) {
            et_first_name.setError("First name is required");
            flag = false;
        }
        if (isEmptyText(last_name)) {
            et_last_name.setError("Last name is required");
            flag = false;
        }
        if (isEmptyText(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email id!", Toast.LENGTH_SHORT).show();
            et_email.setError("Email is required");
            flag = false;
        }
        if (isEmptyText(password)) {
            et_password.setError("Password is required");
            flag = false;
        }
        if (isEmptyText(confirm_password)) {
            et_confirm_password.setError("Password confirmation is required");
            flag = false;
        }
        if (!password.equals(confirm_password)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }

    private boolean isEmptyText(String text) {
        return TextUtils.isEmpty(text);
    }

    private void getInputData() {
        first_name = et_first_name.getText().toString();
        last_name = et_last_name.getText().toString();
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        confirm_password = et_confirm_password.getText().toString();
    }

    public void sendRegisterRequest() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(REGISTER_REQUEST);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    getInputData();

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("first_name", first_name);
                    jsonParam.put("last_name", last_name);
                    jsonParam.put("email", email);
                    jsonParam.put("password", password);
                    jsonParam.put("confirm_password", confirm_password);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    String responseCode = String.valueOf(conn.getResponseCode());

                    InputStream inputStream;
                    if (responseCode.equals("201")) {
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

                    if (responseCode.equals("201")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
}
