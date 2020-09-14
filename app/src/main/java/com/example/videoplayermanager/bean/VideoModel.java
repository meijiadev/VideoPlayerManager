package com.example.videoplayermanager.bean;

import com.shuyu.gsyvideoplayer.model.GSYVideoModel;

public class VideoModel extends GSYVideoModel {
    private String programNum;        //节目单号
    private String floorName;         //楼层
    private String floorNumber;      //门牌号
    private String businessLogo;    //商家logo
    private long videoTimes;
    public VideoModel(String url, String title) {
        super(url, title);

    }



    @Override
    public String getUrl() {
        return super.getUrl();
    }

    @Override
    public void setUrl(String url) {
        super.setUrl(url);
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    public void setProgramNum(String programNum) {
        this.programNum = programNum;
    }

    public String getProgramNum() {
        return programNum;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
    }

    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setVideoTimes(long videoTimes) {
        this.videoTimes = videoTimes;
    }

    public long getVideoTimes() {
        return videoTimes;
    }
}
