package com.example.videoplayermanager.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.shuyu.gsyvideoplayer.model.GSYVideoModel;

public class VideoModel extends GSYVideoModel implements Parcelable {
    public VideoModel(String url, String title) {
        super(url, title);
    }

    protected VideoModel(Parcel in) {
        super(in.readString(),in.readString());

    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getUrl());
        parcel.writeString(getTitle());

    }
}
