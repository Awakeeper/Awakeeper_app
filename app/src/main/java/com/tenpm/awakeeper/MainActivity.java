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
    private TextView timeText;

    public static SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    private int soundId;
    private int songId;
    private boolean isSongSet = false;

    private int drowsyLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

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
        timeText = (TextView)findViewById(R.id.timeText);

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
            songId = data.getIntExtra("songid", 0);
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
        currentTime = 0;
        // 시간에 따라서 데이터에 맞는 행동을 수행한다.
        totalTime = carDataArrayList.size();

        TimerTask myTask = new TimerTask() {
            public void run() {
                // 데이터값 ui에 표시
                gpsText.setText("GPS: " + carDataArrayList.get(currentTime).getGpsX() + "," + carDataArrayList.get(currentTime).getGpsY());
                velocityText.setText("속도: " + carDataArrayList.get(currentTime).getVelocity());
                angleText.setText("핸들 각도: " + carDataArrayList.get(currentTime).getAngle());
                heartbeatText.setText("심박수 " + sensorDataArrayList.get(currentTime).getHartBeat());
                irisText.setText("홍채값: " + sensorDataArrayList.get(currentTime).getIris());
                timeText.setText("시간: " + currentTime + "초");

                if(CurrentState == STATE_IDLE && carDataArrayList.get(currentTime).getVelocity() > 0){
                    driveState();
                }
                else if(CurrentState != STATE_IDLE && carDataArrayList.get(currentTime).getVelocity() <= 0){
                    idleState();
                }
                else if(CurrentState == STATE_DRIVE && detectDrowsy(currentTime)){
                    // 졸음운전 확인
                    if(drowsyLevel == 0) {
                        drowsyLevel1();
                        drowsyLevel = 1;
                    }
                }
                else if(CurrentState == STATE_DRIVE && drowsyLevel == 1){
                    drowsyLevel = 2;
                    drowsyLevel2();
                }
                else if((CurrentState == STATE_DROWSY1 || CurrentState == STATE_DROWSY2) && !detectDrowsy(currentTime)){
                    // 졸음 깬 경우
                    driveState();
                }

                if(currentTime >= totalTime){ // 시나리오 끝
                    idleState();
                    this.cancel();
                }

                currentTime++;
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 0, 1000); // 5초후 첫실행, 1초마다 계속실행
    }

    private void idleState(){
        CurrentState = STATE_IDLE;
        stateText.setText(getString(R.string.idle_text));
        prefButton.setVisibility(View.VISIBLE);
        soundPool.stop(soundId);
        soundPool.stop(songId);
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

        TimerTask myTask = new TimerTask() {
            public void run() {
                soundPool.stop(soundId);
                CurrentState = STATE_DRIVE;
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 10000);
    }

    private void drowsyLevel2(){
        soundPool.play(soundId, 1, 1, 0, 0, 1);
        CurrentState = STATE_DROWSY2;
        TimerTask myTask = new TimerTask() {
            public void run() {
                soundPool.stop(soundId);
                if(isSongSet) // 음악 설정시 노래 부르기 시작
                    playSinging();
                else // 없으면 정지할 때 까지 사이렌 계속됨
                    sirenUnlessStop();
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

    private void sirenUnlessStop(){

    }
}
