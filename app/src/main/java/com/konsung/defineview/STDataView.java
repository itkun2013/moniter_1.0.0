package com.konsung.defineview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.service.AIDLServer;
import com.konsung.util.DPUtils;
import com.konsung.util.DataUtils;
import com.konsung.util.DateUtil;
import com.konsung.util.GlobalConstant;

import java.util.HashMap;

import static com.konsung.util.KParamType.ECG_ST_AVF;
import static com.konsung.util.KParamType.ECG_ST_AVL;
import static com.konsung.util.KParamType.ECG_ST_AVR;
import static com.konsung.util.KParamType.ECG_ST_I;
import static com.konsung.util.KParamType.ECG_ST_II;
import static com.konsung.util.KParamType.ECG_ST_III;
import static com.konsung.util.KParamType.ECG_ST_V1;
import static com.konsung.util.KParamType.ECG_ST_V2;
import static com.konsung.util.KParamType.ECG_ST_V3;
import static com.konsung.util.KParamType.ECG_ST_V4;
import static com.konsung.util.KParamType.ECG_ST_V5;
import static com.konsung.util.KParamType.ECG_ST_V6;

/**
 * Created by chengminghui on 15/9/4.
 * STD参数view
 */
public class STDataView extends BaseDataView implements AIDLServer.SendTrend {
    private TextView tvUpValue;
    private TextView tvDownValue;
    private TextView tvIValue;
    private TextView tvIIValue;
    private TextView tvIIIValue;
    private TextView tvAvrValue;
    private TextView tvAvlValue;
    private TextView tvAvfValue;
    private TextView tvV1Value;
    private TextView tvV2Value;
    private TextView tvV3Value;
    private TextView tvV4Value;
    private TextView tvV5Value;
    private TextView tvV6Value;
    private TextView tvValue3Lead;
    private ImageView ivAlarm;
    private ImageView ivAlarm3Lead;

    private LinearLayout llV;
    private LinearLayout llV2;
    private RelativeLayout rl5Or12Lead;
    private RelativeLayout rl3Lead;

    private float upValue;
    private float downValue;

    private boolean isUp;
    private boolean isDown;

    private boolean isI;
    private boolean isII;
    private boolean isIII;
    private boolean isAVF;
    private boolean isAVL;
    private boolean isAVR;
    private boolean isV1;
    private boolean isV2;
    private boolean isV3;
    private boolean isV4;
    private boolean isV5;
    private boolean isV6;
    private boolean is3Lead = false;

    private boolean isTwinkle; //是否闪烁

    private boolean flag;
    private boolean onoff; //ST开关
    private HashMap<Integer, Float> stValueMAP = new HashMap<>();

    public STDataView(Context context) {
        this(context, null);
    }

    public STDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public STDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ST_ONOFF);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ST_UP);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ST_DOWN);
        //ST段分析广播
//        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PACE_MAKING);
        getContext().registerReceiver(receiver, filter);
    }

    public void init() {
        inflate(getContext(), R.layout.st_data_view, this);
        tvUpValue = (TextView) findViewById(R.id.pos_value);
        tvDownValue = (TextView) findViewById(R.id.nag_value);
        tvIValue = (TextView) findViewById(R.id.i_value_tv);
        tvIIValue = (TextView) findViewById(R.id.ii_value_tv);
        tvIIIValue = (TextView) findViewById(R.id.iii_value_tv);
        tvAvrValue = (TextView) findViewById(R.id.avr_value_tv);
        tvAvlValue = (TextView) findViewById(R.id.avl_value_tv);
        tvAvfValue = (TextView) findViewById(R.id.avf_value_tv);
        tvV1Value = (TextView) findViewById(R.id.v1_value_tv);
        tvV2Value = (TextView) findViewById(R.id.v2_value_tv);
        tvV3Value = (TextView) findViewById(R.id.v3_value_tv);
        tvV4Value = (TextView) findViewById(R.id.v4_value_tv);
        tvV5Value = (TextView) findViewById(R.id.v5_value_tv);
        tvV6Value = (TextView) findViewById(R.id.v6_value_tv);
        ivAlarm = (ImageView) findViewById(R.id.alarm);
        ivAlarm3Lead = (ImageView) findViewById(R.id.iv_alarm_3lead);
        llV = (LinearLayout) findViewById(R.id.v_layout);
        llV2 = (LinearLayout) findViewById(R.id.v2_layout);
        rl5Or12Lead = (RelativeLayout) findViewById(R.id.rl_5or12_lead);
        rl3Lead = (RelativeLayout) findViewById(R.id.rl_3_lead);
        tvValue3Lead = (TextView) findViewById(R.id.tv_value_3lead);

        updateDownValue();
        updateUpValue();
        updateOnOff();
        if (judgeLead() == 0) {
            setStTo3Lead(true);
        } else {
            setStTo3Lead(false);
        }
        super.init();
    }

    /**
     * 初始化map值
     */
    public void initMapValue() {
        stValueMAP.put(ECG_ST_I, 0.0f);
        stValueMAP.put(ECG_ST_II, 0.0f);
        stValueMAP.put(ECG_ST_III, 0.0f);
        stValueMAP.put(ECG_ST_AVR, 0.0f);
        stValueMAP.put(ECG_ST_AVL, 0.0f);
        stValueMAP.put(ECG_ST_AVF, 0.0f);
        stValueMAP.put(ECG_ST_V1, 0.0f);
        stValueMAP.put(ECG_ST_V2, 0.0f);
        stValueMAP.put(ECG_ST_V3, 0.0f);
        stValueMAP.put(ECG_ST_V4, 0.0f);
        stValueMAP.put(ECG_ST_V5, 0.0f);
        stValueMAP.put(ECG_ST_V6, 0.0f);
    }

    /**
     * 判断导联类型 0： 3导联 1:5导联 2：12导联
     */
    private int judgeLead() {
        int id = (int) DPUtils.getSelectValueBySortAttrId(getContext(), 110101);
        return id;
    }

    private void updateUpValue() {
        upValue = DPUtils.getFloatValueBySortAttrId(getContext(), 110208);
//        tvUpValue.setText(upValue + "");

        tvUpValue.setText(DateUtil.floatToStr(upValue));
    }

    private void updateDownValue() {
        downValue = DPUtils.getFloatValueBySortAttrId(getContext(), 110209);
//        tvDownValue.setText(downValue + "");
        tvDownValue.setText(DateUtil.floatToStr(downValue));
    }

    private void updateOnOff() {
        onoff = DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant.ST_ONOFF)
                > 0;
    }

    Handler mHandler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tvUpValue.setSelected(isUp && !flag);
            tvDownValue.setSelected(isDown && !flag);

            tvIValue.setSelected(isI && !flag);
            tvIIValue.setSelected(isII && !flag);
            tvIIIValue.setSelected(isIII && !flag);
            tvAvfValue.setSelected(isAVF && !flag);
            tvAvlValue.setSelected(isAVL && !flag);
            tvAvrValue.setSelected(isAVR && !flag);
            tvV1Value.setSelected(isV1 && !flag);
            tvV2Value.setSelected(isV2 && !flag);
            tvV3Value.setSelected(isV3 && !flag);
            tvV4Value.setSelected(isV4 && !flag);
            tvV5Value.setSelected(isV5 && !flag);
            tvV6Value.setSelected(isV6 && !flag);

            flag = !flag;

            if (isTwinkle) {
                mHandler.postDelayed(this, 500);
            }
        }
    };
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            tvValue3Lead.setSelected(is3Lead && !flag);
            flag = !flag;
            if (isTwinkle) {
                mHandler.postDelayed(this, 500);
            }
        }
    };
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ST_UP).equals(intent
                    .getAction())) {
                updateUpValue();
            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ST_DOWN).equals(intent
                    .getAction())) {
                updateDownValue();
            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ST_ONOFF).equals
                    (intent.getAction())) {
                updateOnOff();
            }
//            else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.PACE_MAKING).equals
//                    (intent.getAction())) {
//                if (getPaceOnOff()) {
//                    //起搏开启，不显示std值
//                    showNoValue();
//                }
//            }
        }
    };

    public void setV2ToV6Visibility(int visibility) {
        llV.setVisibility(visibility);
        llV2.setVisibility(visibility);
    }

    /**
     * 设置3导联时的St显示
     * @param flag 设置3导联
     */
    public void setStTo3Lead(boolean flag) {
        if (flag) {
            rl3Lead.setVisibility(VISIBLE);
            rl5Or12Lead.setVisibility(GONE);
        } else {
            rl3Lead.setVisibility(GONE);
            rl5Or12Lead.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onTrendData(AIDLServer aidlServer) {
        int[] types = new int[]{ECG_ST_I, ECG_ST_II, ECG_ST_III,
                ECG_ST_AVF, ECG_ST_AVL, ECG_ST_AVR,
                ECG_ST_V1, ECG_ST_V2, ECG_ST_V3,
                ECG_ST_V4, ECG_ST_V5, ECG_ST_V6};
        aidlServer.addSendTrendListeners(types, this);
    }

    @Override
    public void sendTrend(int param, int value) {
        if (getVisibility() != View.VISIBLE) {
            return;
        }
        //这里起搏开启，不显示值
        if (getPaceOnOff()) {
            return;
        }
        float v = value / 100f;
        if (value == -1000) {
            tvValue3Lead.setText("-?-");
            ivAlarm3Lead.setVisibility(GONE);
            mHandler.removeCallbacks(runnable1);
            tvValue3Lead.setSelected(false);
            showNoValue();
            mHandler.removeCallbacks(runnable);
            return;
        }
        switch (param) {
            case ECG_ST_I:
                tvValue3Lead.setText("-?-");
                tvIValue.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 0) { //判断导联类型
                        if (getLeadInt() == 0) {
                            tvValue3Lead.setText(String.valueOf(v));
                            is3Lead = judgeAbnormalValue(v) && DataUtils.isValid(tvValue3Lead,
                                    value);
                        }
                    } else {
                        tvIValue.setText(v + "");
                        stValueMAP.put(ECG_ST_I, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_I, 0.0f);
                }
                isI = judgeAbnormalValue(v) && DataUtils.isValid(tvIValue, value);
                break;
            case ECG_ST_II:
                tvValue3Lead.setText("-?-");
                tvIIValue.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 0) { //判断导联类型
                        if (getLeadInt() == 1) {
                            tvValue3Lead.setText(String.valueOf(v));
                            is3Lead = judgeAbnormalValue(v) && DataUtils.isValid(tvValue3Lead,
                                    value);
                        }
                    } else {
                        tvIIValue.setText(v + "");
                        stValueMAP.put(ECG_ST_II, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_II, 0.0f);
                }
                isII = judgeAbnormalValue(v) && DataUtils.isValid(tvIIValue, value);
                break;
            case ECG_ST_III:
//                tvIIIValue.setText(v + "");
                tvValue3Lead.setText("-?-");
                tvIIIValue.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 0) { //判断导联类型
                        if (getLeadInt() == 1) {
                            tvValue3Lead.setText(String.valueOf(v));
                            is3Lead = judgeAbnormalValue(v) && DataUtils.isValid(tvValue3Lead,
                                    value);
                        }
                    } else {
                        tvIIIValue.setText(v + "");
                        stValueMAP.put(ECG_ST_III, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_III, 0.0f);
                }
                isIII = judgeAbnormalValue(v) && DataUtils.isValid(tvIIIValue, value);
                break;
            case ECG_ST_AVF:
                tvAvfValue.setText("-?-");
                if (v != -10) {
                    if (judgeLead() != 0) { //判断导联类型
                        tvAvfValue.setText(v + "");
                        stValueMAP.put(ECG_ST_AVF, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_AVF, 0.0f);
                }
                isAVF = judgeAbnormalValue(v) && DataUtils.isValid(tvAvfValue, value);
                break;
            case ECG_ST_AVL:
                tvAvlValue.setText("-?-");
                if (v != -10) {
                    if (judgeLead() != 0) { //判断导联类型
                        tvAvlValue.setText(v + "");
                        stValueMAP.put(ECG_ST_AVL, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_AVL, 0.0f);
                }
                isAVL = judgeAbnormalValue(v) && DataUtils.isValid(tvAvlValue, value);
                break;
            case ECG_ST_AVR:
                tvAvrValue.setText("-?-");
                if (v != -10) {
                    if (judgeLead() != 0) { //判断导联类型
                        tvAvrValue.setText(v + "");
                        stValueMAP.put(ECG_ST_AVR, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_AVR, 0.0f);
                }
                isAVR = judgeAbnormalValue(v) && DataUtils.isValid(tvAvrValue, value);
                break;
            case ECG_ST_V1:
                tvV1Value.setText("-?-");
                if (v != -10) {
                    if (judgeLead() != 0) { //判断导联类型
                        tvV1Value.setText(v + "");
                        stValueMAP.put(ECG_ST_V1, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_V1, 0.0f);
                }
                isV1 = judgeAbnormalValue(v) && DataUtils.isValid(tvV1Value, value);
                break;
            case ECG_ST_V2:
                tvV2Value.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 2) { //判断导联类型
                        tvV2Value.setText(v + "");
                        stValueMAP.put(ECG_ST_V2, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_V2, 0.0f);
                }
                isV2 = judgeAbnormalValue(v) && DataUtils.isValid(tvV2Value, value);
                break;
            case ECG_ST_V3:
                tvV3Value.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 2) { //判断导联类型
                        tvV3Value.setText(v + "");
                        stValueMAP.put(ECG_ST_V3, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_V3, 0.0f);
                }
                isV3 = judgeAbnormalValue(v) && DataUtils.isValid(tvV3Value, value);
                break;
            case ECG_ST_V4:
                tvV4Value.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 2) { //判断导联类型
                        tvV4Value.setText(v + "");
                        stValueMAP.put(ECG_ST_V4, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_V4, 0.0f);
                }
                isV4 = judgeAbnormalValue(v) && DataUtils.isValid(tvV4Value, value);
                break;
            case ECG_ST_V5:
                tvV5Value.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 2) { //判断导联类型
                        tvV5Value.setText(v + "");
                        stValueMAP.put(ECG_ST_V5, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_V5, 0.0f);
                }
                isV5 = judgeAbnormalValue(v) && DataUtils.isValid(tvV5Value, value);
                break;
            case ECG_ST_V6:
                tvV6Value.setText("-?-");
                if (v != -10) {
                    if (judgeLead() == 2) { //判断导联类型
                        tvV6Value.setText(v + "");
                        stValueMAP.put(ECG_ST_V6, v);
                    }
                } else {
                    stValueMAP.put(ECG_ST_V6, 0.0f);
                }
                isV6 = judgeAbnormalValue(v) && DataUtils.isValid(tvV6Value, value);
                break;
        }
        if (onoff) {
            if (!isTwinkle) {
                isTwinkle = true;
                isUp = false;
                isDown = false;
                if (judgeLead() == 0) {
                    if (is3Lead) {
                        String toString = tvValue3Lead.getText().toString();
                        Float aFloat = Float.valueOf(toString.equals(getContext().getString(R
                                .string.no_pvcs)) ? "0" : toString);
                        if (judgeUpValue(aFloat)) {
                            isUp = true;
                        }
                        if (judgeDownValue(aFloat)) {
                            isDown = true;
                        }
                        mHandler.post(runnable1);
                        ivAlarm3Lead.setVisibility(VISIBLE);
                        sm.playHeightSound();
                    } else {
                        ivAlarm3Lead.setVisibility(GONE);

                        isTwinkle = false;
                    }
                } else if (judgeLead() == 1) {
                    if (isI || isII || isIII || isV1 || isAVR || isAVR || isAVF || isAVL) {
                        Float aFloat1 = stValueMAP.get(ECG_ST_I);
                        Float aFloat2 = stValueMAP.get(ECG_ST_II);
                        Float aFloat3 = stValueMAP.get(ECG_ST_III);
                        Float aFloat4 = stValueMAP.get(ECG_ST_AVR);
                        Float aFloat5 = stValueMAP.get(ECG_ST_AVL);
                        Float aFloat6 = stValueMAP.get(ECG_ST_AVF);
                        Float aFloat7 = stValueMAP.get(ECG_ST_V1);
                        if (judgeUpValue(aFloat1) || judgeUpValue(aFloat2) || judgeUpValue(
                                aFloat3) || judgeUpValue(aFloat4) || judgeUpValue(aFloat5) ||
                                judgeUpValue(aFloat6) || judgeUpValue(aFloat7)) {
                            isUp = true;
                        }
                        if (judgeDownValue(aFloat1) || judgeDownValue(aFloat2) || judgeDownValue
                                (aFloat3) || judgeDownValue(aFloat4) || judgeDownValue(aFloat5) ||
                                judgeDownValue(aFloat6) || judgeUpValue(aFloat7)) {
                            isDown = true;
                        }
                        mHandler.post(runnable);
                        ivAlarm.setVisibility(VISIBLE);
                        sm.playHeightSound();
                    } else {
                        isTwinkle = false;
                        ivAlarm.setVisibility(GONE);
                    }
                } else if (judgeLead() == 2) {
                    if (isI || isII || isIII || isV1 || isAVR || isAVR || isAVF || isAVL || isV2
                            || isV3 || isV4 || isV5 || isV6) {
                        for (Integer i : stValueMAP.keySet()) {
                            if (stValueMAP.get(i) > upValue) {
                                isUp = true;
                            }
                            if (stValueMAP.get(i) < downValue) {
                                isDown = true;
                            }
                        }
                        mHandler.post(runnable);
                        ivAlarm.setVisibility(VISIBLE);
                        sm.playHeightSound();
                    } else {
                        isTwinkle = false;
                        ivAlarm.setVisibility(GONE);
                    }
                }
            } else {
                isTwinkle = false;
            }
        } else {
            isTwinkle = false;
            ivAlarm.setVisibility(GONE);
            ivAlarm3Lead.setVisibility(GONE);
        }
    }

    /**
     * 判断是否为异常值
     * @param value
     */
    private boolean judgeAbnormalValue(float value) {
        if (value > upValue) {
            return true;
        } else if (value < downValue) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否低于下限值
     * @param value
     */
    private boolean judgeDownValue(float value) {
        if (value < downValue) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为超过上限值
     * @param value
     */
    private boolean judgeUpValue(float value) {
        if (value > upValue) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 起搏开启，不显示值
     */
    public void showNoValue() {
        tvIValue.setText(getContext().getString(R.string.no_pvcs));
        tvIValue.setSelected(false);
        tvIIValue.setText(getContext().getString(R.string.no_pvcs));
        tvIIValue.setSelected(false);
        tvIIIValue.setText(getContext().getString(R.string.no_pvcs));
        tvIIIValue.setSelected(false);
        tvAvfValue.setText(getContext().getString(R.string.no_pvcs));
        tvAvfValue.setSelected(false);
        tvAvlValue.setText(getContext().getString(R.string.no_pvcs));
        tvAvlValue.setSelected(false);
        tvAvrValue.setText(getContext().getString(R.string.no_pvcs));
        tvAvrValue.setSelected(false);
        tvV1Value.setText(getContext().getString(R.string.no_pvcs));
        tvV1Value.setSelected(false);
        tvV2Value.setText(getContext().getString(R.string.no_pvcs));
        tvV2Value.setSelected(false);
        tvV3Value.setText(getContext().getString(R.string.no_pvcs));
        tvV3Value.setSelected(false);
        tvV4Value.setText(getContext().getString(R.string.no_pvcs));
        tvV4Value.setSelected(false);
        tvV5Value.setText(getContext().getString(R.string.no_pvcs));
        tvV5Value.setSelected(false);
        tvV6Value.setText(getContext().getString(R.string.no_pvcs));
        tvV6Value.setSelected(false);
        tvValue3Lead.setText(getContext().getString(R.string.no_pvcs));
        tvValue3Lead.setSelected(false);
        ivAlarm.setVisibility(GONE);
        ivAlarm3Lead.setVisibility(GONE);
        tvUpValue.setSelected(false);
        tvDownValue.setSelected(false);
    }

    @Override
    public void setConfigIntentParams(Intent intent) {
        intent.putExtra(GlobalConstant.SORT_ID, 11);
    }

    public void isValid(TextView tv) {
        tv.setText("-?-");
    }

    /**
     * 得到起搏开关状态
     * @return 返回起搏开关状态
     */
    private boolean getPaceOnOff() {
//        int value = DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant.PACE_MAKING);
//        if (value == 2) {
//            //2代表起搏开启
//            return true;
//        }
        return false;
    }

    /**
     * 获取要计算的导联
     * @return 计算导联
     */
    public int getLeadInt() {
        int value = DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant.LEAD_COUNT);
        return value;
    }

    @Override
    public void removeWarnAndReset() {
        super.removeWarnAndReset();
        showNoValue();

    }
}
