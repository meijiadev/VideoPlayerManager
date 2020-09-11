package com.example.videoplayermanager.banner;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.videoplayermanager.widget.SimpleImageView;
import com.youth.banner.loader.ImageLoader;

public class LocalImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context.getApplicationContext())
                .load(path)
                .into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        SimpleImageView simpleImageView=new SimpleImageView(context);
        return simpleImageView;
    }
}
