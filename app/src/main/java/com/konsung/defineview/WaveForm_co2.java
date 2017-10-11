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
import com.konsung.util.DataUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.ThreadPool;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by XuJunwei on 2015-04-20.
 * 二氧化碳波形view
 */
public class WaveForm_co2 extends View implements IWaveData{

    private Paint _paint;
    private Paint paint_brokenLine;
    private float[] _points;
    private int _index = 0;
    int x = 0;
    private int _sampleRate = 500;
    private Handler _handler = new Handler();
    private LinkedList<Float> _wave;
    private LinkedList<Byte> _waveList;


    private byte[] waveData;

    private float _factor ;  // 200 / 256
    private boolean isDrawing = true;

    private String title = "CO2";
    private int speed;
    private int width;
    private int height;
    private int halfHeight;

    public WaveForm_co2(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint_brokenLine = new Paint();
        paint_brokenLine.setStrokeWidth(2);
        int color = getResources().getColor(R.color.co2_color);
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

        IntentFilter intentFilter = new IntentFilter(GlobalConstant.ACTION_UPDATE_DATA+160108);
//        intentFilter.addAction(GlobalConstant.ACTION_RESTART);
        context.registerReceiver(receiver, intentFilter);

        //切换病人
        IntentFilter switchPatientFilter = new IntentFilter("com.kongsung.notify.switchpatient");
        getContext().registerReceiver(switchPatientReceiver, switchPatientFilter);
    }

    private BroadcastReceiver switchPatientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //切换病人 由需要的子类去完成
            reset();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(title, 10, 30, _paint);
        if (isDrawing && _points != null) {
            for(int i=0;i<_points.length;i++) {
            }
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

    public void reset() {
        stop();
        isDrawing = true;
        if(_points != null){
            Arrays.fill(_points,0);
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
//        Log.e("co2","size:"+data.length); 120
        if(getWidth() != 0 && _points == null){
            init();
        }

        if (_points != null){
            waveData = data;
            ThreadPool.execute(convertToList);
        }
    }

    private void init() {
        width = getWidth();
        height = getHeight();
        halfHeight = height/2;
        _points = new float[width*4+32];
        _factor = height / 256f;
    }

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
                if(getWidth() == 0){
                    _handler.postDelayed(this, 100);
                    return;
                }
                if(_points == null){
                    init();
                }
                for (int i = 0; i < 5; i++) {
                    if (_wave.size() < 4) {
                        _handler.postDelayed(this, 100);
                        return;
                    }
                    //
                    _points[_index++] = x;
                    _points[_index++] = _wave.get(0) == null ? 0 : _wave.get(0);
                    x+=speed;
                    _points[_index++] = x;
                    _points[_index++] = _wave.get(1) == null ? 0 : _wave.get(1);
//                    Log.e("_wave.get(1)CO2", _wave.get(1)+"");
                    _wave.remove(0);
                    _wave.remove(0);
                    if (x >= width) {
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
    private Runnable convertToList = new Runnable() {
        @Override
        public void run() {
            if ( waveData != null || _waveList.size() > 4) {
                byte[] data =  waveData;
                if(data != null){
                    for(byte b : data){
                        _waveList.add(b);
                    }
                }
                float v1 ;
                float v2 ;
                while (_waveList.size() > 4) {

                    v1 = halfHeight - (_factor * (_waveList.get(0) & 0xFF));
                    v2 = halfHeight - (_factor * (_waveList.get(1) & 0xFF));
                    _wave.add(v1);
                    _wave.add(v2);
                    _waveList.remove(0);
                }
                waveData = null;
            }

        }
    };


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initSpeed();
            reset();
        }
    };


    private void initSpeed(){
        List<SortAttr> attrs = DBManager.getConfigDBHelper(getContext()).getRuntimeExceptionDao(SortAttr.class).queryForEq("AttrID",160108);
        if(attrs != null && attrs.size() > 0){
            SortAttr attr = attrs.get(0);
            List<DictAttr> dictAttrs = DBManager.getConfigDBHelper(getContext()).getRuntimeExceptionDao(DictAttr.class).queryForEq("DictAttrID",attr.getAttrValue());
            if(dictAttrs != null && dictAttrs.size() > 0){
                speed = Integer.valueOf(dictAttrs.get(0).getDictAttrValue());
            }
        }
    }
}
