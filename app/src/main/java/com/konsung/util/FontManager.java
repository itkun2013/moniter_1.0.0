package com.konsung.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Cmad on 2015/10/16.
 * 修改字体
 */
public class FontManager {

    public static void changeFonts(Context ctx,ViewGroup root,Typeface tf){

        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            changeFonts(ctx,v, tf);
        }
    }

    public static void changeFonts(Context ctx, View v ,Typeface tf) {
        if (v instanceof TextView) {
            ((TextView) v).setTypeface(tf);
        } else if (v instanceof ViewGroup) {
            changeFonts(ctx,(ViewGroup) v, tf);
        }
    }

    public static void changeFonts(Context ctx, View v ) {
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(),
                "Numeral.ttf");
        changeFonts(ctx,v,tf);
    }


}
