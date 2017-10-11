package com.konsung.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-04 14:09
 */
public class SpUtils {
    /**
     * 文件名
     */
    private static final String CONFIG = "konsung";

    /**
     * 私有构造
     */
    private SpUtils() {

    }

    /**
     * 保存布尔值
     *
     * @param mContext
     *            mContext
     * @param key
     *            key
     * @param value
     *            value
     */
    public static synchronized void save2Sp(Context mContext, String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 保存字符串
     *
     * @param mContext
     *            mContext
     * @param key
     *            key
     * @param value
     *            value
     */
    public static synchronized void save2Sp(Context mContext, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    /**
     * 保存数字
     *
     * @param mContext
     *            mContext
     * @param key
     *            key
     * @param value
     *            value
     */
    public static synchronized void save2Sp(Context mContext, String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }



    /**
     * 获取指定key的int
     *
     * @param mContext
     *            mContext
     * @param key
     *            key
     * @param defValue
     *            defValue
     * @return 返回值
     */
    public static synchronized int get4SpInt(Context mContext, String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }
    /**
     * 获取指定key的boolean
     *
     * @param mContext
     *            mContext
     * @param key
     *            key
     * @param defValue
     *            defValue
     * @return 返回值
     */
    public static synchronized boolean get4Sp(Context mContext, String key, boolean defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 获取指定key的String
     *
     * @param mContext
     *            mContext
     * @param key
     *            key
     * @param defValue
     *            defValue
     * @return 返回值
     */
    public static synchronized String get4Sp(Context mContext, String key, String defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * 删除指定的Key
     * @param mContext
     * @param key
     */

    public static  synchronized void remove4Sp(Context mContext,String key){
        SharedPreferences sp=mContext.getSharedPreferences(CONFIG,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.remove(key);
    }

}
