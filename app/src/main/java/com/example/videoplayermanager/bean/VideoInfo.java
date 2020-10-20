package com.example.videoplayermanager.bean;

import androidx.annotation.Nullable;

/**
 * desc:视频信息
 */
public class VideoInfo {
    private String name;
    private int putMode;
    private String businessInfo;
    private int adType;
    private String url;
    private String floor;
    private String number;
    private String logo;
    private String programNum;
    private int duration;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this==null||obj==null){
            return false;
        }

        if (obj.getClass()!=this.getClass()){
            return false;
        }
        VideoInfo videoInfo= (VideoInfo) obj;
        if (this.url.equals(videoInfo.url)&&this.logo.equals(videoInfo.logo)){
            return true;
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getAdType() {
        return adType;
    }

    public void setBusinessInfo(String businessInfo) {
        this.businessInfo = businessInfo;
    }

    public String getBusinessInfo() {
        return businessInfo;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public int getPutMode() {
        return putMode;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFloor() {
        return floor;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLogo() {
        return logo;
    }

    public void setProgramNum(String programNum) {
        this.programNum = programNum;
    }

    public String getNumber() {
        return number;
    }

    public void setPutMode(int putMode) {
        this.putMode = putMode;
    }

    public String getProgramNum() {
        return programNum;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
