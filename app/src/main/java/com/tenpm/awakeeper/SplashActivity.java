package com.tenpm.awakeeper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tenpm.awakeeper.model.CarData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getJSON();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 5000);
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    public void getJSON() {
        client.get("http://10pm.pythonanywhere.com/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resBody = new String(responseBody);
                Log.i("test", "jsonData: " + resBody);

                JsonArrayParse(resBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void JsonArrayParse(String resBody) {
        try {
            JSONArray jsonData = new JSONArray(resBody);

            for(int i=0; i<jsonData.length(); i++) {
                JSONObject jObj = jsonData.getJSONObject(i);

                MainActivity.carDataArrayList.add(new CarData(jObj.getDouble("gpsX"), jObj.getDouble("gpsY"),
                            jObj.getInt("velocity"), jObj.getDouble("angle"), jObj.getString("roadType")));
            }
        } catch(JSONException e) {
            Log.e("SplashActivity", "JSONArray ERROR! - " + e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
