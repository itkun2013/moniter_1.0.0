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
import com.konsung.util.DPUtils;
import com.konsung.util.DataUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.ThreadPool;
import com.konsung.util.UIUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by XuJunwei on 2015-04-20.
 * 呼吸波形
 */
public class WaveForm_resp
        extends View implements IWaveData {

    private Paint _paint = new Paint();//字体对应画笔
    private Paint paint_brokenLine;//波形图画笔
    private float[] _points;//描点
    private int _index = 0;
    int x = 0;
    private int _sampleRate = 500;
    private Handler _handler = new Handler();
    private LinkedList<Float> _wave;
    private LinkedList<Byte> _waveList;

    private float _factor;  // 200 / 256
    private boolean isDrawing = true;

    private String title = "RESP";
    private int speed;
    private float gain;
    private int width;
    private int height;
    private int halfHeight;
    private String strResp = "";
    private boolean finished = true; // 数据转换方法中的循环是否结束

    public WaveForm_resp(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint_brokenLine = new Paint();
        paint_brokenLine.setStrokeWidth(2);
        int color = getResources().getColor(R.color.resp_color);
        paint_brokenLine.setColor(color);
        paint_brokenLine.setAntiAlias(true);
        _paint.setAntiAlias(true);
        //设置波形图上面的字体颜色，大小
        _paint.setColor(color);
        _paint.setTextSize(20);
        _paint.setAntiAlias(true);
        _paint.setStrokeWidth(3);  //设置画出的线的 粗细程度

        _wave = new LinkedList<>();
        _waveList = new LinkedList<>();
        strResp = DPUtils.getSelectValueBySortAttrName(UIUtils.getContext(),
                130105) + "      " + DPUtils.getSelectValueBySortAttrName(
                UIUtils.getContext(), 130102);
        initSpeed();
        initGain();

        IntentFilter intentFilter = new IntentFilter(GlobalConstant.ACTION_UPDATE_DATA + 130104);
        intentFilter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 130105);
        intentFilter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 130102);
        intentFilter.addAction(GlobalConstant.ACTION_RESTART);
        context.registerReceiver(receiver, intentFilter);

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

    private Canvas mCanvas;

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        mCanvas.drawText(title + "      " + strResp, 10, 30, _paint);
        if (isDrawing && _points != null) {
            mCanvas.drawLines(_points, paint_brokenLine);
        }
        super.onDraw(mCanvas);
    }

    @Override
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
    @Override
    public void setData(byte[] data) {
        if (data == null || !isDrawing) {
            return;
        }
        //        Log.e("resp","size:"+data.length); 200
        if (getWidth() != 0 && _points == null) {
            init();
        }
        if (_points != null) {
            waveData = data;
            ThreadPool.execute(convertToList);
        }
    }

    private void init() {
        width = getWidth();
        height = getHeight();
        halfHeight = height / 2;
        _points = new float[width * 4 + 32];
        _factor = height / 256f;
    }

    //    @Override
    //    public void setTitle(String title, String name, int type) {
    //
    //    }

    @Override
    public void setTitle(String title, int type) {
        this.title = title;
    }

    /*
     * 更新界面函数
     */
    private Runnable _update = new Runnable() {
        @Override
        public void run() {
            if (getWidth() == 0) {
                _handler.postDelayed(this, 100);//200
                return;
            }
            if (_points == null) {
                init();
            }

            for (int i = 0; i < 5; i++) {
                if (_wave.size() < 4) {
                    _handler.postDelayed(this, 100);
                    return;
                }
                //
                _points[_index++] = x;
                _points[_index++] = _wave.get(0) == null
                        ? 0
                        : _wave.get(0);
                x += (speed);
//                Log.e("呼吸波速：","x="+x);
                _points[_index++] = x;
                _points[_index++] = _wave.get(1) == null
                        ? 0
                        : _wave.get(1);
//                Log.e("_wave.get(2)RESP", _wave.get(2)+"");
//                Log.e("_wave.get(1)RESP", _wave.get(1)+"");
//                Log.e("_wave.get(0)RESP", _wave.get(0)+"");
//                _wave.remove(0);
                _wave.remove(0);
                if (x >= (width)) {
                    _index = 0;
                    x = 0;
                    DataUtils.removeMoreData(_wave);
                }
            }
            for (int i = 0; i < 32; i += 2) {
                _points[_index + i] = x + i;
                _points[_index + i + 1] = -10;
            }
            postInvalidate();
            _handler.postDelayed(this, DataUtils.TIME);
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
            if (waveData != null || _waveList.size() > 4) {
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
                    //_waveList.get(0) & 0xFF) 同为1则为1，否则0，同0为0
                    v1 = height - (_factor * ((_waveList.get(0) != null ? _waveList.get(0) : 0) &
                            0xFF));

                    v2 = height - (_factor * ((_waveList.get(2) != null ? _waveList.get(2) : 0) &
                            0xFF));
                    _wave.add(v1);
                    _wave.add(v2);
                    //这里易出现角标越界
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

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((GlobalConstant.ACTION_UPDATE_DATA + 130102).equals(intent.getAction()) ||
                    (GlobalConstant.ACTION_UPDATE_DATA + 130105).equals(
                    intent.getAction())) {
                strResp = DPUtils.getSelectValueBySortAttrName(UIUtils.getContext(),
                        130105) + "      " + DPUtils.getSelectValueBySortAttrName(
                        UIUtils.getContext(), 130102);
                invalidate();
            }
            if ((GlobalConstant.ACTION_UPDATE_DATA + 130105).equals(intent.getAction())) {
                initGain();
            } else if ((GlobalConstant.ACTION_UPDATE_DATA + 130104).equals(intent.getAction())) {
                initSpeed();
            }
            reset();
        }
    };

    private void initGain() {
        List<SortAttr> attrs = DBManager.getConfigDBHelper(getContext())
                .getRuntimeExceptionDao(SortAttr.class)
                .queryForEq("AttrID", 130105);
        if (attrs != null && attrs.size() > 0) {
            SortAttr attr = attrs.get(0);
            List<DictAttr> dictAttrs = DBManager.getConfigDBHelper(getContext())
                    .getRuntimeExceptionDao(DictAttr.class)
                    .queryForEq("DictAttrID", attr.getAttrValue());
            if (dictAttrs != null && dictAttrs.size() > 0) {
                gain = Float.valueOf(dictAttrs.get(0)
                        .getDictAttrValue());
            }
        }
    }

    private void initSpeed() {
        List<SortAttr> attrs = DBManager.getConfigDBHelper(getContext())
                .getRuntimeExceptionDao(SortAttr.class)
                .queryForEq("AttrID", 130104);
        if (attrs != null && attrs.size() > 0) {
            SortAttr attr = attrs.get(0);
            List<DictAttr> dictAttrs = DBManager.getConfigDBHelper(getContext())
                    .getRuntimeExceptionDao(DictAttr.class)
                    .queryForEq("DictAttrID", attr.getAttrValue());
            if (dictAttrs != null && dictAttrs.size() > 0) {
                speed = Integer.valueOf(dictAttrs.get(0)
                        .getDictAttrValue());
            }
        }
    }
}
