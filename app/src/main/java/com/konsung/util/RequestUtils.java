package com.konsung.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by liangkun on 2017/9/14 0014.
 * 网络工具类
 */

public class RequestUtils {
    /**
     * 判断wifi是否链接可以使用
     * @param mContext 上下文
     * @return 是否wifi连接
     */
    public static boolean isWifiAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager
                    .TYPE_WIFI);

            return wifi.isAvailable();
        }
        return false;
    }

}
