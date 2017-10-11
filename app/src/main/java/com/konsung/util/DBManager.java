package com.konsung.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.konsung.sqlite.ConfigDBHelper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据库管理类
 * Activity和Service都要用到数据库
 * 为防止重复创建数据库对象,抽象到此类进行管理
 * 因为Service远程调用是在不同的进程中
 * 所以Activity和Service各持有一个数据库对象
 * @author ouyangfan
 * @version 0.0.1
 */
public class DBManager {
    private static final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "DB_KonSung.db3"; //保存的数据库文件名
    //    public static String packageName; //包名
    public static String dbPath;  //在手机里存放数据库的位置

    // 数据库帮助类
    private static ConfigDBHelper configDBHelper = null;
    // 使用范围
    private static boolean isUseInActivity = false;
    private static boolean isUseInService = false;
    private static final String DATA = "/data"; //文件名
    private static final String DB_BASE = "/databases/"; //文件名

    /**
     * 私有构造
     */
    private DBManager() {

    }

    /**
     * 在Activity中获取DBHelper对象
     * @param context 上下文对象
     * @return 数据库帮助类
     */
    public static ConfigDBHelper getConfigDBHelper(Context context) {
        if (configDBHelper == null) {
            configDBHelper = OpenHelperManager.getHelper(context, ConfigDBHelper.class);
        }
        isUseInActivity = true;
        return configDBHelper;
    }

    /**
     * 复制数据库到sd卡
     * @param context 上下文
     */
    public static void copyDatabase(Context context) {
        dbPath = DATA + FileUtils.getSdPath(context) + "/" + context.getPackageName() + DB_BASE;
        try {
            //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
            if (!FileUtils.isFileExist(dbPath)) {
                FileUtils.mkdir(dbPath);
                //导入数据库
                InputStream inputStream = context.getAssets().open(DB_NAME);
                FileOutputStream fileOutputStream = new FileOutputStream(dbPath + DB_NAME);
                //读写
                FileUtils.copy(inputStream, fileOutputStream);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * 释放数据库
    * 只有当activity和service中都不使用数据库的时候才释放
    * @param activity 不在activity销毁时请置@null
    * @param service 不在service销毁时请置@null
    */
    public static void releaseHelper(Activity activity, Service service) {
        if (null != activity) {
            isUseInActivity = false;
        }
        if (null != service) {
            isUseInService = false;
        }
        // 释放DBHelper
        if ((configDBHelper != null) && (!isUseInActivity) &&
                (!isUseInService)) {
            OpenHelperManager.releaseHelper();
            configDBHelper = null;
        }
    }
}

