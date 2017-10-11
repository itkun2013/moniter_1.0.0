package com.konsung.util;

import android.widget.TextView;

import java.util.List;

/**
 * Created by Cmad on 2015/12/17.
 * 数据处理
 */
public class DataUtils {

    private static final int OFFSET = 150;
    public static final int TIME    =40;//66
    private static final String TAG    = "DataUtils";

    /**
     * 删除列表中多余的数据，防止数据过多缓存的问题
     * @param data
     */
    public static void removeMoreData(List data){
        int size = data.size();
//        int removeSize = (size-OFFSET)/2*2;

        int removeSize = (size-OFFSET);
        for(int i=0;i<removeSize;i++ ){
            data.remove(0);
        }
//        for(int i=size-1;i>OFFSET; ){
//            data.remove(i);
//            i-=3;
//        }
    }


    /**
     * 是否有效，显示值为-1000时显示无效值
     * @param tv 显示的textview
     * @param value 显示值
     * @return 是否有效
     */
    public static boolean isValid(TextView tv, int value){
        if(value == -1000){
            tv.setText("-?-");
            return false;
        }
        return true;
    }
    /**
     * 体温是否有效，显示值为-1000时显示无效值
     * @param tv 显示的textview
     * @param value 显示值
     * @return 是否有效
     */
    public static boolean isTempValid(TextView tv, float value){
        value = value * GlobalConstant.TEMP_FACTOR;
        if(value == -1000){
            tv.setText("-?-");
            return false;
        }
        return true;
    }




}
