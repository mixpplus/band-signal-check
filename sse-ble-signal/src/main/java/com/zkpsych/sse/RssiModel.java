package com.zkpsych.sse;

/**
 * @Description : rssi model
 * @Author : ChangHao
 * @Date: 2020-10-28 15:46
 */
public class RssiModel {
    private int rssi;
    private long timeStamp;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
