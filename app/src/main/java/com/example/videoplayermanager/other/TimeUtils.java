package com.example.videoplayermanager.other;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    /**
     * 获取当前时分
     * @return
     */
    public static String getCurrentTime1(){
        SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String ee = dff.format(new Date());
        Logger.e("-----当前时间："+ee);
        return ee;
    }

    /**
     * 获取当前年月日
     * @return
     */
    public static String getCurrentTime2(){
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String ee = dff.format(new Date());
        return ee;
    }

    /**
     * 获取当前年月日时分秒
     * @return
     */
    public static String getCurrentTime3(){
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String ee = dff.format(new Date());

        return ee;
    }

    /**
     * string型时间转long
     * @param strTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static long stringToLong(String strTime,String formatType) throws ParseException {
        Date date=stringToDate(strTime,formatType);
        if (date==null){
            return 0;
        }else {
            return date.getTime();
        }
    }

    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

}
