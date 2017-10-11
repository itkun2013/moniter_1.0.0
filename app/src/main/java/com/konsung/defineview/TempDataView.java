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
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;

/**
 * Created by chengminghui on 15/9/4.
 * 体温参数View
 */
public class TempDataView extends BaseDataView implements AIDLServer.SendTrend {
    private TextView tvT1UpValue;  //上限
    private TextView tvT1DownValue; //下限
    private TextView tvT1Value; //测量的值
    private ImageView ivT1Alarm; //报警

    private float t1UpValue; //字典设置的上限值
    private float t1downValue; ////字典设置的下限值

    private boolean ist1Up; //是否超上限
    private boolean ist1Down; //是否超下限

    private boolean flag; //标志
    private boolean isTwinkle; //是否闪烁
    private boolean onoff; //体温报警是否开启

    public TempDataView(Context context) {
        this(context, null);
    }

    public TempDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 150201);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 150202);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 150203);
        getContext().registerReceiver(receiver, filter);
    }

    public void init() {
        inflate(getContext(), R.layout.temp_data_view, this);
        tvT1UpValue = (TextView) findViewById(R.id.t1_up_value);
        tvT1DownValue = (TextView) findViewById(R.id.t1_down_value);
        tvT1Value = (TextView) findViewById(R.id.t1_value);
        ivT1Alarm = (ImageView) findViewById(R.id.t1_alarm);

        updateData();

        super.init();
    }

    private void updateData() {
        t1UpValue = DPUtils.getFloatValueBySortAttrId(getContext(), 150202);
        t1downValue = DPUtils.getFloatValueBySortAttrId(getContext(), 150203);
        onoff = DPUtils.getSelectValueBySortAttrId(getContext(), 150201) > 0;
        tvT1UpValue.setText(t1UpValue + "");
        tvT1DownValue.setText(t1downValue + "");
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
            //如果超限且报警开
            if ((ist1Down || ist1Up) && onoff) {
                if (!isTwinkle) {
                    isTwinkle = true;
                    mHandler.post(runnable);
                }
                startAudioService();
            } else {
                isTwinkle = false;
                ivT1Alarm.setVisibility(GONE);
            }
        }
    };

    Handler mHandler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //如果报警开了
            if (onoff) {
                if (ist1Up) {
                    ivT1Alarm.setVisibility(VISIBLE);
                    ivT1Alarm.setImageResource(R.drawable.alarm_high);
                } else if (ist1Down) {
                    ivT1Alarm.setVisibility(VISIBLE);
                    ivT1Alarm.setImageResource(R.drawable.alarm_low);
                }

                tvT1UpValue.setSelected(ist1Up && flag);
                tvT1DownValue.setSelected(ist1Down && flag);
                tvT1Value.setSelected((ist1Up || ist1Down) && flag);

                flag = !flag;
                if (isTwinkle) {
                    mHandler.postDelayed(this, 500);
                }
            } else {
                //如果报警关了
                tvT1UpValue.setSelected(false);
                tvT1DownValue.setSelected(false);
                tvT1Value.setSelected(false);
            }
        }
    };

    @Override
    public void onTrendData(AIDLServer aidlServer) {
        aidlServer.addSendTrendListeners(new int[]{KParamType.TEMP_T1,
                KParamType.TEMP_T2,
                KParamType.TEMP_TD}, this);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (changedView == null) {
            return;
        }
        if (visibility == VISIBLE) {
            if (aidlServer != null) {
                aidlServer.addSendTrendListeners(new int[]{KParamType.TEMP_T1,
                        KParamType.TEMP_T2,
                        KParamType.TEMP_TD}, this);
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void sendTrend(int param, int value) {
        float v = value / GlobalConstant.TEMP_FACTOR;

        if (param == KParamType.TEMP_T1) {
            if (v == -10.0f) {
                tvT1Value.setText("-?-");
            } else {
                tvT1Value.setText(v + "");
            }
            ist1Up = v > t1UpValue && DataUtils.isTempValid(tvT1Value, v);
            ist1Down = v < t1downValue && DataUtils.isTempValid(tvT1Value, v);
        }

        //体温报警是否开启
        onoff = DPUtils.getSelectValueBySortAttrId(getContext(), 150201) > 0;
        //如果超限且报警开
        if ((ist1Down || ist1Up) && onoff) {
            if (!isTwinkle) {
                isTwinkle = true;
                mHandler.post(runnable);
            }
            startAudioService();
        } else {
            isTwinkle = false;
            ivT1Alarm.setVisibility(GONE);
        }
    }

    @Override
    public void setConfigIntentParams(Intent intent) {
        intent.putExtra(GlobalConstant.SORT_ID, 15);
    }

    @Override
    public void removeWarnAndReset() {
        super.removeWarnAndReset();
        onoff = DPUtils.getSelectValueBySortAttrId(getContext(), 150201) > 0;


    }
}
