package com.konsung.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.konsung.R;
import com.konsung.defineview.WaveForm_Ecg;
import com.konsung.defineview.WaveForm_co2;
import com.konsung.defineview.WaveForm_resp;
import com.konsung.defineview.WaveForm_spo2;
import com.konsung.service.AIDLServer;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;

/**
 * Created by chengminghui on 15/10/14.
 */
public class TestActivity extends Activity {
    private WaveForm_Ecg ecg_i;
    private WaveForm_Ecg ecg_ii;
    private WaveForm_Ecg ecg_iii;
    private WaveForm_Ecg ecg_avf;
    private WaveForm_Ecg ecg_avl;
    private WaveForm_Ecg ecg_avr;
    private WaveForm_Ecg ecg_v;
    private WaveForm_co2 co2;
    private WaveForm_resp resp;
    private WaveForm_spo2 spo2;
    public AIDLServer aidlServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ecg_i = (WaveForm_Ecg) findViewById(R.id.wave_i);
        ecg_ii = (WaveForm_Ecg) findViewById(R.id.wave_ii);
        ecg_iii = (WaveForm_Ecg) findViewById(R.id.wave_iii);
        ecg_avr = (WaveForm_Ecg) findViewById(R.id.wave_AVR);
        ecg_avl = (WaveForm_Ecg) findViewById(R.id.wave_AVL);
        ecg_avf = (WaveForm_Ecg) findViewById(R.id.wave_AVF);
        ecg_v = (WaveForm_Ecg) findViewById(R.id.wave_V);
        co2 = (WaveForm_co2) findViewById(R.id.wave_co2);
        resp = (WaveForm_resp) findViewById(R.id.wave_resp);
        spo2 = (WaveForm_spo2) findViewById(R.id.wave_spo2);

        ecg_i.reset();
        ecg_ii.reset();
        ecg_iii.reset();
        ecg_avr.reset();
        ecg_avl.reset();
        ecg_avf.reset();
        ecg_v.reset();
        co2.reset();
        resp.reset();
        spo2.reset();
        initAidlService();
    }



    public void initAidlService() {
        Intent _intent = new Intent(GlobalConstant.ACTION_SERVICE);
        startService(_intent);
        bindService(_intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    /*
       * serviceConnection ��aidl�ӿڽ���
       */
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*Log.i("Test", "AIDLClient.onServiceConnected()...");*/
            // ��÷������
            /*_aidlInterface = IKonsungAidlInterface.Stub.asInterface(service);*/
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
                @Override
                public void sendWave(int param, byte[] bytes) {

                    switch (param) {
                        case KParamType.ECG_I:
                            ecg_i.setData(bytes);
                            break;
                        case KParamType.ECG_II:
                            ecg_ii.setData(bytes);
                            break;
                        case KParamType.ECG_III:
                            ecg_iii.setData(bytes);
                            break;
                        case KParamType.ECG_AVR:
                            ecg_avr.setData(bytes);
                            break;
                        case KParamType.ECG_AVL:
                            ecg_avl.setData(bytes);
                            break;
                        case KParamType.ECG_AVF:
                            ecg_avf.setData(bytes);
                            break;
                        case KParamType.ECG_V1:
                            ecg_v.setData(bytes);
                            break;
                        case KParamType.SPO2_WAVE:
                            spo2.setData(bytes);
                            break;
                        case KParamType.RESP_WAVE:
                            resp.setData(bytes);
                            break;
//                        屏蔽CO2_WAVE
//                        case KParamType.CO2_WAVE:
//                            co2.setData(bytes);
//                            break;
                        default:
                            break;

                    }
                }

                @Override
                public void sendTrend(int param, int value) {

                }

                @Override
                public void sendConfig(int param, int value) {
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {



        }
    };
}
