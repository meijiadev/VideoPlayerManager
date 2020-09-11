package com.example.videoplayermanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class SimpleImageView extends androidx.appcompat.widget.AppCompatImageView {
    public SimpleImageView(Context context) {
        super(context);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public SimpleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(ScaleType.CENTER_CROP);
    }
}
