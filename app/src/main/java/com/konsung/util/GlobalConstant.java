package com.konsung.util;

/**
 * 全局常量类
 * 定义程序用到的全局常量
 * @author ouyangfan
 * @version 0.0.1
 */
public class GlobalConstant {
    //应用信息
    public static final String VER = "1.0.0";
    public static final int NUM = 1; //修正次数

    //探头脱落状态
    public static int leffoff = -1000;

    // 统一TAG，方便日志打印
    public static final String LOG_TAG = "konsung";

    // 网络数据的放大倍数，趋势数据统一为100
    public static final int TREND_FACTOR = 100;
    //体温趋势值
    public static final float TEMP_FACTOR = 100f;
    // 尿常规网络数据的放大倍数，趋势数据统一为100
    public static final int URITREND_FACTOR = 100;
    // handler message中趋势数据标识
    public static final int PARAM_ACTIVE_MSG = 2;
    public static final int PUT_TREND = 2;

    // 网络端口号
    public static final int PORT = 6613;
    // 无效趋势值
    public static final int INVALID_TREND_DATA = -1000;
    // 网络数据包最大长度
    public static final int MAX_PACKET_LEN = 1024;
    // 网络命令字
    public static final byte PARA_STATUS = 0x12; //参数版
    public static final byte NET_TREND = 0x51;
    public static final byte NET_WAVE = 0x52;
    public static final byte NET_ECG_CONFIG = 0x21;
    public static final byte NET_RESP_CONFIG = 0x22;
    public static final byte NET_SPO2_CONFIG = 0x24;
    public static final byte NET_NIBP_CONFIG = 0x25;
    /**
     * 体温网络命令字
     */
    public static final byte NET_TEMP_CONFIG = 0x23;


    public static final byte NET_CONNECT_CENTRAL = 0x72; //中央站连接命令
    public static final byte NET_PATIENT_CONFIG = 0x11;
    public static final byte NET_SERVER_CONFIG = 0x71; //服务端ip 或者端口命令字
    public static final int STRING_MAX_LEN = 60;
    /*ip和端口*/
    public static final int SERVER_IP_CONFIG = 190101; //ip
    public static final int SERVER_PORT_CONFIG = 190102; //端口
    public static final int SERVER_DEVICE_CONFIG = 200001; //设备号
    public static final String SERVER_IP_DEFULT = "192.168.0.202"; //默认ip
    public static final String SERVER_PORT_DEFULT = "5510"; //默认端口
    public static final String SERVER_DEVICE_DEFULT = "M0000"; //默认设备

    /*病人*/
    public static final int PAIENT_SORTID_CONFIG = 10; //病人的sortid
    //默认为25mm/s
    public static float ECG_MM = 1.0f;
    //默认为x1
    public static float ECG_XX = 1.0f;

    /**
     * Ecg GlobalConstant
     */
    // 模拟一次ECG测量时间10000(10s)
    public static final int MEASURE_TIME = 10000;
    // 无效趋势值
    public static final int INVALID_DATA = -1000;
    //ECG导联数，默认为三，在设置中可以设置
    public static final int ECG_NUM = 5;

    public static final String CRASHLOGPATH = "/data/data/com.konsung.dev/log/";

    public static final int CONFIG_VALUE_TYPE_INPUT = 1; //输入类型
    public static final int CONFIG_VALUE_TYPE_CHOOICE = 2; //点击选择类型
    public static final int CONFIG_VALUE_TYPE_BUTTON = 3; //按钮类型
    public static final int CONFIG_VALUE_TYPE_VIEW = 4; //只显示，不可点击
    //病人信息ID
    public static final int TYPE_PATIENT = 10;

    public static final String ACTION_SERVICE = "com.konsung.aidlService";
    public static final String ACTION_RESTART = "com.konsung.AppDevice.start";
    public static final String ACTION_SPO2_STOP = "com.kongsung.spo2.stopware"; //血氧设备插拔的广播
    public static final String ACTION_SWITCH_pATIENT = "com.kongsung.notify.switchpatient"; //切换病人
    /**
     * 更新数据的广播
     */
    public static final String ACTION_UPDATE_DATA = "com.konsung.UPDATE_DATA";
    public static final String ACTION_SHOW_TEMP_NIBP = "com.konsung.show.temp_nibp"; //展示血压界面
    //隐藏血压界面
    public static final String ACTION_DISMISS_TEMP_NIBP = "com.konsung.dismiss.temp_nibp";
    public static final String ACTION_CENTRAL_STATE = "com.konsung.central.change"; //中央站连接广播
    public static final String CENTRAL_STATE = "state"; //中央站连接广播状态

    public static final String TYPE_SHOW = "typeShow";
    public static final String SORT_ID = "sortId";
    public static final String ATTR_ID = "attrID";

    public static final int ACTION_CREAT_USER = 1000; //创建新用户
    public static final int ECG_WAVE_GAIN = 110106; //心电波形增益
    public static final int ECG_WAVE_SMOOTH = 110107; //滤波方式
    public static final int ECG_WAVE_SPEED = 110108; //心电波形速度

    public static final int ECG_DL = 110101; //心电导联类型
    public static final int ECG_ST = 110104; //心电ST分析

    public static final int ECG_ONOFF = 110201; //心率开关
    public static final int ECG_UP = 110202; //心率上限
    public static final int ECG_DOWN = 110203; //心率下限

    public static final int PVC_ONOFF = 110204; //PVC开关
    public static final int PVC_UP = 110205; //PVC上限
    public static final int PVC_DOWN = 110206; //PVC下限
    /**
     * 导联类型 DictId
     */
    public static final int LEAD_MOLD = 105; //导联类型
    /**
     * ST段报警设置
     */
    public static final int ST_ONOFF = 110207; //ST开关
    public static final int ST_UP = 110208; //ST上限
    public static final int ST_DOWN = 110209; //ST下限

    public static final int SPO_UP = 120202; //SPO上限
    public static final int SPO_DOWN = 120203; //SPO上限

    public static final int SPO_BPM_UP = 120205; //SPO脉率上限
    public static final int SPO_BPM_DOWN = 120206; //SPO脉率上限

    public static final int BREATHE_UP = 130202; //呼吸率上限
    public static final int BREATHE_DOWN = 130203; //呼吸率下限

//    public static final int PACE_MAKING = 110102; //起搏开关
    public static final int LEAD_COUNT = 110109; //计算导联

    /**
     * 中央站的连接状态
     */
    public static final int CENTRAL_CONNECT_SUCCESS = 1; //成功
    public static final int CENTRAL_CONNECT_FAIL = 0; //失败
}
