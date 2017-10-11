package com.konsung.defineview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.service.AIDLServer;
import com.konsung.util.DPUtils;
import com.konsung.util.DataUtils;
import com.konsung.util.FontManager;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;

/**
 * Created by chengminghui on 15/9/4.
 * 二氧化碳的参数显示界面
 */
public class CO2DataView extends BaseDataView implements AIDLServer.SendTrend {
    private TextView tvAwrrValue;
    private TextView tvAwrrUpValue;
    private TextView tvAwrrDownValue;

    private TextView tvCo2Value;
    private TextView tvDownValue;
    private TextView tvUpValue;

    private TextView tvFico2Value;
    private TextView tvFico2UpValue;

    private ImageView ivCo2Alarm;
    private ImageView ivAwrrAlarm;
    private ImageView ivFico2Alarm;

    private int eUpValue;
    private int eDownValue;
    private int aUpValue;
    private int aDownValue;
    private int fUpValue;

    private boolean isEUp;
    private boolean isEDown;
    private boolean isAUp;
    private boolean isADown;
    private boolean isFUp;

    private boolean isTwinkle;
    private boolean flag;

    private boolean etOnOff;
    private boolean awrrOnOff;
    private boolean fiOnOff;

    public CO2DataView(Context context) {
        this(context,null);
    }

    public CO2DataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CO2DataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //注册广播，当设置界面的相关参数修改后实时通知参数界面进行刷新；后面的id请参考数据库对应id
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160201);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160202);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160203);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160204);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160205);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160206);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160207);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA+160208);
        getContext().registerReceiver(receiver, filter);

        FontManager.changeFonts(getContext(),this);
    }

    public void init() {
        inflate(getContext(), R.layout.co2_data_view,this);
        tvCo2Value = (TextView) findViewById(R.id.co2_value);
        tvUpValue = (TextView) findViewById(R.id.up_value);
        tvDownValue = (TextView) findViewById(R.id.down_value);

        tvAwrrDownValue = (TextView) findViewById(R.id.awrr_down_value);
        tvAwrrUpValue = (TextView) findViewById(R.id.awrr_up_value);
        tvAwrrValue = (TextView) findViewById(R.id.awrr_value);

        tvFico2Value = (TextView) findViewById(R.id.fico2_value);
        tvFico2UpValue = (TextView) findViewById(R.id.fico2_up_value);

        ivCo2Alarm = (ImageView) findViewById(R.id.co2_alarm);
        ivAwrrAlarm = (ImageView) findViewById(R.id.awrr_alarm);
        ivFico2Alarm = (ImageView) findViewById(R.id.fico2_alarm);

        updateData();

        super.init();
    }

    /**
     * 从数据库获取参数值并设置到view上
     */
    private void updateData(){
        eUpValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(),160202);
        eDownValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(),160203);
        aUpValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(),160207);
        aDownValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(),160208);
        fUpValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(),160205);

        etOnOff = DPUtils.getSelectValueBySortAttrId(getContext(),160201) > 0;
        awrrOnOff = DPUtils.getSelectValueBySortAttrId(getContext(),160206) > 0;
        fiOnOff = DPUtils.getSelectValueBySortAttrId(getContext(),160204) > 0;

        tvUpValue.setText(eUpValue+"");
        tvDownValue.setText(eDownValue+"");
        tvAwrrUpValue.setText(aUpValue+"");
        tvAwrrDownValue.setText(aDownValue+"");
        tvFico2UpValue.setText(fUpValue+"");
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }
    };

    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //根据不同条件显示不同报警图标
            if(isEUp){
                ivCo2Alarm.setImageResource(R.drawable.alarm_high);
            }

            if(isEDown){
                ivCo2Alarm.setImageResource(R.drawable.alarm_low);
            }

            if(isAUp){
                ivAwrrAlarm.setImageResource(R.drawable.alarm_high);
            }

            if(isADown){
                ivAwrrAlarm.setImageResource(R.drawable.alarm_low);
            }

            if(isFUp){
                ivFico2Alarm.setImageResource(R.drawable.alarm_high);
            }

            //设置显示值的闪烁效果
            tvUpValue.setSelected(isEUp ? flag : false);
            tvDownValue.setSelected( isEDown ? flag :false);
            tvCo2Value.setSelected( isEDown || isEUp ? flag :false);

            tvAwrrUpValue.setSelected(isAUp ? flag :false);
            tvAwrrDownValue.setSelected(isADown ? flag :false);
            tvAwrrValue.setSelected( isADown || isAUp ? flag :false);

            tvFico2UpValue.setSelected( isFUp ? flag :false);
            tvFico2Value.setSelected( isFUp ? flag :false);

            flag = !flag;

            if(isTwinkle){
                mHandler.postDelayed(this,500);
            }
        }
    };

    @Override
    public void onTrendData(AIDLServer aidlServer) {
        //添加对相应参数值的监听
        aidlServer.addSendTrendListeners(new int[]{KParamType.CO2_ETCO2,KParamType.CO2_AWRR,KParamType.CO2_FICO2},this);
    }


    @Override
    public void sendTrend(int param, int value) {
        value = value / GlobalConstant.TREND_FACTOR;
        if(param == KParamType.CO2_ETCO2){
            tvCo2Value.setText(value+"");
            isEUp = value > eUpValue && etOnOff && DataUtils.isValid(tvCo2Value, value);
            isEDown = value < eDownValue && etOnOff && DataUtils.isValid(tvCo2Value, value);
        }else if(param == KParamType.CO2_AWRR){
            tvAwrrValue.setText(value+"");
            isAUp = value > aUpValue && awrrOnOff && DataUtils.isValid(tvAwrrValue, value);
            isADown = value < aDownValue && awrrOnOff && DataUtils.isValid(tvAwrrValue, value);
        }else if(param == KParamType.CO2_FICO2){
            tvFico2Value.setText(value+"");
            isFUp = value > fUpValue && fiOnOff && DataUtils.isValid(tvFico2Value, value);
        }

        if(isEUp || isEDown || isADown|| isAUp || isFUp){
            //如果没有启动任务则启动
            if(!isTwinkle){
                isTwinkle = true;
                mHandler.post(runnable);
            }
            sm.playLowSound();
        }else {
            isTwinkle = false;
        }

        //设置相应报警view是否显示
        if(isEUp || isEDown){
            ivCo2Alarm.setVisibility(VISIBLE);
        }else{
            ivCo2Alarm.setVisibility(GONE);
        }

        if(isADown || isAUp){
            ivAwrrAlarm.setVisibility(VISIBLE);
        }else{
            ivAwrrAlarm.setVisibility(GONE);
        }

        if(isFUp){
            ivFico2Alarm.setVisibility(VISIBLE);
        }else {
            ivFico2Alarm.setVisibility(GONE);
        }

        etOnOff = DPUtils.getSelectValueBySortAttrId(getContext(),160201) > 0;
        awrrOnOff = DPUtils.getSelectValueBySortAttrId(getContext(),160206) > 0;
        fiOnOff = DPUtils.getSelectValueBySortAttrId(getContext(),160204) > 0;
    }

    @Override
    public void setConfigIntentParams(Intent intent) {
        intent.putExtra(GlobalConstant.SORT_ID,16);
    }

    /**
     * TODO 切换病人之后,需要把之前的警报关闭  把数值重置
     * 不是把警报开关关闭
     */
    @Override
    public void removeWarnAndReset(){
        etOnOff = false; //手动设置为false  不保存数据库  下次测量会重置
        awrrOnOff = false;
        fiOnOff = false;
        tvCo2Value.setText("-?-");
        tvAwrrValue.setText("-?-");
        tvFico2Value.setText("-?-");
    }

}
