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
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;

/**
 * Created by chengminghui on 15/9/4.
 * 心电的参数界面
 */
public class EcgDataView extends BaseDataView implements AIDLServer.SendTrend, AIDLServer
        .SendConfig {
    /**
     * 起搏开关
     */
    private static final String PACE_OPEN = "开";
    private static final String PACE_CLOSE = "关";
    private static final String PACE_NO_SETTING = "未设置";
    private TextView tvUpValue;
    private TextView tvDownValue;
    private TextView tvValue;
    private TextView tvPvcsValue;
    private ImageView ivAlarm;
    private ImageView ivPvcsAlarm;
//    private ImageView ivHeart;

    private ImageView ivPulse;

    private int ecgUpValue;
    private int ecgDownValue;
    private int pvcUpValue;
    private int pvcDownValue;

//    private boolean isHeart;
    private boolean isEcgUp;
    private boolean isEcgDown;
    private boolean isPvcsOut;
    private boolean isPvcsShow; //起搏器开，pvcs不显示数据
    private boolean isTwinkle; //是否报警
    /**
     * 是否心脏停博
     */
    private boolean isHRStop;

    private boolean ecgOnOff;
    private boolean pvcOnOff;
    private int tempValue; //临时测量值

    public EcgDataView(Context context) {
        this(context, null);
    }

    public EcgDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EcgDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_ONOFF);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_UP);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_DOWN);
        //注册导联切换
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_DL);
//        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PVC_UP);
//        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PVC_DOWN);
//        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PVC_ONOFF);
        //注册起搏开关的广播
//        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PACE_MAKING);

        getContext().registerReceiver(receiver, filter);

        mHandler.post(runnable);
    }

    @Override
    public void init() {
        inflate(getContext(), R.layout.ecg_data_view, this);
        tvUpValue = (TextView) findViewById(R.id.up_value);
        tvDownValue = (TextView) findViewById(R.id.down_value);
        tvValue = (TextView) findViewById(R.id.ecg_value);
        tvPvcsValue = (TextView) findViewById(R.id.pvcs_value);
        ivAlarm = (ImageView) findViewById(R.id.alarm);
//        ivHeart = (ImageView) findViewById(R.id.heart_beat);
        ivPvcsAlarm = (ImageView) findViewById(R.id.alarm_pvcs);

        ivPulse = (ImageView) findViewById(R.id.pulse);
        //获取起搏分析的开关状态
//        String paceMakeName = DPUtils.getSelectValueBySortAttrName(getContext(), GlobalConstant
//                .PACE_MAKING);

//        initPulse(paceMakeName);

        updateDownValue();
        updateUpValue();
        updatePvcDownValue();
        updatePvcUpValue();

        updateEcgOnOff();
        //updatePvcOnOff();

        super.init();
    }

    /**
     * 根据起搏开关状态显示pvcs标志
     * @param paceMakeName 起搏名称
     */
    private void initPulse(String paceMakeName) {
        //这个功能未实现
        if (paceMakeName.equals(PACE_OPEN)) {
            //开
            isPvcsShow = false;
            ivPulse.setImageResource(R.drawable.pace_on);
            tvPvcsValue.setText(getContext().getString(R.string.no_pvcs));
        } else if (paceMakeName.equals(PACE_CLOSE)) {
            //关
            isPvcsShow = true;
            ivPulse.setImageResource(R.drawable.pace_off);
        } else if (paceMakeName.equals(PACE_NO_SETTING)) {
            //未设置
            isPvcsShow = true;
            ivPulse.setImageResource(R.drawable.pace_unknow);
        }
    }

    private Handler mHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (isEcgUp) {
                tvUpValue.setSelected(!tvUpValue.isSelected());
                tvValue.setSelected(!tvValue.isSelected());
                ivAlarm.setImageResource(R.drawable.alarm_high);
            }
            if (isEcgDown) {
                tvDownValue.setSelected(!tvDownValue.isSelected());
                tvValue.setSelected(!tvValue.isSelected());
                ivAlarm.setImageResource(R.drawable.alarm_low);
            }

         /*   if (isPvcsOut) {
                tvPvcsValue.setSelected(!tvPvcsValue.isSelected());
                ivPvcsAlarm.setImageResource(R.drawable.alarm_high);
            }*/
            mHandler.postDelayed(this, 500);
        }
    };

    private void updatePulse() {
        ecgUpValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 110202);
        tvUpValue.setText(ecgUpValue + "");
    }

    /**
     * 更新上限值
     */
    private void updateUpValue() {
        ecgUpValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 110202);
        tvUpValue.setText(ecgUpValue + "");
    }

    /**
     * 更新下限值
     */
    private void updateDownValue() {
        ecgDownValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 110203);
        tvDownValue.setText(ecgDownValue + "");
    }

    /**
     * 更新pvc上限值
     */
    private void updatePvcUpValue() {
        pvcUpValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 110205);
//        Log.e("pvcs上限值", String.valueOf(pvcUpValue));
    }

    /**
     * 更新pvc下限值
     */
    private void updatePvcDownValue() {
        pvcDownValue = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 110206);
//        Log.e("pvcs下限值", String.valueOf(pvcDownValue));
    }

    /**
     * 更新ecg开关
     */
    private void updateEcgOnOff() {
        ecgOnOff = DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant.ECG_ONOFF) > 0;
    }

    /**
     * 更新pvc开关
     */
    private void updatePvcOnOff() {
        pvcOnOff = DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant.PVC_ONOFF) > 0;
    }

    @Override
    public void onTrendData(AIDLServer aidlServer) {
        aidlServer.addSendTrendListeners(new int[]{KParamType.ECG_HR, KParamType.ECG_PVC}, this);
        aidlServer.addSendConfigListener(GlobalConstant.NET_ECG_CONFIG, this);
    }

    @Override
    public void sendTrend(int param, int value) {
        //心电切换导联，value可能为-1000，为无效趋势值，心电UI数字会50-0-50 重复2次，在此做一个判断，
        //判断是需要的心电数据参数
        if (param == KParamType.ECG_HR) {
            //判断无效值
            if (value != GlobalConstant.INVALID_TREND_DATA) {
                value = value / GlobalConstant.TREND_FACTOR;
                //心脏停博状态显示无效值
                if (isHRStop) {
                    tvValue.setText("-?-");
//                    isHeart = false;
                    isEcgUp = false;
                    isEcgDown = false;
                    ivAlarm.setVisibility(GONE);
                    tvValue.setSelected(false);
                    tvUpValue.setSelected(false);
                    tvDownValue.setSelected(false);
                    return;
                }

//                if (value > 0 && DataUtils.isValid(tvValue, value)) {
                tempValue = value;
                tvValue.setVisibility(VISIBLE);
                tvValue.setText(String.valueOf(value));
                isEcgUp = value > ecgUpValue && ecgOnOff;
                isEcgDown = value < ecgDownValue && ecgOnOff;
//                    isHeart = true;
//                ivHeart.setSelected(!ivHeart.isSelected());
                if (isEcgDown || isEcgUp) {
                    ivAlarm.setVisibility(VISIBLE);
                    if (isEcgDown) {
                        sm.playLowSound();
                    }
                    if (isEcgUp) {
                        sm.playHeightSound();
                    }
                } else {
                    ivAlarm.setVisibility(GONE);
                    tvValue.setSelected(false);
                    tvUpValue.setSelected(false);
                    tvDownValue.setSelected(false);
                }
//                }
//                else {
////                    isHeart = false;
//                    LogUtils.e("kkk", " ################value " + value);
//                    ivHeart.setSelected(false);
//                }
                //测完之后更新
                ecgOnOff = DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant
                        .ECG_ONOFF)
                        > 0;
            } else {
                //无效值去除播放声音【可能会有1-2秒声音遗留，是因为发送数据延迟】
                tvValue.setText("-?-");
//            isHeart = false;
                isEcgUp = false;
                isEcgDown = false;
                ivAlarm.setVisibility(GONE);
                tvValue.setSelected(false);
                tvUpValue.setSelected(false);
                tvDownValue.setSelected(false);
            }
        }/* else if (param == KParamType.ECG_PVC) {
                int pvcValue = value / GlobalConstant.TREND_FACTOR; //网络数据被放大100倍
                if(isPvcsShow){
                    tvPvcsValue.setText(pvcValue + "");
                }else{
                    tvPvcsValue.setText(getContext().getString(R.string.no_pvcs));
                }

                if ((value < pvcDownValue || value > pvcUpValue) && pvcOnOff && DataUtils.isValid
                        (tvPvcsValue, value)) {
                    isPvcsOut = true;
                    ivPvcsAlarm.setVisibility(VISIBLE);
                    sm.playHeightSound();
                } else {
                    isPvcsOut = false;
                    ivPvcsAlarm.setVisibility(GONE);
                    tvPvcsValue.setSelected(false);
                }
            }*/
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_UP).equals(intent
                    .getAction())) {
                updateUpValue();
                startAlarm();
            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_DOWN).equals
                    (intent.getAction())) {
                updateDownValue();
                startAlarm();

//            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PVC_DOWN).equals
//                    (intent.getAction())) {
//                updatePvcDownValue();
//            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PVC_UP).equals(intent
//                    .getAction())) {
//                updatePvcUpValue();
            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_ONOFF).equals
                    (intent.getAction())) {
                updateEcgOnOff();
                startAlarm();
//            else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PVC_ONOFF).equals
//                    (intent.getAction())) {
//                updatePvcOnOff();
//            }
            }
//            else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PACE_MAKING).equals
//                    (intent.getAction())) {
////                //起搏分析广播
////                String paceMakeName = DPUtils.getSelectValueBySortAttrName(getContext(),
////                        GlobalConstant
////                                .PACE_MAKING);
////                initPulse(paceMakeName);
//            }
            else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_DL).equals
                    (intent.getAction())) {
                //切换导联重新计算值
                tempValue = 0;
                tvValue.setText(getContext().getString(R.string.default_value));
            }
        }
    };

    private void startAlarm() {
        isEcgUp = tempValue > ecgUpValue && ecgOnOff;
        isEcgDown = tempValue < ecgDownValue && ecgOnOff;
        if (ecgOnOff) {
            if (isEcgUp || isEcgDown) {
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

    @Override
    public void setConfigIntentParams(Intent intent) {
        intent.putExtra(GlobalConstant.SORT_ID, 11);
    }

    @Override
    public void sendConfig(int param, int value) {
        if (param == 0x11) {//心率失常类型
            if (value == 0) { //心脏停博
                isHRStop = true;
//                //发送清除ecg wave广播
                getContext().sendBroadcast(new Intent("com.kongsung.ecg.clearcache"));
            } else {
                isHRStop = false;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void removeWarnAndReset() {
        ecgOnOff = false;
        tempValue = 0;
        tvValue.setText(getContext().getString(R.string.default_value));
//        tvPvcsValue.setText(getContext().getString(R.string.default_value));
    }
}
