package com.minionhut.michael.mdp_hack.CalSQL;

/**
 * Created by Kyle on 10/21/2014.
 */
public class CalSQLObjWiFi {
    private int rowID;
    private int channel;
    private int RSSI;
    private String SSID;
    private String BSSID;


    public CalSQLObjWiFi(String SSID, String BSSID, int channel, int RSSI) {
       this.setSSID(SSID);
       this.setBSSID(BSSID);
       this.setChannel(channel);
       this.setRSSI(RSSI);
    }


    public CalSQLObjWiFi(int rowID, String SSID, String BSSID, int channel, int RSSI) {
        this.rowID = rowID;

        this.SSID = SSID;
        this.BSSID = BSSID;
        this.channel = channel;
        this.RSSI =RSSI;
    }

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }
}