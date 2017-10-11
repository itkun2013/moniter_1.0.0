package com.konsung.util;

import android.content.Context;
import android.widget.Toast;

/**
 * package: ${package_declaration}.
 * 时间 : 2017/2/21.
 * 作者 ：huyufeng
 * 作用 :
 */

public class ToastAssit {
    public static void showT(Context cxt, String msg){
        Toast.makeText(cxt, msg, Toast.LENGTH_SHORT).show();
    }
}
