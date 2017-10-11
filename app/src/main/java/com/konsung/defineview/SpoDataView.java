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
 * 血氧参数view
 */
public class SpoDataView extends BaseDataView implements AIDLServer.SendTrend {
    private TextView tvUpValue;
    private TextView tvMUpValue;
    private TextView tvDownValue;
    private TextView tvMDownValue;
    private TextView tvSpoValue;
    private TextView tvMValue;
    private ImageView ivAlarm;
    private ImageView ivMAlarm;

    private int upValue;
    private int downValue;
    private int mUpValue;
    private int mDownValue;

    private boolean isUp;
    private boolean isDown;
    private boolean isMUp;
    private boolean isMDown;

    private boolean isTwinkle;
    private boolean flag;

    private boolean spoOnOff; //血氧报警是否开启
    private boolean mOnOff; //脉率报警是否开启
    private int currValue = 0;

    public SpoDataView(Context context) {
        this(context, null);
    }

    public SpoDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpoDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 120201); //血氧开关
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 120202); //血氧上限
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 120203); //血氧下限
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 120204); //脉率开关
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 120205); //脉率上限
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 120206); //脉率下限
        getContext().registerReceiver(receiver, filter);

        FontManager.changeFonts(getContext(), this);
    }

    public void init() {
        inflate(getContext(), R.layout.spo_data_view, this);
        tvUpValue = (TextView) findViewById(R.id.up_value);
        tvDownValue = (TextView) findViewById(R.id.down_value);
        tvMDownValue = (TextView) findViewById(R.id.m_down_value);
        tvMUpValue = (TextView) findViewById(R.id.m_up_value);
        tvSpoValue = (TextView) findViewById(R.id.spo_value);
        tvMValue = (TextView) findViewById(R.id.m_value);
        ivAlarm = (ImageView) findViewById(R.id.alarm);
        ivMAlarm = (ImageView) findViewById(R.id.m_alarm);

        updateData();
        super.init();
    }

    private void updateData() {
        upValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 120202);
        downValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 120203);
        mUpValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 120205);
        mDownValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 120206);

        spoOnOff = DPUtils.getSelectValueBySortAttrId(getContext(), 120201) > 0;
        mOnOff = DPUtils.getSelectValueBySortAttrId(getContext(), 120204) > 0;
        tvUpValue.setText(upValue + "");
        tvDownValue.setText(downValue + "");
        tvMUpValue.setText(mUpValue + "");
        tvMDownValue.setText(mDownValue + "");
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }
    };

    Handler mHandler = new Handler();
    /**
     * 通过每500毫秒让TextView选中与被选中，有动画的效果
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isUp) {
                ivAlarm.setImageResource(R.drawable.alarm_high);
            } else if (isDown) {
                ivAlarm.setImageResource(R.drawable.alarm_low);
            }

            if (isMUp) {
                ivMAlarm.setImageResource(R.drawable.alarm_high);
            } else if (isMDown) {
                ivMAlarm.setImageResource(R.drawable.alarm_low);
            }

            tvUpValue.setSelected(isUp ? flag : false);
            tvDownValue.setSelected(isDown ? flag : false);
            tvSpoValue.setSelected(isUp || isDown ? flag : false);

            tvMUpValue.setSelected(isMUp ? flag : false);
            tvMDownValue.setSelected(isMDown ? flag : false);
            tvMValue.setSelected(isMDown || isMUp ? flag : false);

            flag = !flag;
            if (isTwinkle) {
                mHandler.postDelayed(this, 500);
            }
        }
    };

    @Override
    public void onTrendData(AIDLServer aidlServer) {
        aidlServer.addSendTrendListeners(new int[]{KParamType.SPO2_TREND, KParamType.SPO2_PR},
                this);
    }

    private static boolean isMeasureSpo2 = false;

    @Override
    public void sendTrend(int param, int value) {
        value = value / GlobalConstant.TREND_FACTOR;

        if (value * GlobalConstant.TREND_FACTOR == GlobalConstant.INVALID_TREND_DATA) {
            //发送广播 停止ware波形 清除ware数据
            if (isMeasureSpo2) {
                getContext().sendBroadcast(new Intent(GlobalConstant.ACTION_SPO2_STOP));
            }
            isMeasureSpo2 = false;
            tvSpoValue.setText("-?-");
            tvMValue.setText("-?-");
        } else if (param == KParamType.SPO2_TREND) {
            isMeasureSpo2 = true;
            tvSpoValue.setText(value + "");
            isUp = value > upValue && spoOnOff && DataUtils.isValid(tvSpoValue, value *
                    GlobalConstant.TREND_FACTOR);
            isDown = value < downValue && spoOnOff && DataUtils.isValid(tvSpoValue, value *
                    GlobalConstant.TREND_FACTOR);
        } else if (param == KParamType.SPO2_PR) {
            tvMValue.setText(value + "");
            isMUp = value > mUpValue && mOnOff && DataUtils.isValid(tvMValue, value *
                    GlobalConstant.TREND_FACTOR);
            isMDown = value < mDownValue && mOnOff && DataUtils.isValid(tvMValue, value *
                    GlobalConstant.TREND_FACTOR);
        }

        if (isMUp || isMDown || isUp || isDown) {
            if (!isTwinkle) {
                isTwinkle = true;
                mHandler.post(runnable);
            }
            startAudioService();
        } else {
            isTwinkle = false;
            stopAudioService();
        }

        if (isUp || isDown) {
            ivAlarm.setVisibility(VISIBLE);
        } else {
            ivAlarm.setVisibility(GONE);
        }

        if (isMUp || isMDown) {
            ivMAlarm.setVisibility(VISIBLE);
        } else {
            ivMAlarm.setVisibility(GONE);
        }

        //-10为无效趋势值
        if (value == -10) {
            mHandler.removeCallbacks(runnable); //移除run
            stopAudioService();

            //重置内容
            ivAlarm.setVisibility(GONE);
            ivMAlarm.setVisibility(GONE);
            isMUp = isMDown = isUp = isDown = false;
            showBrightText();
        }

        spoOnOff = DPUtils.getSelectValueBySortAttrId(getContext(), 120201) > 0;
        mOnOff = DPUtils.getSelectValueBySortAttrId(getContext(), 120204) > 0;
    }

    /**
     * 不接入血氧信号，显示值都不选中，文字加亮
     */
    private void showBrightText() {
        tvUpValue.setSelected(false);
        tvDownValue.setSelected(false);
        tvSpoValue.setSelected(false);
        tvMUpValue.setSelected(false);
        tvMDownValue.setSelected(false);
        tvMValue.setSelected(false);
    }

    @Override
    public void setConfigIntentParams(Intent intent) {
        intent.putExtra(GlobalConstant.SORT_ID, 12);
    }

    @Override
    public void removeWarnAndReset() {
        super.removeWarnAndReset();
        tvSpoValue.setText("-?-");
        tvMValue.setText("-?-");
        spoOnOff = false;
        mOnOff = false;
    }
}
