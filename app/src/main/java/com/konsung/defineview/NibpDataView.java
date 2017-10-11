package com.konsung.defineview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.service.AIDLServer;
import com.konsung.util.DPUtils;
import com.konsung.util.DataUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;

import java.text.SimpleDateFormat;

/**
 * Created by chengminghui on 15/9/4.
 * 血压参数view
 */
public class NibpDataView extends BaseDataView implements AIDLServer.SendTrend, AIDLServer
        .SendConfig {

    public static boolean NIBP_MEASURE_ING = false; //是否正在测量
    public static boolean NIBP_MEASURE_START = true; //是否测量
    public static boolean NIBP_MEASURE_AUTO = false; //是否自动测量
    public static boolean NIBP_IS_MEASURE = false; //监测病人是否做过测量
    private TextView tvUpValue;
    private TextView tvDownValue;
    private TextView tvValue1;
    private TextView tvValue2;
    private TextView tvValue3;
    private TextView tvMValue;
    private TextView tvNimp;
    private TextView tvTime;
    private LinearLayout linControll; //袖带压
    private TextView tvhint;

    private ImageView ivAlarm;

    private int v1Up;
    private int v1Down;
    private int v2Up;
    private int v2Down;
    private int v3Up;
    private int v3Down;

    private boolean isV1Up;
    private boolean isV1Down;
    private boolean isV2Up;
    private boolean isV2Down;
    private boolean isV3Up;
    private boolean isV3Down;

    private boolean isTwinkle;
    private boolean flag;

    private boolean onOff; //血压报警是否开启
    private static int interval;

    private int nimp = 0;
    private boolean viewDetachChange = false;
    static int timeNIBPStart35, timeNIBPStart12;
    NIBPCountTask nibpTask;

    //    private static boolean isStart = false; //主要用于切换导联导致的界面重绘制 袖带压力消失
    private static int mBloodH, mBloodL, mBloodV, mPulse; //切换导联恢复数据

    public NibpDataView(Context context) {
        this(context, null);
    }

    public NibpDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NibpDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140103);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140104);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140101);
        //注册血压开关监听
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140201);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140202);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140203);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140204);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140205);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140206);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140207);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140102); //测量间隔
        filter.addAction(GlobalConstant.ACTION_RESTART); //监测appdevices是否关闭
        filter.addAction(GlobalConstant.ACTION_SWITCH_pATIENT); //切换病人
        getContext().registerReceiver(receiver, filter);

        //切换导联类型
        IntentFilter filter12Link = new IntentFilter();
        filter12Link.addAction(GlobalConstant.ACTION_SHOW_TEMP_NIBP);
        filter12Link.addAction(GlobalConstant.ACTION_DISMISS_TEMP_NIBP);
        getContext().registerReceiver(receiver12Link, filter12Link);
    }

    @Override
    public void init() {
        super.init();
        inflate(getContext(), R.layout.nibp_data_view, this);
        tvUpValue = (TextView) findViewById(R.id.up_value);
        tvDownValue = (TextView) findViewById(R.id.down_value);
        tvValue1 = (TextView) findViewById(R.id.value_1);
        tvValue2 = (TextView) findViewById(R.id.value_2);
        tvValue3 = (TextView) findViewById(R.id.value_3);
        tvMValue = (TextView) findViewById(R.id.mailv_value);
        tvTime = (TextView) findViewById(R.id.time);
        tvhint = (TextView) findViewById(R.id.hint);
        ivAlarm = (ImageView) findViewById(R.id.alarm);
        tvNimp = (TextView) findViewById(R.id.nimp_tv);
        linControll = (LinearLayout) findViewById(R.id.nimp_controll);
        updateData();
        reset();
        int dmmd = DPUtils.getSelectValueBySortAttrId(getContext(), 140101);
        if (dmmd > 0) {
            timeNIBPStart35 = timeNIBPStart12 = dmmd;
        }
    }

    private void updateData() {
        v1Up = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140202);
        v1Down = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140203);
        v2Up = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140204);
        v2Down = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140205);
        v3Up = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140206);
        v3Down = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140207);
        //血压报警开关关 0，开 1，判断是否开关
        onOff = DPUtils.getSelectValueBySortAttrId(getContext(), 140201) > 0;

        tvUpValue.setText(v1Up + "");
        tvDownValue.setText(v1Down + "");

        measure = DPUtils.getSelectValueBySortAttrId(getContext(), 140101);
        timeNIBPStart12 = timeNIBPStart35 = measure;
        //测量间隔
        interval = DPUtils.getSelectValueBySortAttrId(getContext(), 140102);
        if (measure == 0) {
            NIBP_MEASURE_AUTO = false;
        } else {
            if (interval > 0) {
                NIBP_MEASURE_AUTO = true;
            } else {
                NIBP_MEASURE_AUTO = false;
            }
        }
    }

    private void reset() {
        //重置数据
        offWarn();
        tvValue1.setText("-?-");
        tvValue2.setText("-?-");
        tvValue3.setText("-?-");
        tvMValue.setText("-?-");
        tvNimp.setText("0");
        tvTime.setText("--:--:--");
        NIBP_MEASURE_START = false;
        NIBP_MEASURE_ING = false;
        NIBP_IS_MEASURE = false;
        if (NIBP_MEASURE_ING) {
            linControll.setVisibility(VISIBLE);
        } else {
            linControll.setVisibility(GONE);
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GlobalConstant.ACTION_UPDATE_DATA + 140103)) {
                //启动测量
                startMeasure();
            } else if (intent.getAction().equals(GlobalConstant.ACTION_UPDATE_DATA + 140104) ||
                    //停止
                    intent.getAction().equals(GlobalConstant.ACTION_UPDATE_DATA + 140101)) {
                //测量模式
                stopMeasure();
                reset();
            } else if (intent.getAction().equals(GlobalConstant.ACTION_UPDATE_DATA + 140201)) {
                //血压开关 - 是否打开警报
                if (NIBP_MEASURE_ING) {
                    //正在测量，结果没出来，不显示警报
                } else {
                    updateData();
                    if (!onOff) {
                        //如果是警报未开，关闭警报
                        offWarn();
                    } else {
                        if ((isV1Up || isV1Down || isV2Up || isV2Down || isV3Up || isV3Down) &&
                                onOff) {
                            if (!isTwinkle) {
                                isTwinkle = true;
                                mHandler.post(twinkleRun);
                                ivAlarm.setVisibility(VISIBLE);
                            }
                        } else {
                            isTwinkle = false;
                            ivAlarm.setVisibility(GONE);
                        }
                    }
                }
            } else if (intent.getAction().equals(GlobalConstant.ACTION_UPDATE_DATA + 140102)) {
                //测量间隔更改
                updateData();
            } else if (intent.getAction().equals(GlobalConstant.ACTION_SWITCH_pATIENT)) {
                //测量间隔更改
                switchPaitent();
            } else {
                updateDataBoardCast();

                if ((isV1Up || isV1Down || isV2Up || isV2Down || isV3Up || isV3Down) && onOff) {
                    if (!isTwinkle) {
                        //如果没有报警
                        isTwinkle = true;
                        mHandler.postDelayed(twinkleRun, 500);
                        ivAlarm.setVisibility(VISIBLE);
                    } else {
                        //如果正在报警,先清除报警任务，在重新跑任务
                        mHandler.removeCallbacks(twinkleRun);
                        mHandler.postDelayed(twinkleRun, 500);
                    }
                } else {
                    isTwinkle = false;
                    ivAlarm.setVisibility(GONE);
                    tvUpValue.setSelected(false);
                    tvDownValue.setSelected(false);
                }
            }
        }
    };

    /**
     * 更改上下限重新获取数据
     */
    private void updateDataBoardCast() {
        //数据的改变
        v1Up = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140202);
        v1Down = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140203);
        v2Up = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140204);
        v2Down = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140205);
        v3Up = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140206);
        v3Down = (int) DPUtils.getFloatValueBySortAttrId(getContext(), 140207);
        //默认显示舒张压
        setUpAndDwonValue(v1Up, v1Down);
        //血压报警开关关 0，开 1，判断是否开关
        onOff = DPUtils.getSelectValueBySortAttrId(getContext(), 140201) > 0;
        //判断是否设置超限
        if (mBloodH != 0) {
            isV1Up = setIsUp(mBloodH, v1Up, tvValue1);
            isV1Down = setIsDown(mBloodH, v1Down, tvValue1);
        }
        if (mBloodL != 0) {
            isV2Up = setIsUp(mBloodL, v2Up, tvValue2);
            isV2Down = setIsDown(mBloodL, v2Down, tvValue2);
        }

        if (mBloodV != 0) {
            isV3Up = setIsUp(mBloodV, v3Up, tvValue3);
            isV3Down = setIsDown(mBloodV, v3Down, tvValue3);
        }
    }

    /**
     * 判断修改数字后，是否超上限
     * @param value 当前测量的值
     * @param limit 上下限值
     * @param tvValue 显示控件
     * @return boolean
     */
    private boolean setIsUp(int value, int limit, TextView tvValue) {
        boolean isOver = value > limit && DataUtils.isValid(tvValue, value);
        return isOver;
    }

    /**
     * 判断修改数字后，是否超下限
     * @param value 当前测量的值
     * @param limit 上下限值
     * @param tvValue 显示控件
     * @return boolean
     */
    private boolean setIsDown(int value, int limit, TextView tvValue) {
        boolean isOver = value < limit && DataUtils.isValid(tvValue, value);
        return isOver;
    }

    /**
     * 关闭血压开关
     */
    public void offWarn() {
        //TODO
        //重置数据  考虑切换病人的时候进行复用 重新测量之前要警告关闭
//        mHandler.removeCallbacks(twinkleRun, 500);

        ivAlarm.setVisibility(GONE);
        //正在报警
        //关闭声音
        //关闭闪烁
        tvValue1.setSelected(false);
        tvValue2.setSelected(false);
        tvValue3.setSelected(false);
        tvUpValue.setSelected(false);
        tvDownValue.setSelected(false);
        ivAlarm.setVisibility(GONE);
        onOff = false; //手动设置为false  不影响下次启动,测量完之后还会重置为最新测量结果
//        if(twinkleRun != null){
//            mHandler.removeCallbacks(twinkleRun);
//        }
        //停止线程
    }

    /**
     * 停止测量
     */
    private void stopMeasure() {
        linControll.setVisibility(View.GONE);
        NIBP_MEASURE_START = false;
        NIBP_MEASURE_ING = false;

        mHandler.removeCallbacks(runnable);
        //发送停止测量血压的命令
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);

        if (nibpTask != null) {
            nibpTask.onFinish();
            nibpTask.cancel();
        }
    }

    int measure = 0;

    /**
     * 启动测量
     */
    private void startMeasure() {

        //关闭警报
        offWarn();
        //重置数据
        reset();
        //测量模式 自动（0）还是手动(1)
        measure = DPUtils.getSelectValueBySortAttrId(getContext(), 140101);
        //测量间隔
        interval = DPUtils.getSelectValueBySortAttrId(getContext(), 140102);
        //手动测量
        if (measure == 0) {
            NIBP_MEASURE_AUTO = false;
        } else {
            //自动测量,如果测量间距>0
            if (interval > 0) {
                NIBP_MEASURE_AUTO = true;
            }
        }
        mHandler.post(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //这里要把数据重置 因为有自动测量 直接走的runnable
            NIBP_MEASURE_ING = true;
            NIBP_MEASURE_START = true;
            //偶发.启动测量没反应，需要发送2次（还是会偶发）
            EchoServerEncoder.setNibpConfig((short) 0x05, 0);
            //子线程不更新ui
            uiHandler.sendEmptyMessageDelayed(0x890, 500);
//            if (NIBP_MEASURE_AUTO) {
//                if (!MyApplication.appDeviceIsConnect) {
////                    NIBP_MEASURE_AUTO = false;
//                    NIBP_MEASURE_START = false;
//                    NIBP_MEASURE_ING = false;
//                    linControll.setVisibility(View.GONE);
//                    offWarn();
//                    reset();
//                    ToastUtils.toastContent(getContext(), getContext().getString(R.string
//                            .nibp_start_fail));
//                    return;
//                }
//////                mHandler.postDelayed(this, interval*1000);
//            }
        }
    };

    private Handler mHandler = new Handler();
    private Handler uiHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0x892:
                    //如果没有正在测量  没有停止
                    if (NIBP_MEASURE_ING) {
                        //正在测量
                        linControll.setVisibility(View.VISIBLE);
                        tvTime.setText("--:--:--");
                    } else {
                        //当前没有正在测量
                        linControll.setVisibility(View.GONE);
                        if (NIBP_MEASURE_START) {
                            //没有停止 继续运行
                            if (NIBP_MEASURE_AUTO) {
                                //自动测量
//                                uiHandler.sendEmptyMessageDelayed(0x893, 1000);
//                                uiHandler.sendEmptyMessageDelayed(0x892, 1000);
                                linControll.setVisibility(GONE);

                                if (nibpTask != null) {
                                    nibpTask.cancel();
                                    nibpTask.onFinish();
                                    nibpTask = null;
                                }
                                nibpTask = new NIBPCountTask(timeNIBPStart35 * 1000 * 60, 1000);
                                nibpTask.start();
                            } else {
                                //没有停止 不需要自动测量
                            }
                        } else {
                            //停止了测量系统
                        }
                    }

                    break;

                case 0x890:
                    if (nibpTask != null) {
                        nibpTask.cancel();
                        nibpTask = null;
                    }
                    timeNIBPStart35 = timeNIBPStart12 = 0;
                    linControll.setVisibility(VISIBLE);
//                    reset();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 测量完成后显示数据，报警的任务
     */
    Runnable twinkleRun = new Runnable() {
        @Override
        public void run() {
            //如果是收缩压超标，显示收缩压
            if (isV1Up || isV1Down) {
                setUpAndDwonValue(v1Up, v1Down);
                tvUpValue.setSelected((isV1Up) && flag);
                tvDownValue.setSelected((isV1Down) && flag);
            } else {
                //如果收缩压没超，显示舒张压
                if (isV2Up || isV2Down) {
                    setUpAndDwonValue(v2Up, v2Down);
                    tvUpValue.setSelected((isV2Up) && flag);
                    tvDownValue.setSelected((isV2Down) && flag);
                } else {
                    //如果舒张压没超，显示平均压
                    if (isV3Up || isV3Down) {
                        //如果是平均呀超标，显示
                        setUpAndDwonValue(v3Up, v3Down);
                        tvUpValue.setSelected((isV3Up) && flag);
                        tvDownValue.setSelected((isV3Down) && flag);
                    }
                }
            }

            if ((isV1Up || isV2Up || isV3Up)) {
                //如果是上限超了，报警
                if (onOff) {
                    ivAlarm.setVisibility(VISIBLE);
                    ivAlarm.setImageResource(R.drawable.alarm_high);
                    sm.playLowSound(); //高音警报
                }
            }
            if ((isV1Down || isV2Down || isV3Down)) {
                //如果是下限超了，报警
                if (onOff) {
                    ivAlarm.setVisibility(VISIBLE);
                    ivAlarm.setImageResource(R.drawable.alarm_low);
                    sm.playLowSound(); //低音警报
                }
            }

            tvValue1.setSelected((isV1Up || isV1Down) && flag);
            tvValue2.setSelected((isV2Up || isV2Down) && flag);
            tvValue3.setSelected((isV3Up || isV3Down) && flag);

            flag = !flag;
            if (isTwinkle) {
                mHandler.postDelayed(this, 500);
            }

            if (!onOff) {
                //警报被关闭
                tvUpValue.setText(v1Up + "");
                tvDownValue.setText(v1Down + "");
                tvValue1.setSelected(false);
                tvValue2.setSelected(false);
                tvValue3.setSelected(false);
                tvUpValue.setSelected(false);
                tvDownValue.setSelected(false);
            }

//            //显示袖带压
            if (NIBP_MEASURE_ING) {
//                ToastAssit.showT(getContext(), "runnable-show:linc");
                linControll.setVisibility(View.VISIBLE);
            } else {
//                ToastAssit.showT(getContext(), "runnable-gone:linc");
                linControll.setVisibility(View.GONE);
            }
        }
    };

    private void setUpAndDwonValue(float up, float down) {
        tvUpValue.setText(String.valueOf((int) up));
        tvDownValue.setText(String.valueOf((int) down));
    }

    @Override
    public void onTrendData(AIDLServer aidlServer) {
        //2017-2-13 17:00:04 axx 添加判空
        if (aidlServer != null) {
            aidlServer.addSendTrendListeners(new int[]{KParamType.NIBP_SYS,
                    KParamType.NIBP_DIA,
                    KParamType.NIBP_MAP,
                    KParamType.NIBP_PR}, this);
            aidlServer.addSendConfigListener(GlobalConstant.NET_NIBP_CONFIG, this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewDetachChange = true;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
//        Log.e("NibpDataView", "view从window改变");
        viewDetachChange = false;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (R.id.nibp_data_view == changedView.getId()) {
//            Log.e("abc-12", "vi:"+visibility+"=="+changedView.getId());
            if (VISIBLE == visibility && viewDetachChange) {
                onTrendData(aidlServer);
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void sendTrend(int param, int value) {
        int v = value / GlobalConstant.TREND_FACTOR;
        linControll.setVisibility(GONE);
        //正在测量false
        NIBP_MEASURE_ING = false;
        switch (param) {
            case KParamType.NIBP_SYS:
//                WriterLog.saveLog("bH:"+v);
                tvValue1.setText(v + "");
                mBloodH = v;
                isV1Up = v > v1Up && DataUtils.isValid(tvValue1, value);
                isV1Down = v < v1Down && DataUtils.isValid(tvValue1, value);
//                WriterLog.saveLog("bH:"+v +"-v1Up:"+v1Up+"-v1Down:"+v1Down);
                break;
            case KParamType.NIBP_DIA:
                if (DataUtils.isValid(tvValue2, value)) {
                    tvValue2.setText(v + "");
                    mBloodL = v;
                    isV2Up = v > v2Up && DataUtils.isValid(tvValue2, value);
                    isV2Down = v < v2Down && DataUtils.isValid(tvValue2, value);
//                    WriterLog.saveLog("bL:"+v +"-v2Up:"+v2Up+"-v2Down:"+v2Down);
                }
                break;
            case KParamType.NIBP_MAP:
                tvValue3.setText(v + "");
                mBloodV = v;
                isV3Up = v > v3Up && DataUtils.isValid(tvValue3, value);
                isV3Down = v < v3Down && DataUtils.isValid(tvValue3, value);
                break;
            case KParamType.NIBP_PR:
                if (DataUtils.isValid(tvMValue, value)) {
                    tvMValue.setText(v + "");
                    mPulse = v;
                    //测试血压最后一次都会走504_脉率，标记赋值
                    NIBP_MEASURE_START = true;
                    //监测到这里，证明有病人测量了
                    NIBP_IS_MEASURE = true;
                    //是否开启自动测试
                    autoTaskStart();
                }
                break;
        }

        //测量完之后 马上获取开关值
        onOff = DPUtils.getSelectValueBySortAttrId(getContext(), 140201) > 0;

        if ((isV1Up || isV1Down || isV2Up || isV2Down || isV3Up || isV3Down) && onOff) {
            if (!isTwinkle) {
                isTwinkle = true;
                mHandler.post(twinkleRun);
                ivAlarm.setVisibility(VISIBLE);
            }
        } else {
            isTwinkle = false;
            ivAlarm.setVisibility(GONE);
        }
        showTime();
    }

    /**
     * 显示血压测量时间
     */
    public void showTime() {
        //测量完之后,显示测量数据
        if (NIBP_MEASURE_START) {
            //如果是自动测量，不能显示当前系统时间
            if (!NIBP_MEASURE_AUTO) {
                //初始化Formatter的转换格式。
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                long endTime = System.currentTimeMillis();
                String formatTime = formatter.format(endTime);
                tvTime.setText(formatTime);
                //测量结束，为false
                NIBP_MEASURE_START = false;
            }
        }
    }

    /**
     * 自动测量任务
     */
    private void autoTaskStart() {
        //测量结束判断是否需要再次自动测量
        if (NIBP_MEASURE_START) {
            //没有停止测量系统
            if (NIBP_MEASURE_AUTO) {
                //是自动测量  需要再次启动
                //1.延后启动测量系统
                mHandler.postDelayed(runnable, interval * 1000 * 60);
                //2.启动倒计时
                changeText();
            } else {
                //没有打开自动启动 不需要再次启动测量
                //已经停了测量系统
                NIBP_MEASURE_START = false;
            }
        }
    }

    private void changeText() {
//        Log.e("NibpDataView", "mea:"+ measure);
        //调用一次
        //自动测量才有
        timeNIBPStart35 = timeNIBPStart12 = interval;
//        Log.e("NibpDataView", "time:"+timeNIBPStart35+"---interval:"+interval);
        uiHandler.sendEmptyMessage(0x892);
    }

    @Override
    public void setConfigIntentParams(Intent intent) {
        intent.putExtra(GlobalConstant.SORT_ID, 14);
    }

    @Override
    public void sendConfig(int param, int value) {
//        Log.e("NibpDataView", "config值-" + param +"::"+value);
        if (param == 0x04) {
            nimp = value;
            //2017-2-17 11:09:05 添加:既然有值了,就表示在测量 可以显示了
            //不断的发送数据来
            tvNimp.setText(nimp + "");
        } else if (param == 0x07) {
            //时间的测试时间
//            startTime = System.currentTimeMillis();
            //开始测量了
            if (value == 1) {
                NIBP_MEASURE_START = true;
            }
        } else if (param == 0x02 && value != 0) {
//            Log.e("NibpDataView", "v:"+value);
//            tvValue1.setText("-?-");
//            tvValue2.setText("-?-");
//            tvValue3.setText("-?-");
//            tvMValue.setText("-?-");
//            PreferenceUtils.putBoolean(getContext(), NIBP_MEASURE_AUTO, false);
            NIBP_MEASURE_START = false;
            NIBP_MEASURE_ING = false;
            //TODO 重置数据 隐藏  自动测量计算时间
            stopMeasure();
//            reset();
//            ToastAssit.showT(getContext(), "血压测量发生问题,请检查设备重新测量");
        }

        //测量结束
//        if(param == 0x02){
//            ToastAssit.showT(getContext(), "param=0x02::测量结束");
//            linControll.setVisibility(GONE);
//        }
    }

    private BroadcastReceiver receiver12Link = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (GlobalConstant.ACTION_SHOW_TEMP_NIBP.equals(action) || GlobalConstant
                    .ACTION_DISMISS_TEMP_NIBP.equals(action)) {
                //切换到的是 3，5，12 导联的广播
//                switch35Link();
//                //是否做过血压测试
//                if (NIBP_IS_MEASURE) {
//                    //切换导联是否是自动测试，显示时间
//                    if (!NIBP_MEASURE_AUTO) {
//                        //初始化Formatter的转换格式。
//                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                        long endTime = System.currentTimeMillis();
//                        String formatTime = formatter.format(endTime);
//                        tvTime.setText(formatTime);
//                    }
//                }
            }
        }
    };

    private void switch35Link() {

        if (nibpTask != null) {
            nibpTask.cancel();
            nibpTask.onFinish();
            nibpTask = null;
        }
        //是否正在测量
        if (NIBP_MEASURE_ING) {
            linControll.setVisibility(VISIBLE);
            tvTime.setText("--:--:--");
        } else {
//                //是否自动测量
            if (NIBP_MEASURE_AUTO) {
                //自动测量  需要重新启动
                //需要计时
                linControll.setVisibility(GONE);
                //测量模式
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, interval * 1000 * 60);
                //2.启动倒计时
                changeText();
                //已停止  但是自动测量

//                    mHandler.removeCallbacksAndMessages(null);
//                    uiHandler.removeCallbacksAndMessages(null);

//                    uiHandler.sendEmptyMessage(0x890);
//                    timeNIBPStart35 = timeNIBPStart12;
//                    Log.e("NibpDataView", "35time:" + timeNIBPStart35);
//                    mHandler.postDelayed(runnable, timeNIBPStart35 * 1000);

//                    uiHandler.sendEmptyMessage(0x892);

//                    NIBP_MEASURE_START = false;
//                    NIBP_MEASURE_ING = false;
//                    reset();

            }
        }

        //更新之前的数据
//        updateData();
        if (mBloodH == 0 || mBloodL == 0 || mBloodV == 0 || mPulse == 0) {
        } else {
            tvValue1.setText(String.valueOf(mBloodH));
            tvValue2.setText(String.valueOf(mBloodL));
            tvValue3.setText(String.valueOf(mBloodV));
            tvMValue.setText(String.valueOf(mPulse));
        }
    }

    private void switch12Link() {
        if (nibpTask != null) {
            nibpTask.cancel();
            nibpTask.onFinish();
            nibpTask = null;
        }
        //是否正在测量
        if (NIBP_MEASURE_ING) {
            linControll.setVisibility(VISIBLE);
            tvTime.setText("--:--:--");
        } else {
            //当前没有测量

            //true: 没有停止测量系统
            //是否自动测量
            if (NIBP_MEASURE_AUTO) {
                //自动测量  需要重新启动
                //需要计时
                linControll.setVisibility(GONE);
                //测量模式

                //已停止  但是自动测量

//                    mHandler.removeCallbacksAndMessages(null);
//                    uiHandler.removeCallbacksAndMessages(null);

//                    uiHandler.sendEmptyMessage(0x890);
                timeNIBPStart12 = timeNIBPStart35;
//                    Log.e("abc-12", "12time:"+timeNIBPStart12);
//                    mHandler.postDelayed(runnable, timeNIBPStart12*1000);
                //虽然在计时 但是没有修改值
                uiHandler.sendEmptyMessage(0x892);
            }
        }

        //更新之前的数据
//        updateData();
        if (mBloodH == 0 || mBloodL == 0 || mBloodV == 0 || mPulse == 0) {

        } else {
            tvValue1.setText(String.valueOf(mBloodH));
            tvValue2.setText(String.valueOf(mBloodL));
            tvValue3.setText(String.valueOf(mBloodV));
            tvMValue.setText(String.valueOf(mPulse));
        }
    }

    public void switchPaitent() {
        stopMeasure();
        reset();
        stopAlarm();
        //切换病人，所有导联的数据全部归于0
        mBloodH = mBloodL = mBloodV = mPulse = 0;
    }

    /**
     * 停止警报
     */
    private void stopAlarm() {
        onOff = false;
        isTwinkle = false;
        mHandler.removeCallbacks(twinkleRun);
        isV1Up = isV2Up = isV3Up = false;
        isV1Down = isV2Down = isV3Down = false;
        ivAlarm.setVisibility(GONE);
        tvUpValue.setSelected(false);
        tvDownValue.setSelected(false);
        tvUpValue.setText(v1Up + "");
        tvDownValue.setText(v1Down + "");
        tvValue1.setSelected(false);
        tvValue2.setSelected(false);
        tvValue3.setSelected(false);
    }

    /**
     * 倒计时
     */
    class NIBPCountTask extends CountDownTimer {
        long sencondTime = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。

        public NIBPCountTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            sencondTime = millisInFuture / 1000;
        }

        @Override
        public void onTick(long l) {

            String time = formatter.format(sencondTime * 1000);
            //可能是东八区时差的原因，这里显示默认+08，做-8处理
            String tTime = formatter.format((sencondTime - 8 * 60 * 60) * 1000);
            tvTime.setText(tTime);
            sencondTime--;
        }

        @Override
        public void onFinish() {
            tvTime.setText("--:--:--");
            timeNIBPStart35 = interval;
            sencondTime = 0;
        }
    }
}
