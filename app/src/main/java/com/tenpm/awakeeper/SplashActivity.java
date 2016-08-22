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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;
    private Runnable mRunnable;
    private final int PERMISSION_INTERNET = 99;
    private final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //getSupportActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onresume");
        //checkPermission();
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
        //mHandler.postDelayed(mRunnable, 5000);
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

//    @TargetApi(Build.VERSION_CODES.M)
//    private void checkPermission() {
//        Log.i(TAG, "CheckPermission : " + checkSelfPermission(Manifest.permission.INTERNET));
//        if (checkSelfPermission(Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "checkPermission if");
//            // Should we show an explanation?
//            if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
//                // Explain to the user why we need to write the permission.
//                Toast.makeText(this, "인터넷 연결", Toast.LENGTH_SHORT).show();
//            }
//            Log.d(TAG, "requestPermissions");
//            requestPermissions(new String[]{Manifest.permission.INTERNET},
//                    PERMISSION_INTERNET);
//            Log.d(TAG, "requestPermissions end");
//            // MY_PERMISSION_REQUEST_STORAGE is an
//            // app-defined int constant
//
//        } else {
//            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
//            Log.d(TAG, "getJson  granted");
//            //getJSON();
//        }
//    }



//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_INTERNET:
//                Log.d(TAG, "" + permissions.length);
//                Log.d(TAG, "" + grantResults.length);
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    //getJSON();
//
//                    // permission was granted, yay! do the
//                    // calendar task you need to do.
//
//                } else {
//
//                    Log.d(TAG, "Permission always deny");
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                break;
//        }
//    }
}
