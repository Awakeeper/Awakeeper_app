package com.tenpm.awakeeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PreferenceActivity extends AppCompatActivity {
    private int RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        int songid = MainActivity.soundPool.load(this, R.raw.big_boy, 1);


        Intent intent = new Intent();
        intent.putExtra("songid", songid);
        setResult(RESULT_OK, intent);
        finish();
    }
}
