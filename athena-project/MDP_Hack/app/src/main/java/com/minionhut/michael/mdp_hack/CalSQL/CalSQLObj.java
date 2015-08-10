package com.minionhut.michael.mdp_hack.CalSQL;

/**
 * Created by Kyle on 10/21/2014.
 */
public class CalSQLObj {
    private long timestamp;
    private float xVal;
    private float yVal;
    private float zVal;
    private float proxVal;
    private float luxVal;
    private int isWalking;
    private int isTraining;
    private float deltaSteps;

    public CalSQLObj(float xVal, float yVal, float zVal, float proxVal, float luxVal, int isWalking, int isTraining, float deltaSteps, long timestamp) {
        this.setxVal(xVal);
        this.setyVal(yVal);
        this.setzVal(zVal);
        this.setProxVal(proxVal);
        this.setLuxVal(luxVal);
        this.setIsWalking(isWalking);
        this.setIsTraining(isTraining);
        this.setTimestamp(timestamp);
        this.setDeltaSteps(deltaSteps);
    }

    public CalSQLObj(float xVal, float yVal, float zVal, float proxVal, float luxVal, int isWalking, int isTraining, float deltaSteps) {
        this.xVal = xVal;
        this.yVal = yVal;
        this.zVal = zVal;
        this.proxVal = proxVal;
        this.luxVal = luxVal;
        this.isWalking = isWalking;
        this.isTraining = isTraining;
        this.timestamp = System.currentTimeMillis();
        this.deltaSteps = deltaSteps;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timeStamp) {
        this.timestamp = timeStamp;
    }

    public float getxVal() {
        return xVal;
    }

    public void setxVal(float xVal) {
        this.xVal = xVal;
    }

    public float getyVal() {
        return yVal;
    }

    public void setyVal(float yVal) {
        this.yVal = yVal;
    }

    public float getzVal() {
        return zVal;
    }

    public void setzVal(float zVal) {
        this.zVal = zVal;
    }

    public float getProxVal() {
        return proxVal;
    }

    public void setProxVal(float proxVal) {
        this.proxVal = proxVal;
    }

    public float getLuxVal() {
        return luxVal;
    }

    public void setLuxVal(float luxVal) {
        this.luxVal = luxVal;
    }

    public int getIsWalking() {
        return isWalking;
    }

    public void setIsWalking(int isWalking) {
        this.isWalking = isWalking;
    }

    public int getIsTraining() {
        return isTraining;
    }

    public void setIsTraining(int isTraining) {
        this.isTraining = isTraining;
    }

    public float getDeltaSteps() {
        return deltaSteps;
    }

    public void setDeltaSteps(float deltaSteps) {
        this.deltaSteps = deltaSteps;
    }
}