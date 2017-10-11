package com.konsung.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 时间工具类
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-05 12:28
 * 垃圾工具类，bug一堆，哥不用你的
 */
public class DateUtil {
    // 私有化构造器
    private DateUtil() {

    }

    /**
     * 获取应用列表数据，每添加一项体检，请在此处添加
     * @return List<String>
     */
    public static List<String> getAppList() {
        List<String> list = new ArrayList<String>();
        list.add("心电");
        list.add("血氧");
        list.add("血压");
        list.add("体温");
/*        list.add("耳温");*/
/*        list.add("血糖");
        list.add("尿常规");*/
        return list;
    }

    /**
     * 获取当前年月日时分秒毫秒
     * @return 20170717140412505
     */
    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return format.format(date);
    }

    /**
     * 将字符串转化为日期对象
     * 返回时间对象;<br/>
     * format为时间格式如("yyyy-MM-dd hh:mm:ss")<br/>
     * 返回null表示出错了
     */
    public static Date getDate(String time, String format) {
        Date date = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    /**
     * 将long转化为日期对象
     * 返回时间格式字符串;<br/>
     * format为时间格式如("yyyy-MM-dd hh:mm:ss")<br/>
     * 返回""表示出错了
     */
    public static String getDateToStr(long time, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(time);
        return sdf.format(date);
    }

    public static String getDateToStr(long time) {
        String times = null;
        if (time < 60) {
            times = pundun(time);
        } else if (time < 3600) {
            times = pundun(time / 60) + ":" + pundun(time % 60);
        } else if (time >= 3600) {
            times = pundun(time / 3600) + ":" + pundun(time % 3600 / 60) + ":" + pundun(time %
                    3600 % 60);
        }
        if (times.length() == 2) {
            times = "00:00:" + times;
        } else if (times.length() == 5) {
            times = "00:" + times;
        }

        return times;
    }

    public static String pundun(long time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return time + "";
        }
    }

    //
    //    if(time<60){
    //        if(time<10){
    //            return "00:00:0"+time;
    //        }
    //        return "00:00:"+time;
    //    }else if(time>=60 && time<3600){
    //        if(time/60<10){
    //            return "00:0"+time/60 +":"+time%60;
    //        }
    //        return "00:"+time/60 +":"+time%60 ;
    //    }else if(time>=3600){
    //        //			return time/3600+":"+getDateToStr(time%3600);
    //        if(time%3600 <60){
    //            if(time%3600 <10){
    //                return "0"+time/3600+":00:"+getDateToStr(time%3600);
    //            }
    //            return time/3600+":00:"+getDateToStr(time%3600);
    //        }else {
    //            if(time%3600 <10){
    //                return "0"+time/3600+":"+getDateToStr(time%3600);
    //            }
    //            return time/3600+":"+getDateToStr(time%3600);
    //        }
    //    }
    //    return null;

    /**
     * 根据当地格式格式化日期
     * @param date 日期
     * @param locale 当地格式
     * @return 字符串格式时间 返回格式如(2015年2月5日)
     */
    public static String getDateLocalFormat(Date date, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
        return dateFormat.format(date);
    }

    /**
     * 根据当地格式格式化日期
     * 注意与上面getDate的参数及返回值的区别
     * @param date 日期
     * @return 字符串格式时间
     * 返回格式如(yyyy-MM-dd hh:mm:ss) 表示12小时制
     * (yyyy-MM-dd HH:mm:ss) 表示24小时制
     */
    public static String getTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

     /*
     * 保留两位小数
     */

    public static String floatToStr(float f) {
        DecimalFormat format = new DecimalFormat("0.00");
        return String.valueOf(format.format(f));
    }

    /*
     * 保留一位小数
     */
    public static String floatToStr1(float f) {
        DecimalFormat format = new DecimalFormat("0.0");
        return String.valueOf(format.format(f));
    }

    /**
     * 获取log奔溃时间
     * @return 时间
     */
    public static String getDateLog() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String date = formatter.format(new Date(System.currentTimeMillis()));
        return date; // 2017-4-03 9:41:31
    }
}
