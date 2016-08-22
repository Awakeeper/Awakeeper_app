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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getSupportActionBar().hide();

        File root = new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath());
        ListDir(root);

    }

    void ListDir(File f){
        path = new File(f.getPath() + "/Music");
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
}