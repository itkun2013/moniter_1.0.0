package com.konsung.defineview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.konsung.R;
import com.konsung.bean.DictAttr;
import com.konsung.bean.SortAttr;
import com.konsung.util.DBManager;
import com.konsung.util.GlobalConstant;
import com.konsung.util.ThreadPool;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by XuJunwei on 2015-04-20.
 * 血氧波形
 */
public class WaveForm_spo2 extends View implements IWaveData {

    private Paint _paint;
    private Paint paint_brokenLine;
    private float[] _points; //波形数集的点的集合
    private int _index = 0;
    int x = 0;
    private int _sampleRate = 500;
    private Handler _handler = new Handler();
    private List<Float> _wave;
    private LinkedList<Byte> _waveList;
    private float _factor = (float) 0.781;  // 200 / 256
    private boolean isDrawing = true;
    private static float COMMON_Y = (float) 81.875; //连接血氧设备之前，平滑直线，曲线Y坐标为固定值
    private String title = "SPO2";
    private int speed;
    private int width;
    private int height;
    private int halfHeight;
    private boolean isStop; //血氧脱落广播的标志
    private float lastY; //记录血氧脱落或者连接前最后一次的Y轴值
    private boolean isAccessDevice; //是否已经介入设备
    private boolean finished = true; // 数据转换方法中的循环是否结束

    public WaveForm_spo2(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint_brokenLine = new Paint();
        paint_brokenLine.setStrokeWidth(2);
        int color = getResources().getColor(R.color.spo_color);
        paint_brokenLine.setColor(color);
        paint_brokenLine.setAntiAlias(true);

        _paint = new Paint();
        _paint.setColor(color);
        _paint.setAntiAlias(true);

        //设置字体大小
        _paint.setTextSize(20);
        _paint.setAntiAlias(true);
        _paint.setStrokeWidth(3);  //设置画出的线的 粗细程度

        _wave = new LinkedList<>();
        _waveList = new LinkedList<>();

        initSpeed();

        IntentFilter intentFilter = new IntentFilter(GlobalConstant.ACTION_UPDATE_DATA + 120103);
        intentFilter.addAction(GlobalConstant.ACTION_RESTART);
        context.registerReceiver(receiver, intentFilter);

        IntentFilter filterStopWare = new IntentFilter("com.kongsung.spo2.stopware");
        context.registerReceiver(receiverStop, filterStopWare);

        //切换病人
        IntentFilter switchPatientFilter = new IntentFilter("com.kongsung.notify.switchpatient");
        getContext().registerReceiver(switchPatientReceiver, switchPatientFilter);
    }

    private BroadcastReceiver switchPatientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //切换病人
            reset();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(title, 10, 30, _paint);
//        WriterLog.saveLog("绘制文本_paint");
        if (isDrawing && _points != null) {
//            WriterLog.saveLog("绘制brokenLine");
            canvas.drawLines(_points, paint_brokenLine);
        }
        super.onDraw(canvas);
    }

    public void stop() {
        x = 0;
        _index = 0;
        isDrawing = false;
        _wave.clear();
        _waveList.clear();
        waveData = null;
        _handler.removeCallbacks(_update);
    }

    @Override
    public void reset() {
        stop();
        isDrawing = true;
        if (_points != null) {
            Arrays.fill(_points, 0);
        }
        _handler.post(_update);
    }

    /*
     * 往波形控件中添加数据
     */
    public void setData(byte[] data) {
        if (data == null || !isDrawing) {
            return;
        }

//        Log.e("spo2", "size:" + data.length); 150
        if (getWidth() != 0 && _points == null) {
            init();
//            WriterLog.saveLog("spo2波形init");
        }
        if (_points != null) {
            waveData = data;
            ThreadPool.execute(convertToList);
        } else {
//            WriterLog.saveLog("spo2波形 _points是null");
        }
    }

    private void init() {
        width = getWidth();
        height = getHeight();
        halfHeight = height / 2;
        _points = new float[width * 4 + 32];
        _factor = height / 256f;
    }

    @Override
    public void setTitle(String title, int type) {
        this.title = title;
    }

    public void setSampleRate(int sampleRate) {
        _points = new float[sampleRate * 40];
        _index = 0;
        _sampleRate = sampleRate;
        _handler.post(_update);
    }

    /*
     * 更新界面函数
     */
    private Runnable _update = new Runnable() {
        @Override
        public void run() {

            if (getWidth() == 0) {
                _handler.postDelayed(this, 100);
                return;
            }
            if (_points == null) {
                init();
            }

            for (int i = 0; i < 4; i++) {
                //wave波形值，长度最长为2，改为2
                if (_wave.size() < 2) {
                    _handler.postDelayed(this, 40);

                    return;
                }

                if (isStop) {
                    //赋值Y轴集的最后一次血氧脱落的值，第一位的值，便于将断开的线画出来
                    if (_wave.size() > 0) {
                        _wave.set(0, lastY);
                        isStop = false;
                    }
                } else if (isAccessDevice) {
                    //连接血氧设备，给第一位Y轴赋值，角标越界
                    if (_wave.size() > 0) {

                        _wave.set(0, lastY);
                        isAccessDevice = false;
                    }
                }
                _points[_index++] = x; //x轴
                //易出现角标越界
                if (_points.length - 1 > _index && _wave.size() > 0) {
                    _points[_index++] = _wave.get(0) == null ? 0 : _wave.get(0); //y轴
                }
                x += speed;
                if (_points.length - 1 > _index && _wave.size() > 1) {
                    _points[_index++] = x;
                    _points[_index++] = _wave.get(1) == null ? 0 : _wave.get(1);
                }

                //TODO 因为数据非同步的 其他线程在不断的新增 这里的remove 数据量很小没有作用
                //TODO 导致图像的延迟
                //TODO 数据积压导致波形延迟
                if (_wave.size() > 1) {
                    _wave.remove(0);        // SPO2波形占用两个字节，将最前面的两个字节移除
                    _wave.remove(0);
                }

                if (x >= getWidth()) {
                    _index = 0;
                    x = 0;
                    _wave.clear();
                }
            }
            for (int i = 0; i < 32; i += 2) {
                _points[_index + i] = x + i;
                _points[_index + i + 1] = -10;
            }

            postInvalidate();
            //缩短画线的时间
            _handler.post(this);
        }
    };

    /**
     * 当前一个线程数据转换循环为完成时，当前线程先睡0.1秒，再继续判断
     */
    private void delay() {
        if (!finished) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            delay();
        }
    }

    private byte[] waveData;
    private Runnable convertToList = new Runnable() {
        @Override
        public void run() {
            delay();
//            WriterLog.saveLog("convertToList线程run");
            if (waveData != null || _waveList.size() > 4) {

//                WriterLog.saveLog("convertToList线程__waveList.size>4");
                byte[] data = waveData;
                if (data != null) {
                    for (byte b : data) {
                        _waveList.add(b);
                    }
                }
                float v1;
                float v2;
                while (_waveList.size() > 4) {
                    finished = false;
                    //这里经常报空指针异常

                    v1 = height - (_factor * ((_waveList.get(0) != null ? _waveList.get(0) : 0) &
                            0xFF));

                    v2 = height - (_factor * ((_waveList.get(2) != null ? _waveList.get(2) : 0) &
                            0xFF));

                    _wave.add(v1);
                    _wave.add(v2);

                    float fOne = (_waveList.get(0) != null ? _waveList.get(0) : 0) & 0xFF;
                    float fTwo = (_waveList.get(2) != null ? _waveList.get(2) : 0) & 0xFF;
                    if (fOne != fTwo) {
                        mLoopValueDifferent++;
                    } else {
                        mLoopValueSame++;
//                        iLoopChange = 0;
                    }
                    if (mLoopValueDifferent == 4) {
                        //数据不同的次数有4次了  推断有测量数据了
                        //只删除一次
                        bJudgeMeasure = true;
                    }
                    if (mLoopValueSame > 30) {
                        bJudgeMeasure = false;
                        mLoopValueSame = 0;
                    }

                    if (bJudgeMeasure) {
                        clearWareBuffer();
                        bJudgeMeasure = false;
                    }
                    if (_waveList.size() > 1) {
                        _waveList.remove(0);
                        _waveList.remove(0);
                    }
                }
                finished = true;
                waveData = null;
            }
        }
    };

    private static int mLoopValueSame = 0;
    private static int mLoopValueDifferent = 0;
    private static boolean bJudgeMeasure = false;

    //通知清理数据
    private void clearWareBuffer() {
        //不能删除数据  只能通知波形改变  需要把之前的波形 _points 内容替换
        //获取当前波形的位置   删除_ware这个位置之前的数据
        //当接入血氧止夹，会走这里，设置连接状态，接入之前，记录Y坐标
        lastY = COMMON_Y;
        isAccessDevice = true;
        _wave.clear();

//        WriterLog.saveLog("数据清理:mLoopValueDifferent"+mLoopValueDifferent+"=mLoopValueDifferent
// :"+mLoopValueDifferent+"=_index:"+_index+"=_wave.size:"+_wave.size());
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reset();
            initSpeed();
        }
    };

    BroadcastReceiver receiverStop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bJudgeMeasure = false;
            mLoopValueDifferent = 0;
            isStop = true;
            //记录血氧脱落前最后一次的Y值，易角标越界
            if (_wave.size() > 0) {

                lastY = _wave.get(0);
            }
            _wave.clear();
//            WriterLog.saveLog("收到停止波形广播通知");
        }
    };

    private void initSpeed() {
        List<SortAttr> attrs = DBManager.getConfigDBHelper(getContext()).getRuntimeExceptionDao
                (SortAttr.class).queryForEq("AttrID", 120103);
        if (attrs != null && attrs.size() > 0) {
            SortAttr attr = attrs.get(0);
            List<DictAttr> dictAttrs = DBManager.getConfigDBHelper(getContext())
                    .getRuntimeExceptionDao(DictAttr.class).queryForEq("DictAttrID", attr
                            .getAttrValue());
            if (dictAttrs != null && dictAttrs.size() > 0) {
                speed = Integer.valueOf(dictAttrs.get(0).getDictAttrValue());
            }
        }
    }
}
