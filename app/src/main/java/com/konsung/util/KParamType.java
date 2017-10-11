package com.konsung.util;

/**
 * 参数类型
 * 这些参数与AppDevice保持一致
 * 参数数值由协议指定
 * 目前只写了程序中使用到的部分趋势子参数的值
 * 后期可以在本类中增加相应的子参数
 * 这些值必须与协议保持一致
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-04-09 11:08
 */

public class KParamType {

	public static final int ECG = 0;
	public static final int ECG_I = 1;
	public static final int ECG_II =2;
	public static final int ECG_III =3;
    public static final int ECG_AVR=4;
    public static final int ECG_AVL=5;
    public static final int ECG_AVF=6;
    public static final int ECG_V1=7;
    public static final int ECG_V2=8;
    public static final int ECG_V3=9;
    public static final int ECG_V4=10;
    public static final int ECG_V5=11;
    public static final int ECG_V6=12;

    public static final int ECG_ST_I = 17;
    public static final int ECG_ST_II =18;
    public static final int ECG_ST_III =19;
    public static final int ECG_ST_AVR=20;
    public static final int ECG_ST_AVL=21;
    public static final int ECG_ST_AVF=22;
    public static final int ECG_ST_V1=23;
    public static final int ECG_ST_V2=24;
    public static final int ECG_ST_V3=25;
    public static final int ECG_ST_V4=26;
    public static final int ECG_ST_V5=27;
    public static final int ECG_ST_V6=28;

    // ECG趋势子参数(心电图)
    public static final int ECG_HR = 14;
    public static final int ECG_PVC = 15;
    public static final int ECG_ST = 18;

    //呼吸波形参数
    public static final int RESP_WAVE = 101;


    // resp趋势子参数(呼吸频率)
    public static final int RESP_RR = 102;

    // spo2趋势子参数(血氧)
	public static final int SPO2_WAVE  = 201;
    public static final int SPO2_TREND = 202;
    public static final int SPO2_PR = 203;

    // temp趋势子参数（体温）
    public static final int TEMP_T1 = 301;
    public static final int TEMP_T2 = 302;
    public static final int TEMP_TD = 303;

    // irTemp趋势子参数(耳温)
    public static final int IRTEMP_TREND = 401;//红外耳温

    // nibp趋势子参数（无损血压测量）
    public static final int NIBP_SYS = 501;
    public static final int NIBP_DIA = 502;
    public static final int NIBP_MAP = 503;
    public static final int NIBP_PR = 504;

    //co2波形参数
    public static final int CO2_WAVE = 601;

    public static final int CO2_ETCO2 = 602;
    public static final int CO2_FICO2 = 603;
    public static final int CO2_AWRR = 604;

    // bloodGlu趋势子参数（血糖）
    public static final int BLOODGLU_BEFORE_MEAL = 901;
    public static final int BLOODGLU_AFTER_MEAL = 902;

    // urineRt趋势子参数(尿常规)
    public static final int URINERT_LEU = 1201;
    public static final int URINERT_NIT = 1202;
    public static final int URINERT_UBG = 1203;
    public static final int URINERT_PRO = 1204;
    public static final int URINERT_PH = 1205;
    public static final int URINERT_BLD = 1206;
    public static final int URINERT_SG = 1207;
    public static final int URINERT_BIL = 1208;
    public static final int URINERT_KET = 1209;
    public static final int URINERT_GLU = 1210;
    public static final int URINERT_VC = 1211;
}
