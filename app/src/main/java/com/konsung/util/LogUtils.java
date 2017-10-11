package com.konsung.util;

import android.util.Log;

/**
 * Created by liangkun on 2017/3/9 0009.
 * 日志工具，上线可屏蔽日志信息
 */
public class LogUtils {
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    //屏蔽log的时候，可以将NONE 的值赋给LEVEL
    public static final int NONE = 5;

    //通过控制LEVEL的值，实现屏蔽Log的效果,默认使用最低级别
    public static final int LEVEL = INFO;

    /**
     *
     * @param tag 当前类的标记
     * @param msg log信息
     */
    public static void v(String tag, String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg);
        }
    }
    /**
     *
     * @param tag 当前类的标记
     * @param msg log信息
     */
    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg);
        }
    }
    /**
     *
     * @param tag 当前类的标记
     * @param msg log信息
     */
    public static void i(String tag, String msg) {
        if (LEVEL <= INFO) {
            Log.i(tag, msg);
        }
    }
    /**
     *
     * @param tag 当前类的标记
     * @param msg log信息
     */
    public static void w(String tag, String msg) {
        if (LEVEL <= WARN) {
            Log.w(tag, msg);
        }
    }
    /**
     *
     * @param tag 当前类的标记
     * @param msg log信息
     */
    public static void e(String tag, String msg) {
        if (LEVEL <= ERROR) {
            Log.e(tag, msg);
        }
    }


}

