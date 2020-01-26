package com.example.iballbaton.ui.traffic;

public class TrafficModel {
    private String ip_addr,uplink,downlink, sentMsg, sentBytes, receivedMsg, receivedBytes;

    public TrafficModel(String[] strings) {
        ip_addr = strings[0];
        uplink = strings[1];
        downlink = strings[2];
        sentMsg = strings[3];
        sentBytes = strings[4];
        receivedMsg = strings[5];
        receivedBytes = strings[6];
    }

    public String getIp_addr() {
        return ip_addr;
    }

    public void setIp_addr(String ip_addr) {
        this.ip_addr = ip_addr;
    }

    public String getUplink() {
        return uplink;
    }

    public void setUplink(String uplink) {
        this.uplink = uplink;
    }

    public String getDownlink() {
        return downlink;
    }

    public void setDownlink(String downlink) {
        this.downlink = downlink;
    }

    public String getSentMsg() {
        return sentMsg;
    }

    public void setSentMsg(String sentMsg) {
        this.sentMsg = sentMsg;
    }

    public String getSentBytes() {
        return sentBytes;
    }

    public void setSentBytes(String sentBytes) {
        this.sentBytes = sentBytes;
    }

    public String getReceivedMsg() {
        return receivedMsg;
    }

    public void setReceivedMsg(String receivedMsg) {
        this.receivedMsg = receivedMsg;
    }

    public String getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(String receivedBytes) {
        this.receivedBytes = receivedBytes;
    }
}
