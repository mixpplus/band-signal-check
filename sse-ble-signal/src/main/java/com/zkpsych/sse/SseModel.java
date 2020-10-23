package com.zkpsych.sse;

/**
 * @Description :
 * @Author : ChangHao
 * @Date: 2020-10-12 17:59
 */
public class SseModel {
    private String id;
    private int rssi;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
