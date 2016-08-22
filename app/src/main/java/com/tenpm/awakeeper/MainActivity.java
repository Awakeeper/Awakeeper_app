package com.tenpm.awakeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tenpm.awakeeper.model.CarData;
import com.tenpm.awakeeper.model.SensorData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static ArrayList<CarData> carDataArrayList = new ArrayList<CarData>();
    private static ArrayList<SensorData> sensorDataArrayList = new ArrayList<SensorData>();
    private static final int STATE_IDLE = 0;
    private static final int STATE_DRIVE = 1;
    private static final int STATE_DROWSY1 = 2;
    private static final int STATE_DROWSY2 = 3;
    public static int CurrentState = STATE_IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLayout();
    }

    private void setLayout(){

    }

    private void playScenario(){
        // 시간에 따라서 데이터에 맞는 행동을 수행한다.
    }

    private void idleState(){

    }

    private void driveState(){

    }

    private void detectDrowsy(){

    }

    private void drowsyLevel1(){

    }

    private void drowsyLevel2(){

    }

    private void playSinging(){

    }
}
