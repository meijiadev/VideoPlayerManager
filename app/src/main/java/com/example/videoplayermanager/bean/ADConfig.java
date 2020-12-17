package com.example.videoplayermanager.bean;

/**
 * 广告机参数配置
 */
public class ADConfig {
    private String IP="192.168.0.96";
    private String port="189";
    private String account="admin_ad1";
    private String password="admin_ad";
    private int videoTime=5;
    private float speed=1.0f;
    private long currentVideoFinish;     //当前视频播放完得时间戳

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPort() {
        return port;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAccount() {
        return account;
    }

    public String getIP() {
        return IP;
    }

    public String getPassword() {
        return password;
    }

    public void setVideoTime(int videoTime) {
        this.videoTime = videoTime;
    }

    public int getVideoTime() {
        return videoTime;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
}
