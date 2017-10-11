package com.konsung.defineview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
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
 * 呼吸参数view
 */
public class RespDataView extends BaseDataView implements AIDLServer.SendTrend, AIDLServer
        .SendConfig {
    private TextView tvUpValue;
    private TextView tvDownValue;
    private TextView tvValue;
    private ImageView ivAlarm;

    private int upValue;
    private int downValue;

    private boolean isUp;
    private boolean isDown;

    private boolean isTwinkle;
    private boolean onoff;
    /**
     * 是否处于窒息状态
     */
    private boolean isStifle = false;
    private int tempValue = -10; //临时呼吸率数值【-10代表无效值】

    public RespDataView(Context context) {
        this(context, null);
    }

    public RespDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RespDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 130201);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 130202);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 130203);
        getContext().registerReceiver(receiver, filter);

        FontManager.changeFonts(getContext(), this);
    }

    public void init() {
        inflate(getContext(), R.layout.resp_data_view, this);
        tvUpValue = (TextView) findViewById(R.id.up_value);
        tvDownValue = (TextView) findViewById(R.id.down_value);
        tvValue = (TextView) findViewById(R.id.resp_value);
        ivAlarm = (ImageView) findViewById(R.id.alarm);
        updateData();
        super.init();
    }

    private void updateData() {
        upValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 130202);
        downValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 130203);

        onoff = DPUtils.getSelectValueBySortAttrId(getContext(), 130201) > 0;

        tvUpValue.setText(upValue + "");
        tvDownValue.setText(downValue + "");
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //开关报警，数据更新
            updateData();
            isUp = tempValue > upValue && DataUtils.isValid(tvValue, tempValue * GlobalConstant
                    .TREND_FACTOR);
            isDown = tempValue < downValue && DataUtils.isValid(tvValue, tempValue * GlobalConstant
                    .TREND_FACTOR);
            if (onoff) {
                if (isUp || isDown) {
                    if (!isTwinkle) {
                        //如果没有报警
                        isTwinkle = true;
                        mHandler.postDelayed(runnable, 500);
                        ivAlarm.setVisibility(VISIBLE);
                    } else {
                        //如果正在报警,先清除报警任务，在重新跑任务
                        mHandler.removeCallbacks(runnable);
                        mHandler.postDelayed(runnable, 500);
                    }
                } else {
                    isTwinkle = false;
                    mHandler.removeCallbacks(runnable);
                    ivAlarm.setVisibility(GONE);
                }
            } else {
                isTwinkle = false;
                mHandler.removeCallbacks(runnable);
                ivAlarm.setVisibility(GONE);
                tvValue.setSelected(false);
                tvUpValue.setSelected(false);
                tvDownValue.setSelected(false);
            }
        }
    };

    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isUp) {
                ivAlarm.setVisibility(View.VISIBLE);
                tvUpValue.setSelected(!tvValue.isSelected());
                tvValue.setSelected(!tvValue.isSelected());
                ivAlarm.setImageResource(R.drawable.alarm_high);
            } else if (isDown) {
                ivAlarm.setVisibility(View.VISIBLE);
                tvValue.setSelected(!tvValue.isSelected());
                tvDownValue.setSelected(!tvValue.isSelected());
                ivAlarm.setImageResource(R.drawable.alarm_low);
            }
            if (isUp && onoff) {
                sm.playHeightSound();
            }
            if (isDown && onoff) {
                sm.playLowSound();
            }

            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onTrendData(AIDLServer aidlServer) {
        aidlServer.addSendTrendListener(KParamType.RESP_RR, this);
        aidlServer.addSendConfigListener(GlobalConstant.NET_NIBP_CONFIG, this);
    }

    @Override
    public void sendTrend(int param, int value) {

        if (param == KParamType.RESP_RR) {
            if (value != GlobalConstant.INVALID_DATA) {
                value = value / GlobalConstant.TREND_FACTOR;
                //窒息状态时显示无效值
                if (isStifle) {
                    tvValue.setText("-?-");
                    tempValue = -10;
                    ivAlarm.setVisibility(GONE);
                    return;
                }
                if (value == 0) {
                    tvValue.setText("-?-");
                } else {
                    tvValue.setText(value + "");
                }
//            Log.e("呼吸率值：", "->" + value);
                tempValue = value;
                isUp = value > upValue && DataUtils.isValid(tvValue, value * GlobalConstant
                        .TREND_FACTOR);
                isDown = value < downValue && DataUtils.isValid(tvValue, value * GlobalConstant
                        .TREND_FACTOR);
                onoff = DPUtils.getSelectValueBySortAttrId(getContext(), 130201) > 0;
                if ((isUp || isDown) && onoff) {
                    if (!isTwinkle) {
                        isTwinkle = true;
                        mHandler.post(runnable);
                    }
                } else {
                    isTwinkle = false;
                    ivAlarm.setVisibility(GONE);
                }
            } else {
                //无效值去除播放声音【可能会有1-2秒声音遗留，是因为发送数据延迟】
                tvValue.setText("-?-");
                tempValue = -10;
                isUp = false;
                isDown = false;
                ivAlarm.setVisibility(GONE);
                tvValue.setSelected(false);
                tvUpValue.setSelected(false);
                tvDownValue.setSelected(false);
            }
        }else{
        }
    }

    @Override
    public void setConfigIntentParams(Intent intent) {
        intent.putExtra(GlobalConstant.SORT_ID, 13);
    }

    @Override
    public void sendConfig(int param, int value) {
//        Log.e("abc-respdata", "config数据:"+param +":"+value);
        if (param == 0x05) {
            if (value == 1) {//窒息开始
                isStifle = true;
            } else if (value == 0) {//窒息结束
                isStifle = false;
            }
        }
    }

    @Override
    public void removeWarnAndReset() {
        super.removeWarnAndReset();
        onoff = false;
        tempValue = -10;
        tvValue.setText("-?-");
    }
}
