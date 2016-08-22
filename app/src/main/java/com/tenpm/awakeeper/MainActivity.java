package com.tenpm.awakeeper;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssomai.android.scalablelayout.ScalableLayout;
import com.tenpm.awakeeper.model.CarData;
import com.tenpm.awakeeper.model.SensorData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    public static ArrayList<CarData> carDataArrayList = new ArrayList<>();
    public static ArrayList<SensorData> sensorDataArrayList = new ArrayList<>();
    private static final int STATE_IDLE = 0;
    private static final int STATE_DRIVE = 1;
    private static final int STATE_DROWSY1 = 2;
    private static final int STATE_DROWSY2 = 3;
    private static final int STATE_SINGING = 4;
    public static int CurrentState = STATE_IDLE;

    private ScalableLayout mainBackground;
    private Button startScenarioButton;
    private Button prefButton;
    private TextView gpsText;
    private TextView velocityText;
    private TextView angleText;
    private TextView heartbeatText;
    private TextView irisText;
    private TextView stateText;
    private TextView timeText;
    private TextView coffeeText;
    private ImageView coffeeImg;
    private TextView rpmText;
    private TextView coText;
    private TextView windowOffsetText;

    private String songId;
    private boolean isSongSet = false;
    private AudioManager am;

    private int drowsyLevel = 0;

    private MainActivity thisActivity = this;
    private Timer timer;
    private Boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setLayout();
        setSounds();
        idleState();
    }

    private void setLayout(){
        mainBackground = (ScalableLayout)findViewById(R.id.mainBackground);
        gpsText = (TextView)findViewById(R.id.gpsText);
        velocityText = (TextView)findViewById(R.id.velocityText);
        angleText = (TextView)findViewById(R.id.angleText);
        heartbeatText = (TextView)findViewById(R.id.heartbeatText);
        irisText = (TextView)findViewById(R.id.irisText);
        stateText = (TextView)findViewById(R.id.stateText);
        timeText = (TextView)findViewById(R.id.timeText);
        coffeeText = (TextView)findViewById(R.id.coffeeText);
        coffeeImg = (ImageView)findViewById(R.id.coffeeImg);

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
                if(!isPlaying){
                    isPlaying = true;
                    startScenarioButton.setText("주행 정지");
                    playScenario();
                }
                else{
                    isPlaying = false;
                    startScenarioButton.setText("주행 시작");
                    stopScenario();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            isSongSet = true;
            songId = data.getStringExtra("songid");
            Log.i("MainActivity", "songid: " + songId);
        }
        else{
            isSongSet = false;
        }
    }

    private MediaPlayer mPlayer = null;

    private void setSounds(){
        String sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = "/sdcard/" + "경찰사이렌.mp3";
        // 오디오 파일을 로딩한다
        try {
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.siren2);
            mPlayer.setOnCompletionListener(mCompleteListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // 현재 볼륨 가져오기
        originalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    MediaPlayer.OnCompletionListener mCompleteListener =
            new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    Log.d(TAG, "music oncomplete");
                    if(CurrentState == STATE_SINGING){
                        Log.d(TAG, "music STATE_SINGING");
                        // 커피 쿠폰 주기
                        coffeeText.setVisibility(View.VISIBLE);
                        coffeeImg.setVisibility(View.VISIBLE);
                        // 이미지 터치 시 사라짐
                        coffeeImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                view.setVisibility(View.GONE);
                                coffeeText.setVisibility(View.GONE);
                                driveState();
                            }
                        });
                    }
                }
            };

    private void playMusic(){
        mPlayer.start();
    }

    private void stopMusic(){
        if(mPlayer != null && mPlayer.isPlaying())
        {
            mPlayer.pause();
            mPlayer.seekTo(0);
        }
    }

    public int currentTime = 0;
    public int totalTime = 0;

    private void playScenario(){
        currentTime = 0;
        // 시간에 따라서 데이터에 맞는 행동을 수행한다.
        totalTime = carDataArrayList.size();

        TimerTask myTask = new TimerTask() {
            public void run() {
                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 데이터값 ui에 표시
                        gpsText.setText("GPS: " + carDataArrayList.get(currentTime).getGpsX() + "," + carDataArrayList.get(currentTime).getGpsY());
                        velocityText.setText("속도: " + carDataArrayList.get(currentTime).getVelocity());
                        angleText.setText("핸들 각도: " + carDataArrayList.get(currentTime).getAngle());
                        heartbeatText.setText("심박수: " + sensorDataArrayList.get(currentTime).getHartBeat());
                        irisText.setText("홍채값: " + sensorDataArrayList.get(currentTime).getIris());
                        timeText.setText("시간: " + currentTime + "초");
                        // 일산화탄소농도
                        // 창문 열어진 길이
                        // 엔진 rpm

                        if(CurrentState == STATE_IDLE && carDataArrayList.get(currentTime).getVelocity() > 0){
                            driveState();
                        }
                        else if(CurrentState != STATE_IDLE && carDataArrayList.get(currentTime).getVelocity() <= 0 && carDataArrayList.get(currentTime).getRPM() == 0){ // rpm이 0이어야 정지
                            idleState();
                        }
                        else if(CurrentState == STATE_DRIVE && detectDrowsy(currentTime)){
                            // 졸음운전 확인
                            if(drowsyLevel == 0) {
                                drowsyLevel = 1;
                                drowsyLevel1();
                            }
                            else if(drowsyLevel == 1){
                                drowsyLevel = 2;
                                drowsyLevel2();
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
                            timer.cancel();
                        }

                        currentTime++;
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(myTask, 0, 1000);
    }

    private void stopScenario(){
        timer.cancel();
        currentTime = 0;
        totalTime = 0;
        idleState();
    }

    private void idleState(){
        CurrentState = STATE_IDLE;
        resumeVolume();
        stateText.setText(getString(R.string.idle_text));
        prefButton.setVisibility(View.VISIBLE);
        stopMusic();
        drowsyLevel = 0;
        mainBackground.setBackgroundColor(Color.rgb(189,189,189));
    }

    private void driveState(){
        CurrentState = STATE_DRIVE;
        resumeVolume();
        stateText.setText(getString(R.string.drive_text));
        prefButton.setVisibility(View.GONE);
        stopMusic();
        mainBackground.setBackgroundColor(Color.rgb(36 ,120,255));
    }

    private boolean detectDrowsy(int currentTime){ // RPM이 0이 되어야 시동이 꺼졌다고 확인함 아니면 신호등 정지도 있음
        if(currentTime >= 20 && currentTime <= 70)
            return true;

        return false;
    }

    private void drowsyLevel1(){
        setVolumeMax();
        playMusic();
        CurrentState = STATE_DROWSY1;
        stateText.setText(getString(R.string.drowsy_text1));
        mainBackground.setBackgroundColor(Color.rgb(255,54,54));
        TimerTask myTask = new TimerTask() {
            public void run() {
                stopMusic();
                CurrentState = STATE_DRIVE;
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 5000);
    }

    private void drowsyLevel2(){
        CurrentState = STATE_DROWSY2;
        stateText.setText(getString(R.string.drowsy_text2));
        mainBackground.setBackgroundColor(Color.rgb(99,36,189));
        stopMusic();
        if(isSongSet) // 음악 설정시 노래 부르기 시작
            playSinging();
        else // 없으면 정지할 때 까지 사이렌 계속됨
            sirenUnlessStop();
    }

    private void playSinging(){
        Log.d(TAG, "playsinging");
        stateText.setText(R.string.singing_text);
        CurrentState = STATE_SINGING;
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.big_boy);
        mPlayer.setOnCompletionListener(mCompleteListener);
        playMusic();
    }

    private int originalVolume = 0;

    private void setVolumeMax(){
        Log.d(TAG, "max vol: " + am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
    }

    private void resumeVolume(){
        am.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, AudioManager.FLAG_PLAY_SOUND);
    }

    private void sirenUnlessStop(){
        Log.d(TAG, "sirenUnlessStop");
    }

    private void turnOnAirConditioner(){

    }

    private void turnOffAirConditioner(){

    }

    private void moveWindow(int move){
        // mm단위
        // 창문이 mm이동했다고 노티
        Toast.makeText(getApplicationContext(), "환기를 위하여 창문이 " + move + "mm 열립니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        mPlayer.release();
        mPlayer = null;
    }
}
