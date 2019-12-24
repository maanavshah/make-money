package com.maanavshah.makemoney.Helper;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpGetRequest extends AsyncTask<String, Void, String> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    private Context context;

    public HttpGetRequest(Context thisContext) {
        context = thisContext;
    }

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        String result;
        String inputLine;


        try {

            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);         //Create a connection
            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();         //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();         //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());         //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }         //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();         //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}