package com.konsung.activity;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.konsung.defineview.NibpDataView;
import com.konsung.util.CrashHandler;
import com.konsung.util.DBManager;
import com.konsung.util.UIUtils;
import com.konsung.util.WriterLog;

/**
 * Created by JustRush on 2015/7/15.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    public static MyApplication application;
    public static boolean appDeviceIsConnect = false;
//    public static boolean isAutoMeasureNibp = false;
//    public static boolean isStopMeasureNibp = false;
//    public static boolean isNIBPMeasuring = false; //正在测量
//    public static int timeNIBPStart = 30; //默认30

    @Override
    public void onCreate() {
        super.onCreate();
        DBManager.copyDatabase(this);
        WriterLog.init(this);
        //是否抓取奔溃日志，初始化奔溃日志工具
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            boolean isCatch = appInfo.metaData.getBoolean("catch");
            if (isCatch) {
                //正式测试,储存本地
                CrashHandler.getInstance().init(getApplicationContext());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        startAppDevice();
        application = this;

        UIUtils.init(this);
        resetNibp();
    }

    /**
     * 设置血压
     */
    private void resetNibp() {
        NibpDataView.NIBP_MEASURE_START = false;
        NibpDataView.NIBP_MEASURE_ING = false;
    }

    /**
     * 重启app
     */
    public void restartApp() {
        startAppDevice();
        UIUtils.init(this);
        Intent intent = new Intent(application, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    /**
     * 启动appdevice
     */
    public void startAppDevice() {
        PackageManager packageManager = getPackageManager();
        Intent intent;
        try {
            intent = packageManager.getLaunchIntentForPackage("org.qtproject.qt5.android.bindings");
            if (intent == null) {
                return;
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
