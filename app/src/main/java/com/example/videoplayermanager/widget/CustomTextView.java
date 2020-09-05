package com.example.videoplayermanager.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView
{
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(@Nullable Typeface tf) {
        tf=Typeface.createFromAsset(getContext().getAssets(),"fonts/SourceHanSansCN-Medium.otf");
        super.setTypeface(tf);
    }


}
