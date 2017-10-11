package com.konsung.defineview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.konsung.R;
import com.konsung.util.DPUtils;
import com.konsung.util.FontManager;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengminghui on 15/9/12.
 * 首页心电展示view，包括心电波形（12个）和心电参数
 */
public class ECGView extends LinearLayout {
    private WaveForm_Ecg ecg_i;
    private WaveForm_Ecg ecg_ii;
    private WaveForm_Ecg ecg_iii;
//    private WaveForm_Ecg ecg_avf;
//    private WaveForm_Ecg ecg_avl;
//    private WaveForm_Ecg ecg_avr;
//    private WaveForm_Ecg ecg_v1;
//    private WaveForm_Ecg ecg_v2;
//    private WaveForm_Ecg ecg_v3;
//    private WaveForm_Ecg ecg_v4;
//    private WaveForm_Ecg ecg_v5;
//    private WaveForm_Ecg ecg_v6;

    private EcgDataView ecgDataView;
    private STDataView stDataView;
//    private TempDataView tempDataView;
//    private NibpDataView nibpDataView;

    private Map<Integer, WaveForm_Ecg> waveMap = new HashMap<>();

    public ECGView(Context context) {
        this(context, null);
    }

    public ECGView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ECGView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        FontManager.changeFonts(getContext(), this);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ecg_view, this);
        initWaveView();
        initDataView();
        setWave();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_DL);
        intentFilter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_ST);
        intentFilter.addAction(GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.LEAD_COUNT);
        intentFilter.addAction(GlobalConstant.ACTION_RESTART);
        getContext().registerReceiver(receiver, intentFilter);
    }

    private void initDataView() {
        ecgDataView = (EcgDataView) findViewById(R.id.ecg_data_view);
        stDataView = (STDataView) findViewById(R.id.st_data_view);
//        tempDataView = (TempDataView) findViewById(R.id.temp_data_view);
//        nibpDataView = (NibpDataView) findViewById(R.id.nibp_data_view);
        stDataView.initMapValue();
    }

    private void initWaveView() {
        ecg_i = (WaveForm_Ecg) findViewById(R.id.wave_i);
        ecg_ii = (WaveForm_Ecg) findViewById(R.id.wave_ii);
        ecg_iii = (WaveForm_Ecg) findViewById(R.id.wave_iii);
//        ecg_avr = (WaveForm_Ecg) findViewById(R.id.wave_AVR);
//        ecg_avl = (WaveForm_Ecg) findViewById(R.id.wave_AVL);
//        ecg_avf = (WaveForm_Ecg) findViewById(R.id.wave_AVF);
//        ecg_v1 = (WaveForm_Ecg) findViewById(R.id.wave_V1);
//        ecg_v2 = (WaveForm_Ecg) findViewById(R.id.wave_V2);
//        ecg_v3 = (WaveForm_Ecg) findViewById(R.id.wave_V3);
//        ecg_v4 = (WaveForm_Ecg) findViewById(R.id.wave_V4);
//        ecg_v5 = (WaveForm_Ecg) findViewById(R.id.wave_V5);
//        ecg_v6 = (WaveForm_Ecg) findViewById(R.id.wave_V6);

        ecg_i.setSampleRate(500);
        ecg_ii.setSampleRate(500);
        ecg_iii.setSampleRate(500);

        ecg_i.setTitle("I", KParamType.ECG_I);
        ecg_ii.setTitle("II", KParamType.ECG_II);
        ecg_iii.setTitle("III", KParamType.ECG_III);
//        ecg_avf.setTitle("AVF", KParamType.ECG_AVF);
//        ecg_avl.setTitle("AVL", KParamType.ECG_AVL);
//        ecg_avr.setTitle("AVR", KParamType.ECG_AVR);
//        ecg_v1.setTitle("V1", KParamType.ECG_V1);
//        ecg_v2.setTitle("V2", KParamType.ECG_V2);
//        ecg_v3.setTitle("V3", KParamType.ECG_V3);
//        ecg_v4.setTitle("V4", KParamType.ECG_V4);
//        ecg_v5.setTitle("V5", KParamType.ECG_V5);
//        ecg_v6.setTitle("V6", KParamType.ECG_V6);

        waveMap.put(KParamType.ECG_I, ecg_i);
        waveMap.put(KParamType.ECG_II, ecg_ii);
        waveMap.put(KParamType.ECG_III, ecg_iii);
//        waveMap.put(KParamType.ECG_AVF, ecg_avf);
//        waveMap.put(KParamType.ECG_AVL, ecg_avl);
//        waveMap.put(KParamType.ECG_AVR, ecg_avr);
//        waveMap.put(KParamType.ECG_V1, ecg_v1);
//        waveMap.put(KParamType.ECG_V2, ecg_v2);
//        waveMap.put(KParamType.ECG_V3, ecg_v3);
//        waveMap.put(KParamType.ECG_V4, ecg_v4);
//        waveMap.put(KParamType.ECG_V5, ecg_v5);
//        waveMap.put(KParamType.ECG_V6, ecg_v6);

//        reset();

    }

    /**
     * 所有波形重置
     */
    private void reset() {
        for (WaveForm_Ecg ecg : waveMap.values()) {
            ecg.reset();
        }
    }

    /**
     * 所有波形暂停
     */
    public void stop() {
        for (WaveForm_Ecg ecg : waveMap.values()) {
            ecg.stop();
        }
    }

    /**
     * 给对应波形设置数据
     * @param type 波形类型
     * @param data 数据
     */
    public void setData(int type, byte[] data) {
        WaveForm_Ecg wave = waveMap.get(type);
        if (wave != null) {
            wave.setData(data);
        }
    }

    /**
     * 根据设置的导联类型显示不同的波形
     * @param i
     */
    private void setShowWave(int i) {
        if (i == 0) { //三导联
            setWaveVisible(GONE);
            int id = (int) DPUtils.getSelectValueBySortAttrId(getContext(),
                    GlobalConstant.LEAD_COUNT);
            switch (id) {
                case 0:
                    ecg_i.setVisibility(View.VISIBLE);
                    ecg_i.reset();
                    break;
                case 1:
                    ecg_ii.setVisibility(View.VISIBLE);
                    ecg_ii.reset();
                    break;
                case 2:
                    ecg_iii.setVisibility(View.VISIBLE);
                    ecg_iii.reset();
                    break;
            }
            stDataView.setVisibility(View.GONE);
            ecgDataView.setVisibility(View.VISIBLE);
            stDataView.setStTo3Lead(true);
            stDataView.initMapValue();
            stDataView.showNoValue();
            setStViewtParams(80, 120); //调整控件高度 默认高度160
            setSTDataView();
            getContext().sendBroadcast(new Intent(GlobalConstant.ACTION_SHOW_TEMP_NIBP));
        } else if (i == 1) { //五导联
            setWaveVisible(GONE);
            ecg_i.setVisibility(View.VISIBLE);
            ecg_ii.setVisibility(View.VISIBLE);
            ecg_iii.setVisibility(View.GONE);
            ecg_i.reset();
            ecg_ii.reset();
//            setDataViewVisible(GONE);
            ecgDataView.setVisibility(View.VISIBLE);
//            tempDataView.setVisibility(View.VISIBLE);
//            nibpDataView.setVisibility(View.INVISIBLE);
            stDataView.setStTo3Lead(false);
            stDataView.initMapValue();
            stDataView.showNoValue();
            setSTDataView();
            stDataView.setV2ToV6Visibility(View.GONE);
            setStViewtParams(160, 160);
            Intent intent = new Intent(GlobalConstant.ACTION_SHOW_TEMP_NIBP);
//            if (stDataView.getVisibility() != VISIBLE) {
//                //心电的ST段是否开启 在5导联的时候 会要调整位置
////                tempDataView.setVisibility(View.VISIBLE);
//                intent.putExtra(GlobalConstant.TYPE_SHOW, Params.SHOW_NIBP);
//            }
            getContext().sendBroadcast(intent);
        }
        //屏蔽12导联
//        else { //12导联
//            setWaveVisible(VISIBLE);
//            setDataViewVisible(VISIBLE);
//            stDataView.setStTo3Lead(false);
//            stDataView.initMapValue();
//            stDataView.showNoValue();
//            setStViewtParams(160, 160);
//            setSTDataView();
//            stDataView.setV2ToV6Visibility(View.VISIBLE);
//            getContext().sendBroadcast(new Intent(GlobalConstant.ACTION_DISMISS_TEMP_NIBP));
//            reset();
//        }
    }

    /**
     * 设置3导联时控件的高度
     * @param stInt stDataView高度
     * @param ecgInt ecgDtaView 高度
     */
    private void setStViewtParams(int stInt, int ecgInt) {
        ViewGroup.LayoutParams layoutParams = stDataView.getLayoutParams();
        layoutParams.height = stInt;
        stDataView.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams1 = ecgDataView.getLayoutParams();
        layoutParams1.height = ecgInt;
        ecgDataView.setLayoutParams(layoutParams1);
    }

    /**
     * 设置view的显示状态
     * @param visible 显示状态
     */
    private void setDataViewVisible(int visible) {
        ecgDataView.setVisibility(visible);
        stDataView.setVisibility(visible);
//        tempDataView.setVisibility(visible);
//        nibpDataView.setVisibility(visible);
    }

    /**
     * 设置波形的显示状态
     * @param visible 显示状态
     */
    private void setWaveVisible(int visible) {
        for (WaveForm_Ecg ecg : waveMap.values()) {
            ecg.setVisibility(visible);
            ecg.stop();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_DL).equals(intent.
                    getAction())) {
                setWave();
            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.ECG_ST).equals(intent
                    .getAction())) {
                setSTDataView();
            } else if (GlobalConstant.ACTION_RESTART.equals(intent.getAction())) {
                reset();
            } else if ((GlobalConstant.ACTION_UPDATE_DATA + GlobalConstant.LEAD_COUNT).equals(
                    intent.getAction())) {
                setWave();
            }
        }
    };

    /**
     * 设置波形的显示
     */
    private void setWave() {
        int id = (int) DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant.ECG_DL);
        setShowWave(id);
    }

    /**
     * 设置STD参数的显示
     */
    private void setSTDataView() {
        int value = DPUtils.getSelectValueBySortAttrId(getContext(), GlobalConstant.ECG_ST);
        if (value == 0) {
            stDataView.setVisibility(GONE);
        } else {
            stDataView.setVisibility(VISIBLE);
        }
    }
}
