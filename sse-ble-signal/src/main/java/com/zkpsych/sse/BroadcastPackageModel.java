package com.zkpsych.sse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BroadcastPackageModel implements Serializable {
    private static final long serialVersionUID = -902328083506067695L;

    private String adData;
    private String name;
    /**
     * 信号强度
     */
    private int rssi;
    /**
     * 一般有两个值
     * 0: 可连接
     * 3: 不可连接
     */
    private int evtType;

    public String getAdData() {
        return adData;
    }

    public void setAdData(String adData) {
        this.adData = adData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEvtType() {
        return evtType;
    }

    public void setEvtType(int evtType) {
        this.evtType = evtType;
    }

    public int getRssi() {
        if (rssi == 0) {
            return -1000;
        }
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
