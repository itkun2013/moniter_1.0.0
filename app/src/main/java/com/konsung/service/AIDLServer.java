package com.konsung.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.konsung.netty.EchoServer;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.util.DBManager;
import com.konsung.util.DPUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.UIUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * aidl server端
 * server端为app中AIDLServer类,client端为各个子参数的Activity
 * 在增加本类内容时需要注意本类的变量设计,具体参考注释内容
 * 本类包含AIDL数据传递,以及直接进行数据存储
 * @author ouyangfan
 * @version 0.0.1
 */
public class AIDLServer extends Service {
    // aidl Binder
    /*private AIDLServerBinder serverBinder;*/
    Message message = null;

    // 将趋势数据存储进List,用于数据缓存
    // 保存进List集合的原因是连续数据需要过滤
    // 如果是点测数据值则不需要加入集合进行过滤,而是直接使用即可
    private HashMap<Integer, Integer> status;
//    private HashMap<Integer, Integer> _trends;
//    private HashMap<Integer, Integer> _ecgConfig;
//    private HashMap<Integer, Integer> _spo2Config;
//    private HashMap<Integer, Integer> _nibpConfig;
//    private HashMap<Integer, Integer> _tempConfig;
//    private HashMap<Integer, Integer> _irtempConfig;

    //    MeasureDataBean _dataBean;
    private static SendMSGToFragment sendMsg;

    private MsgBinder msgBinder;

    private static Map<Integer, SendTrend> sendMap = new HashMap<>();
    private static Map<Integer, SendConfig> configMap = new HashMap<>();

    public void setSendMSGToFragment(SendMSGToFragment obj) {
        sendMsg = obj;
    }

    public interface SendMSGToFragment {
        public void sendWave(int param, byte[] bytes);

        public void sendTrend(int param, int value);

        public void sendConfig(int param, int value);
    }

    public interface SendTrend {
        /**
         * 发送对应参数及值
         * @param param 参数命令
         * @param value 参数值
         */
        void sendTrend(int param, int value);
    }

    public interface SendConfig {
        /**
         * 发送配置指令值
         * @param param 指令
         * @param value 值
         */
        void sendConfig(int param, int value);
    }

    public void addSendTrendListener(int type, SendTrend sendTrendListener) {
        sendMap.put(type, sendTrendListener);
    }

    public void addSendConfigListener(int type, SendConfig sendConfigListener) {
        configMap.put(type, sendConfigListener);
    }

    public void addSendTrendListeners(int[] types, SendTrend sendTrendListener) {
        for (int t : types) {
            addSendTrendListener(t, sendTrendListener);
        }
    }

    // 构造器
    public AIDLServer() {
    }

    /*
     * Handler 处理数据
     * 使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
     */
    private Handler mHandler = new ParamsHandler();

    private class ParamsHandler extends Handler {
//        private static final String TAG = "";

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            try {
            //Log.d("Test", "Msg:" + msg.toString());
            switch (msg.what) {
                case GlobalConstant.NET_TREND:
                    if (sendMsg != null) {
                        sendMsg.sendTrend(msg.arg1, msg.arg2);
                    }
                    SendTrend st = sendMap.get(msg.arg1);

                    if (st != null) {
//                            Log.e("CMAD","----->"+msg.arg1);
                        st.sendTrend(msg.arg1, msg.arg2);
                    }
//                        _trends.put(msg.arg1, msg.arg2);
                    break;
                case GlobalConstant.PARA_STATUS:
                    status.put(msg.arg1, msg.arg2);
                    Bundle paraStatusBundle = msg.getData();
                    byte[] paraBoardName = paraStatusBundle.getByteArray("paraBoardName");
                    byte[] paraBoardVersion = paraStatusBundle
                            .getByteArray("paraBoardVersion");
                    String boardName = null;
                    try {
                        boardName = new String(paraBoardName, "UTF-8");
                        String boardVersion = new String(paraBoardVersion, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    // 把KSM5的版本号作为多参模块版本号
//                    if (boardName.equals(UiUitls.getString(R.string.ksm5))) {
//                        SpUtils.saveToSp(getApplicationContext(), "app_config",
//                                "paraBoardName", boardName);
//                        SpUtils.saveToSp(getApplicationContext(),
//                                "app_config", "paraBoardVersion",
//                                boardVersion);
//                        //复位
//                        if (msg.arg1 == 0 && msg.arg2 == 1) {
//                            UiUitls.initSysConfig();
//                        }
//                    }
//
//                    // 与参数板连接成功后，初始化参数配置。0代表心电参数。
//                    if (0 == msg.arg1) {
//                        initParaConfig();
//                    }

                    break;
                case GlobalConstant.NET_WAVE://波形数据
                    Bundle data = msg.getData();
                    //Log.e("Test", "what--msg "+ msg.what+"--" + msg);
                    byte[] bytes;
                    if (data.containsKey(String.valueOf(KParamType.SPO2_WAVE))) {
//                            WriterLog.saveLog("收到spo2波形信号:view类型:"+KParamType.SPO2_WAVE);
                        bytes = data.getByteArray(String.valueOf(KParamType.SPO2_WAVE));

                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.SPO2_WAVE, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_II))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_II));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_II, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_I))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_I));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_I, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_III))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_III));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_III, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_AVR))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_AVR));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_AVR, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_AVL))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_AVL));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_AVL, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_AVF))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_AVF));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_AVF, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_V1))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_V1));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_V1, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_V2))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_V2));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_V2, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_V3))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_V3));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_V3, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_V4))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_V4));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_V4, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_V5))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_V5));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_V5, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.ECG_V6))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.ECG_V6));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.ECG_V6, bytes);
                        }
                    } else if (data.containsKey(String.valueOf(KParamType.RESP_WAVE))) {
                        bytes = data.getByteArray(String.valueOf(KParamType.RESP_WAVE));
                        if (sendMsg != null) {
                            sendMsg.sendWave(KParamType.RESP_WAVE, bytes);
                        }
                    }
                    //屏蔽CO2_WAVE
//                    else if (data.containsKey(String.valueOf(KParamType.CO2_WAVE))) {
//                        bytes = data.getByteArray(String.valueOf(KParamType.CO2_WAVE));
//                        if (sendMsg != null) {
//                            sendMsg.sendWave(KParamType.CO2_WAVE, bytes);
//                        }
//                    }
                    break;
                case GlobalConstant.NET_NIBP_CONFIG:
//                        _nibpConfig.put(msg.arg1, msg.arg2);
                    if (sendMsg != null) {
                        sendMsg.sendConfig(msg.arg1, msg.arg2);
                    }
                    if (configMap.get(msg.what) != null) {
                        configMap.get(msg.what).sendConfig(msg.arg1, msg.arg2);
                    }
                    break;
                case GlobalConstant.NET_SPO2_CONFIG:

                    if (msg.arg1 == 0x05) {
                        GlobalConstant.leffoff = msg.arg2;
                    }
                    if (sendMsg != null) {
                        sendMsg.sendConfig(msg.arg1, msg.arg2);
                    }
                    break;
                case GlobalConstant.NET_ECG_CONFIG:
//                        _ecgConfig.put(msg.arg1, msg.arg2);
                    if (sendMsg != null) {
                        sendMsg.sendConfig(msg.arg1, msg.arg2);
                    }
                    break;
                //体温
                case GlobalConstant.NET_TEMP_CONFIG:
                    //发送参数
                    if (sendMsg != null) {
                        sendMsg.sendConfig(msg.arg1, msg.arg2);
                    }
                    break;
                case GlobalConstant.NET_RESP_CONFIG:
                    if (configMap.get(msg.what) != null) {
                        configMap.get(msg.what).sendConfig(msg.arg1, msg.arg2);
                    }
                    break;
                case GlobalConstant.NET_PATIENT_CONFIG:
//                    Bundle bundle = msg.getData();
//                    byte[] idcards = bundle.getByteArray("idcard");
//                    byte[] name = bundle.getByteArray("name");
//                    char type = bundle.getChar("type");
//                    char sex = bundle.getChar("sex");
                    break;
                case GlobalConstant.NET_CONNECT_CENTRAL:
                    //中央站解析
                    int centralState = msg.arg1;
                    sendCentralBoardCast(centralState);

                    break;

                default:
                    Bundle errorData = msg.getData();
                       /* Log.d("error", msg.what + " = " + errorData.getByteArray(String.valueOf
                       (-1000)));*/
                    break;
            }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        /**
         * 发送连接状态广播
         * @param centralState 中央站状态
         */
        private void sendCentralBoardCast(int centralState) {
            Intent intent = new Intent(GlobalConstant.ACTION_CENTRAL_STATE);
            intent.putExtra(GlobalConstant.CENTRAL_STATE, centralState);
            UIUtils.getContext().sendBroadcast(intent);
        }
    }

    ;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        status = new HashMap<>();
//        _trends = new HashMap<>();
//        _ecgConfig = new HashMap<>();
//        _spo2Config = new HashMap<>();
//        _nibpConfig = new HashMap<>();
//        _tempConfig = new HashMap<>();
//        _dataBean = new MeasureDataBean();
        /*serverBinder = new AIDLServerBinder();*/
        msgBinder = new MsgBinder();

        // 采用动态的方式注册广播
        IntentFilter filter = new IntentFilter();
        // 需要监听的消息名称,用户切换消息
        filter.addAction("com.konsung.patientChange.receiver");
        registerReceiver(mPatientChangeReceiver, filter);

//        sendConfig();
        // 开启线程处理网络数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EchoServer.getEchoServerInstance(GlobalConstant.PORT, mHandler).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void sendConfig() {
        //发送病人信息
        DPUtils.sendPatientConfig(getApplicationContext());

        int v = DPUtils.getSelectValueBySortAttrId(getApplicationContext(), 110101);
        EchoServerEncoder.setEcgConfig((short) 0x02, v);//设置导联类型

        DPUtils.sendAllConfig(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return msgBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DBManager.releaseHelper(null, this);
        unregisterReceiver(mPatientChangeReceiver);
    }

    /*
     * 定义一个广播接收者
     * 用于接收病人改变
     */
    private BroadcastReceiver mPatientChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if ("com.konsung.patientChange.receiver".equals(action)) {
                String idcard = intent.getStringExtra("idcard");//身份证是唯一的
                /*_dataBean.setPatientName(name);*/
//                _patientList = new ArrayList<>();
//                _patientList = DBManager.getDBHelper(getApplicationContext(), AIDLServer.this)
// .getPatientDao().queryForEq("idcard", idcard);
                //_dataBean.setPatient();


                /*_dataBean = new MeasureDataBean();
                if (_patientList.size() > 0) {
                    List<MeasureDataBean> dataBeanList;
                    dataBeanList = DBManager.getDBHelper(getApplicationContext(), AIDLServer
                    .this).getMeasureDataDao().queryForEq("uid", _patientList.get(0).getId());
//                    Log.d("Test", "find the lasted measure uid" + _patientList.get(0).getId());
                    *//*Log.d("Test", "data size = " + dataBeanList.size());*//*
                    if (dataBeanList.size() > 0) {
                        //在这里找到最新的纪录
*//*                        int temp = -1;
                        for (MeasureDataBean bean : dataBeanList) {
                            if (bean.getId() > temp) {
                                temp = bean.getId();
                                _dataBean = bean;
                            }
                        }*//*
                        _dataBean = dataBeanList.get(dataBeanList.size() - 1);
                    }
                    _dataBean.setPatient(_patientList.get(0));
                }*/
//                _dataBean.setMeasureTime(new Date());
            }
        }
    };

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public AIDLServer getService() {
            return AIDLServer.this;
        }
    }

    /**
     * 保存趋势数据
     * @param param
     * @param value
     */
    public void saveTrend(int param, int value) {

//        _dataBean.setTrendValue(param, value);
    }

    /**
     * 保存波形数据
     * @param param
     * @param value
     * @throws RemoteException
     */
    public void savedWave(int param, String value) throws RemoteException {
//        _dataBean.set_ecgWave(param, value);
    }
}


