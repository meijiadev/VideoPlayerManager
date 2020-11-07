package com.example.videoplayermanager.other;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * desc：用于保存设置的参数
 */
public class SpUtil {
    private static final String SP_NAME = "ADRobot";
    public static final String CURRENT_VIDEO_FINISH="videoFinish";    //当前视频播放完得时间
    private static SpUtil spUtil;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private SpUtil(Context context) {
        context=context.getApplicationContext();
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SpUtil getInstance(Context context) {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil(context);
                }
            }
        }
        return spUtil;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
        Logger.e("设置的语言："+value);
    }

    public void putBoolean(String key, boolean value){
        editor.putBoolean(key,value);
        editor.commit();
    }

    public void putInt(String key, int value){
        editor.putInt(key,value);
        editor.commit();
    }

    public void putLong(String key,long value){
        editor.putLong(key,value);
        editor.commit();
    }


    public String getString(String key) {
        return sharedPreferences.getString(key,"");
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,true);
    }

    public int getInt(String key){
        return sharedPreferences.getInt(key,88);
    }

    public long getLong(String key){
        return sharedPreferences.getLong(key,System.currentTimeMillis());
    }
}
