package com.konsung.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by hyf on 2016/3/25.
 */
public class UIUtils {
    private static Context mBaseContext;

    public static void init(Application application){
        mBaseContext = application;
    }

    public static Context getContext() {
        return mBaseContext;
    }
}
