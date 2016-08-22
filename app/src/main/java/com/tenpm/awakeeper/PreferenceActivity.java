package com.tenpm.awakeeper;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreferenceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<String> fileList = new ArrayList<String>();
    private File path;
    private final String TAG = "PreferenceActivity";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private File root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getSupportActionBar().hide();

        root = new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath());
        checkPermission();
        //ListDir(root);

    }

    void ListDir(File f){
        //path = new File(f.getPath() + "/Music");
<<<<<<< HEAD
        path = new File(f.getPath());
        //path = new File("/sdcard/mp3");
=======
        path = new File("/sdcard");
>>>>>>> 2fd8f8460b42225716441d18fd1de4b6484938c1
        Log.d(TAG, "path: " + path);
        if(!path.exists()){
            return;
        }

        File[] files = path.listFiles();
        fileList.clear();
        for (File file : files){
            fileList.add(file.getName());
        }

        ListView listView = (ListView)findViewById(R.id.song_listView);

        ArrayAdapter<String> directoryList
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(directoryList);
        //setListAdapter(directoryList);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.putExtra("songid", path + "/" + fileList.get(i).toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        Log.i(TAG, "CheckPermission : " + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            Log.e(TAG, "permission deny");
            //writeFile();
            ListDir(root);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    //writeFile();
                    ListDir(root);
                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d(TAG, "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
}