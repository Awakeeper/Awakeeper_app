package com.tenpm.awakeeper.model;

/**
 * Created by ChaosControl on 2016. 8. 22..
 */
public class CarData {
    private double gpsX;
    private double gpsY;
    private int velocity;
    private double angle;
    private String roadType;
    private double CO;
    private int windowOffset;
    private int RPM;
    private int aircondition;

    public CarData(double gpsX, double gpsY, int velocity, double angle, String roadType, double CO,
                   int windowOffset, int RPM, int aircondition) {
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.velocity = velocity;
        this.angle = angle;
        this.roadType = roadType;
        this.CO = CO;
        this.windowOffset = windowOffset;
        this.RPM = RPM;
        this.aircondition = aircondition;
    }

    public double getGpsX() {
        return gpsX;
    }

    public void setGpsX(double gpsX) {
        this.gpsX = gpsX;
    }

    public double getGpsY() {
        return gpsY;
    }

    public void setGpsY(double gpsY) {
        this.gpsY = gpsY;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public double getCO() {
        return CO;
    }

    public void setCO(double CO) {
        this.CO = CO;
    }

    public int getWindowOffset() {
        return windowOffset;
    }

    public void setWindowOffset(int windowOffset) {
        this.windowOffset = windowOffset;
    }

    public int getRPM() {
        return RPM;
    }

    public void setRPM(int RPM) {
        this.RPM = RPM;
    }

    public int getAircondition() {
        return aircondition;
    }

    public void setAircondition(int aircondition) {
        this.aircondition = aircondition;
    }
}
