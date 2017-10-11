package com.konsung.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.Params;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.service.AIDLServer;
import com.konsung.util.DBManager;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.RequestUtils;
import com.konsung.util.ToastUtils;
import com.konsung.util.UnitConvertUtil;
import com.konusng.adapter.WaveRecyclerAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.sql.SQLException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Cmad on 2015/8/25.
 * 主界面
 */
public class MainActivity extends Activity implements Toolbar.OnMenuItemClickListener {
    private static final int NOTIFICATION_FLAG = 1; //通知应用的唯一标志
    private static final String WIFI_LOG = "网路不可用，上不了网";
    private RecyclerView mWaveRecyclerView;
    private WaveRecyclerAdapter mWaveAdapter;
    private static final String URL = "https://www.baidu.com";
    private static final String RESPONE_CODE = "200";
    public AIDLServer aidlServer;
    private int hrInvalid = GlobalConstant.INVALID_DATA; //趋势无效值？

    private boolean isChecking = true;
    private NotificationManager manager;
    private Notification notification;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //home键标志
    public NetWifiConnectReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.activity_main);
        DBManager.copyDatabase(MainActivity.this);

        initView();
    }

    /**
     * 显示连接socket是否成功
     * @param isConnect 是否连接
     */
    private void showConnectState(boolean isConnect) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        //常驻状态栏的图标
        if (isConnect) {
            builder.setSmallIcon(R.drawable.connect_success);
            builder.setContentTitle(getString(R.string.socket_connect_success)); //设置标题
        } else {
            builder.setSmallIcon(R.drawable.connect_fail);
            builder.setContentTitle(getString(R.string.socket_connect_fail));
        }
        //设置通知时间
        builder.setWhen(System.currentTimeMillis());
        //获得Notification对象
        notification = builder.build();
        // 将此通知放到通知栏的"Ongoing"常驻状态
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        // 无法清除
        notification.flags = Notification.FLAG_NO_CLEAR;
        manager.notify(NOTIFICATION_FLAG, notification);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                manager.notify(NOTIFICATION_FLAG, notification);
            }
        }, 1000);
    }

    /**
     * 初始化view
     */
    private void initView() {
        mWaveRecyclerView = (RecyclerView) findViewById(R.id.wave_recycler);
//        mDataRecyclerView = (RecyclerView) findViewById(R.id.data_recycler);

        mWaveAdapter = new WaveRecyclerAdapter();

        mWaveRecyclerView.setAdapter(mWaveAdapter);
//        mDataRecyclerView.setAdapter(mWaveDataAdapter);

        mWaveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            //从数据库获取要显示的波形
            List<Params> paramses = DBManager.getConfigDBHelper(this).getRuntimeExceptionDao(
                    Params.class).queryBuilder().orderBy("OrderBy", true).where().eq("Sort", 1)
                    .query();
            mWaveAdapter.setParamses(paramses);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initAidlService();

        //注册广播，是否显示底部的血压和体温参数界面
        IntentFilter filter = new IntentFilter(GlobalConstant.ACTION_SHOW_TEMP_NIBP);
        filter.addAction(GlobalConstant.ACTION_DISMISS_TEMP_NIBP);
        filter.addAction(GlobalConstant.ACTION_CENTRAL_STATE);
        registerReceiver(receiver, filter);
        //注册wifi广播
        wifiReceiver = new NetWifiConnectReceiver();
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(wifiReceiver, wifiFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        //跳转设置界面
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止绘制波形
        mWaveAdapter.destroy();

        //TODO 停止所有正在测量的操作  其实主动的也就血压这一个
        //停止测量血压
//        sendBroadcast(new Intent(GlobalConstant.ACTION_UPDATE_DATA+140104));
        DBManager.releaseHelper(this, null);
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);
        unregisterReceiver(receiver);
        unregisterReceiver(wifiReceiver);
        unbindService(serviceConnection);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //复写该返回键，不继承原来的方法，屏蔽掉
    }

    /**
     * 启动aidl服务
     */
    public void initAidlService() {
        Intent intent = new Intent(GlobalConstant.ACTION_SERVICE);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
                @Override
                public void sendWave(int param, byte[] bytes) {

                    switch (param) {
                        case KParamType.ECG_I:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }
                            break;
                        case KParamType.ECG_II:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }

                            break;
                        case KParamType.ECG_III:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }
                            break;
                        case KParamType.ECG_AVR:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }
                            break;
                        case KParamType.ECG_AVL:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }
                            break;
                        case KParamType.ECG_AVF:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }
                            break;
                        case KParamType.ECG_V1:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }

                            break;
                        case KParamType.ECG_V2:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }

                            break;
                        case KParamType.ECG_V3:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }

                            break;
                        case KParamType.ECG_V4:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }

                            break;
                        case KParamType.ECG_V5:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }

                            break;
                        case KParamType.ECG_V6:
                            if (isChecking) {
                                mWaveAdapter.setData(param, bytes);
                            }

                            break;
                        case KParamType.SPO2_WAVE:
                            if (isChecking) {
                                //TODO spo2波形数据回调
                                mWaveAdapter.setData(param, bytes);
                            }
                            break;
                        case KParamType.RESP_WAVE:
//                            showConnectState(false);
                            mWaveAdapter.setData(param, bytes);
                            break;
//                        屏蔽CO2_WAVE
//                        case KParamType.CO2_WAVE:
//                            mWaveAdapter.setData(param, bytes);
//                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendTrend(int param, int value) {
                    int hrValue;
                    switch (param) {
                        case KParamType.ECG_HR:
                            if (isChecking) {
                                hrValue = value / GlobalConstant.TREND_FACTOR;
                                if ((Math.abs(hrInvalid - hrValue) < 4) && hrValue !=
                                        GlobalConstant.INVALID_DATA) {
                        /*Log.d("Test", "_mesureCount" + _measureCount);*/
//                                    if ((_measureCount++) == 120) {
//                                        isChecking = false;
//                                        //��Ҫ�������
//                                        aidlServer.saveTrend(KParamType.ECG_HR, hrInvalid *
// GlobalConstant.TREND_FACTOR);
//                                        aidlServer.saveToDb2();
//                                        return;
//                                    }
                                } else {
                                    hrInvalid = hrValue;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                    int leadoff = -1;
                    switch (param) {
                        case 0x10:
                            leadoff = value;
//                            if (leadoff == 496 || leadoff == 0) {
//                                isEcgConnect = true;
//                                if (!isChecking&&!isTimeOut) {
//                                    Toast.makeText(MainActivity.this,getString(R.string
// .ecg_pls_keep_quiet_wait_check),Toast.LENGTH_SHORT).show();
//                                }
//                            } else if (leadoff == GlobalConstant.INVALID_DATA) {
//                                isEcgConnect = false;
//                                isChecking = false;
//                                Toast.makeText(MainActivity.this,getString(R.string
// .ecg_pls_checkfordevice),Toast.LENGTH_SHORT).show();
//                            } else {
//                                isEcgConnect = false;
//                                isChecking = false;
//                                Toast.makeText(MainActivity.this,getString(R.string
// .ecg_pls_checkforline),Toast.LENGTH_SHORT).show();
//
//                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /*Log.i("Test", "AIDLClient.onServiceDisconnected()...");*/
            /*_aidlInterface = null;*/
            aidlServer = null;
            isChecking = false;
            Toast.makeText(MainActivity.this, getString(R.string.ecg_pls_checkfordevice), Toast
                    .LENGTH_SHORT).show();
        }
    };

    /**
     * 保存波形图
     * @param server 服务
     * @param param 参数
     * @param bytes 字节
     */
    public void saveWave(AIDLServer server, int param, byte[] bytes) {
        try {
            server.savedWave(param, UnitConvertUtil.bytesToHexString(bytes));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Params params = new Params();
            params.setParaId(-1);
            if (GlobalConstant.ACTION_SHOW_TEMP_NIBP.equals(intent.getAction())) {
                int showType = intent.getIntExtra(GlobalConstant.TYPE_SHOW, Params.SHOW_ALL);

                params.setShowType(showType);
                //TODO
                if (mWaveAdapter.getParamses().contains(params)) {
                    mWaveAdapter.getParamses().get(mWaveAdapter.getItemCount() - 1).setShowType(
                            showType);
                    mWaveAdapter.notifyDataSetChanged();

//                    Toast.makeText(MainActivity.this, ""+showType, Toast.LENGTH_SHORT).show();
                    return;
                }
                mWaveAdapter.addParams(params);
            } else if (GlobalConstant.ACTION_DISMISS_TEMP_NIBP.equals(intent.getAction())) {
                if (mWaveAdapter.getParamses().contains(params)) {
                    mWaveAdapter.getParamses().remove(params);
                    mWaveAdapter.notifyDataSetChanged();
                }
            } else if (GlobalConstant.ACTION_CENTRAL_STATE.equals(intent.getAction())) {
                //收到广播
                int state = intent.getIntExtra(GlobalConstant.CENTRAL_STATE, 0);
                if (state == GlobalConstant.CENTRAL_CONNECT_FAIL) {
                    //连接失败
                    showConnectState(false);
                } else if (state == GlobalConstant.CENTRAL_CONNECT_SUCCESS) {
                    //连接成功
                    showConnectState(true);
                }
            }
        }
    };

    /**
     * wifi广播，监听wifi已连接但上不了网，导致中央站连接异常
     */
    public class NetWifiConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //wifi连接
            if (RequestUtils.isWifiAvailable(MainActivity.this)) {
                OkHttpUtils.get().url(URL).build().execute(new StringCallback
                        () {

                    @Override
                    public String parseNetworkResponse(Response response, int id) {
                        return response.code() + "";
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!response.equals(RESPONE_CODE)) {
                            ToastUtils.toastContent(MainActivity.this, getString(R.string
                                    .wifi_internet_no));
                        }
                    }
                });
            }else{
                ToastUtils.toastContent(MainActivity.this, getString(R.string
                        .wifi_unconnent));
            }
        }
    }
}
