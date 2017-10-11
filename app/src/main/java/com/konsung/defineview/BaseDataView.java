package com.konsung.defineview;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.konsung.activity.ConfigActivity;
import com.konsung.service.AIDLServer;
import com.konsung.service.AudioService;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SoundManager;

/**
 * Created by Cmad on 2015/9/9.
 * 参数view的基类
 */
public class BaseDataView extends LinearLayout implements View.OnClickListener {

    /**
     * 播放声音service的intent
     */
    private Intent audioIntent;

    /**
     * 获取参数值的server
     */
    public AIDLServer aidlServer;

    public SoundManager sm;

    public BaseDataView(Context context) {
        this(context, null);
    }

    public BaseDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        sm = SoundManager.getInstance(getContext());
        initAidlService();
        setOnClickListener(this);
        audioIntent = new Intent(getContext(), AudioService.class);

        //切换病人
        IntentFilter switchPatientFilter = new IntentFilter("com.kongsung.notify.switchpatient");
        getContext().registerReceiver(switchPatientReceiver, switchPatientFilter);
    }

    private BroadcastReceiver switchPatientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //切换病人 由需要的子类去完成
            removeWarnAndReset();
        }
    };

    /**
     * TODO 切换病人之后,需要把之前的警报关闭  把数值重置
     * 不是把警报开关关闭
     */
    public void removeWarnAndReset(){}


    /**
     *播放报警音。目前只有上下限报警音
     */
    public void startAudioService()
    {
        getContext().startService(audioIntent);
    }

    public void stopAudioService(){
        if(audioIntent!=null) {
            getContext().stopService(audioIntent);
        }
    }
    public void initAidlService() {
        Intent _intent = new Intent(GlobalConstant.ACTION_SERVICE);
        getContext().startService(_intent);
        getContext().bindService(_intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }


    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
//            Toast.makeText(getContext(), "aidlService onBind", Toast.LENGTH_SHORT).show();
            onTrendData(aidlServer);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void onTrendData(AIDLServer aidlServer){

    }


    @Override
    public void onClick(View v) {
        //打开配置界面
        Intent intent = new Intent(getContext(), ConfigActivity.class);
        setConfigIntentParams(intent);
        getContext().startActivity(intent);
    }

    /**
     * 设置启动配置界面需要的参数
     * @param intent
     */
    public void setConfigIntentParams(Intent intent){

    }
}
