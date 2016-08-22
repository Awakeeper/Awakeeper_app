package com.tenpm.awakeeper.model;

/**
 * Created by ChaosControl on 2016. 8. 22..
 */
public class SensorData {
    private int heartBeat;
    private double iris;

    public SensorData(int heartBeat, double iris){
        this.heartBeat = heartBeat;
        this.iris = iris;
    }

    public int getHartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public double getIris() {
        return iris;
    }

    public void setIris(double iris) {
        this.iris = iris;
    }
}
