package com.demo.androidservicesdemo.activity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Rahul on 12/4/2016.
 */

public class DownloadData extends AsyncTask<String, Void, String> {

    private final String TAG = "DownloadData";
    private IJsonResponse iJsonResponse;
    private URL httpUrl;
    private HttpsURLConnection httpsURLConnection;

    public DownloadData(IJsonResponse iResponse) {
        iJsonResponse = iResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return getResult(params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        iJsonResponse.jsonResult(s);
    }

    private String getResult(String url) {
        try {
            httpUrl = new URL(url);
            httpsURLConnection = (HttpsURLConnection) httpUrl.openConnection();
            httpsURLConnection.setDoOutput(false);
            httpsURLConnection.setRequestMethod("GET");

            httpsURLConnection.setConnectTimeout(10000);
            Log.i(TAG, String.valueOf(httpsURLConnection.getResponseCode()));

            return readIt(httpsURLConnection.getInputStream(),10000);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return "";
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }

        return total.toString();
    }

}
