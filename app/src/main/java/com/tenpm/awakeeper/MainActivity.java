package com.tenpm.awakeeper;

import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tenpm.awakeeper.model.CarData;
import com.tenpm.awakeeper.model.SensorData;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<CarData> carDataArrayList = new ArrayList<>();
    public static ArrayList<SensorData> sensorDataArrayList = new ArrayList<>();
    private static final int STATE_IDLE = 0;
    private static final int STATE_DRIVE = 1;
    private static final int STATE_DROWSY1 = 2;
    private static final int STATE_DROWSY2 = 3;
    public static int CurrentState = STATE_IDLE;

    private Button startScenarioButton;
    private Button prefButton;
    private TextView gpsText;
    private TextView velocityText;
    private TextView angleText;
    private TextView heartbeatText;
    private TextView irisText;
    private TextView stateText;

    public static SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    private int soundId;
    private String songId;
    private boolean isSongSet = false;

    private int drowsyLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLayout();
        setSounds();
        idleState();
    }

    private void setLayout(){
        gpsText = (TextView)findViewById(R.id.gpsText);
        velocityText = (TextView)findViewById(R.id.velocityText);
        angleText = (TextView)findViewById(R.id.angleText);
        heartbeatText = (TextView)findViewById(R.id.heartbeatText);
        irisText = (TextView)findViewById(R.id.irisText);
        stateText = (TextView)findViewById(R.id.stateText);

        prefButton = (Button)findViewById(R.id.prefButton);
        prefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        startScenarioButton = (Button)findViewById(R.id.startScenarioButton);
        startScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playScenario();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            isSongSet = true;
            songId = data.getStringExtra("songid");
            Log.i("MainActivity", songId);
        }
        else{
            isSongSet = false;
        }
    }

    private void setSounds(){
        soundId = soundPool.load(this, R.raw.siren, 1);
    }

    public int currentTime = 0;
    public int totalTime = 0;

    private void playScenario(){
        // 시간에 따라서 데이터에 맞는 행동을 수행한다.
        totalTime = carDataArrayList.size();

        TimerTask myTask = new TimerTask() {
            public void run() {
                if(CurrentState == STATE_IDLE && carDataArrayList.get(currentTime).getVelocity() > 0){
                    driveState();
                }
                else if(CurrentState != STATE_IDLE && carDataArrayList.get(currentTime).getVelocity() <= 0){
                    idleState();
                }
                else if(CurrentState == STATE_DRIVE && drowsyLevel < 2 && detectDrowsy(currentTime)){
                    // 졸음운전 확인
                    if(drowsyLevel == 0){
                        drowsyLevel1();
                        drowsyLevel++;
                    }
                    else if(drowsyLevel == 1){
                        drowsyLevel2();
                    }
                }
                else if((CurrentState == STATE_DROWSY1 || CurrentState == STATE_DROWSY2) && !detectDrowsy(currentTime)){
                    driveState();
                }

                if(currentTime >= totalTime){
                    idleState();
                    this.cancel();
                }

                currentTime++;
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 0, 1000); // 5초후 첫실행, 1초마다 계속실행

        for(int i=0; i<carDataArrayList.size(); i++){

        }
    }

    private void idleState(){
        stateText.setText(getString(R.string.idle_text));
        prefButton.setVisibility(View.VISIBLE);
        soundPool.stop(soundId);
        drowsyLevel = 0;

    }

    private void driveState(){
        CurrentState = STATE_DRIVE;
        stateText.setText(getString(R.string.drive_text));
        prefButton.setVisibility(View.GONE);
        soundPool.stop(soundId);
    }

    private boolean detectDrowsy(int currentTime){

        return false;
    }

    private void drowsyLevel1(){
        soundPool.play(soundId, 1, 1, 0, 0, 1);
        CurrentState = STATE_DROWSY1;
    }

    private void drowsyLevel2(){
        soundPool.play(soundId, 1, 1, 0, 0, 1);
        CurrentState = STATE_DROWSY2;
        TimerTask myTask = new TimerTask() {
            public void run() {
                soundPool.stop(soundId);
                playSinging();
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 10000);
    }

    private void playSinging(){
        Intent intent = new Intent(getApplicationContext(), SingingActivity.class);
        intent.putExtra("songId", songId);
        startActivity(intent);
    }
}
