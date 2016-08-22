package com.tenpm.awakeeper.model;

/**
 * Created by ChaosControl on 2016. 8. 22..
 */
public class FromWebData {
    private String carId;
    private int time;
    private CarData carData;
    private SensorData sensorData;

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public CarData getCarData() {
        return carData;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }
}
