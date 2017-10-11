package com.konsung.util;

import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * 单位转换工具类
 *
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-04 10:19
 */
public class UnitConvertUtil {

    /*
     * 私有构造
     */
    private UnitConvertUtil() {

    }

    // Dp转换Px的方法
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }
    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
/*
    */
/**
 * byte[] 转16进制工具
 * @param src
 * @return
 *//*

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
*/

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            if(i == (src.length) -1){}else{

                stringBuilder.append(hv).append(",");
            }
        }
        return stringBuilder.toString();
    }


    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 转javabean为json
     *
     * @param bean
     * @return
     */
    public static String objectToJSON(Object bean) {
        //返回的json
        String json = "{";
        //几个属性
        int count;
        //当前位置
        int position = 0;
        Class cl = bean.getClass();
        Field[] fields = cl.getDeclaredFields();
        count = fields.length;
        try {
            for (Field field : fields) {
                position++;//从第一个开始
                field.setAccessible(true);
                json += "\"" + field.getName() + "\":\"" + field.get(bean) + "\"";
                if (position != count)
                    json += ",";
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        json += "}";
        return json;

    }

    /**
     * Convert Map to JSON
     *
     * @param map
     * @return
     */

    public static String mapToJSON(Map<String, Object> map) {
        String json = "{";
        int position = 0;
        for (String str : map.keySet()) {
            if (str.equals("recs")) {
                json += "\"" + str + "\":" + map.get(str);
            } else
                json += "\"" + str + "\":" + "\"" + map.get(str) + "\"";
            position++;
            if (position != map.keySet().size())
                json += ",";
        }
        json += "}";

        return json;
    }

    public static String ecgwaveToString(String sixteenbytes) {
        //转化的float[]字符串
        String s = "";
        float _factor = 0.044f;

        //float[]的size
        int size = 0;
        byte[] bytes = hexStringToBytes(sixteenbytes);
        if (bytes != null)
            size = bytes.length / 4;
        else
            return "";
        ArrayList<Byte> data = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            data.add(bytes[i]);
        }
        float[] _point = new float[size];
        for (int i = 0; ; ) {
            if (data.size() > 16) {
                _point[i++] = (data.get(0) & 0xFF) + ((data.get(1) & 0x0F) << 8);
                _point[i++] = (data.get(8) & 0xFF) + ((data.get(9) & 0x0F) << 8);
                for (int j = 0; j < 8; j++) {
                    data.remove(0);
                }
            } else {
                break;
            }
        }
        s = Arrays.toString(_point);
        return s;
    }

    /**
     *
     * <p>华氏度  = 32 + 摄氏度 × 1.8。</p>
     * @param degree 需要转换的温度
     * @param scale 保留的小数位
     * @return
     */
    public static double centigrade2Fahrenheit(double degree,int scale) {
        double d = 32 + degree * 1.8;
        return new BigDecimal(d).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     *
     * <p>摄氏度  = (华氏度 - 32) ÷ 1.8。</p>
     * @param degree 需要转换的温度
     * @param scale 保留的小数位
     * @return
     */
    public static double fahrenheit2Centigrade(double degree, int scale) {
        double d = (degree - 32) / 1.8;
        return new BigDecimal(d).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
