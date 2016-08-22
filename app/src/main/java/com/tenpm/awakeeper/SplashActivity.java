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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tenpm.awakeeper.model.CarData;
import com.tenpm.awakeeper.model.SensorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {
    private final int PERMISSION_INTERNET = 99;
    private final String TAG = "SplashActivity";
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        getJSON();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onresume");
        //checkPermission();
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    public void getJSON() {
        client.get("http://10pm.pythonanywhere.com/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resBody = new String(responseBody);
                Log.i("test", "jsonData: " + resBody);

                JsonArrayParse(resBody);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //finish();

                isLoaded = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isLoaded)
            finish();
    }

    public void JsonArrayParse(String resBody) {
        try {
            JSONArray jsonData = new JSONArray(resBody);

            for(int i=0; i<jsonData.length(); i++) {
                JSONObject jObj = jsonData.getJSONObject(i);

                MainActivity.carDataArrayList.add(new CarData(jObj.getDouble("gpsX"), jObj.getDouble("gpsY"),
                            jObj.getInt("velocity"), jObj.getDouble("angle"), jObj.getString("roadType"), 1, 0, 999, 0));
                MainActivity.sensorDataArrayList.add(new SensorData(80, 0.654)); // 임시 데이터
                //Log.i("SplashActivity", "getJSON");
            }
            Log.d(TAG, "carData len: " + MainActivity.carDataArrayList.size());
        } catch(JSONException e) {
            Log.e("SplashActivity", "JSONArray ERROR! - " + e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
