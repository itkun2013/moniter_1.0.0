package com.konusng.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsung.R;
import com.konsung.bean.Params;
import com.konsung.defineview.ECGView;
import com.konsung.defineview.IWaveData;
import com.konsung.defineview.NibpDataView;
import com.konsung.defineview.TempDataView;
import com.konsung.util.KParamType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengminghui on 15/8/30.
 * 首页recycler的adapter
 */
public class WaveRecyclerAdapter extends Adapter<ViewHolder> {
    private List<Params> paramses;
    private Map<Integer,ViewHolder> holderMap = new HashMap<>();

    public void setParamses(List<Params> paramses){
        this.paramses = paramses;
    }

    public List<Params> getParamses(){
        return paramses;
    }

    public void addParams(Params params){
        if(paramses != null){
            paramses.add(params);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        ViewHolder holder = null;
        //根据类型加载不同的view
        switch (viewType){
            case KParamType.ECG:
                View view = new ECGView(viewGroup.getContext());
                holder = new ECGViewHolder(view);
                break;
            case KParamType.SPO2_WAVE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wave_spo2,null);
                holder = new WaveViewHolder(view,viewType);
                break;
            case KParamType.RESP_WAVE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wave_resp,null);
                holder = new WaveViewHolder(view,viewType);
                break;
            //屏蔽CO2_WAVE
//            case KParamType.CO2_WAVE:
//                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wave_co2,null);
//                holder = new WaveViewHolder(view,viewType);
//                break;
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.temp_nibp_view,null);
                holder = new DefaultViewHolder(view);
                break;
        }
        holderMap.put(viewType,holder);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(viewHolder instanceof DefaultViewHolder){
            DefaultViewHolder holder = (DefaultViewHolder) viewHolder;
            Params params = paramses.get(i);
            if(params.getShowType() == Params.SHOW_NIBP){
                holder.tempDataView.setVisibility(View.INVISIBLE);
            }else{
                holder.tempDataView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(paramses == null){
            return 0;
        }
        return paramses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return paramses.get(position).getParaId();
    }

    /**
     * 给波形界面发送数据
     * @param type 类型
     * @param data 数据
     */
    public void setData(int type, byte[] data){

        View view = findViewByType(type);
        if(view == null){
            return;
        }
        if(view instanceof IWaveData){
            IWaveData waveData = (IWaveData) view;
            waveData.setData(data);
        }else if(view instanceof ECGView){
            ECGView ecgView = (ECGView) view;
            ecgView.setData(type,data);
        }

    }

    /**
     * 停止所有波形
     */
    public void destroy(){
        for(ViewHolder holder : holderMap.values()){
            if(holder instanceof WaveViewHolder){
                WaveViewHolder viewHolder = (WaveViewHolder) holder;
                if(viewHolder.wave instanceof  IWaveData){
                    ((IWaveData)viewHolder.wave).stop();
                }
            }else if(holder instanceof ECGViewHolder){
                ECGViewHolder ecgViewHolder = (ECGViewHolder) holder;
                ecgViewHolder.ecgView.stop();
            }
        }
    }

    /**
     * 通过类型找到对应view
     * @param type 类型
     * @return view
     */
    private View findViewByType(int type){
        if(type <= 12){
            type = 0;
//            Log.e("CMAD","---adapter type---");
        }
        ViewHolder holder = holderMap.get(type);
        if(holder instanceof WaveViewHolder){
            WaveViewHolder viewHolder = (WaveViewHolder) holder;
            return viewHolder.wave;
        }else if(holder instanceof ECGViewHolder){
            ECGViewHolder ecgViewHolder = (ECGViewHolder) holder;
            return  ecgViewHolder.ecgView;
        }
        return null;
    }

    /**
     * 根据id找到参数对象
     * @param paraId id
     * @return
     */
    private Params findParamsByParaId(int paraId){
        if(paramses == null){
            return null;
        }
        for(Params params : paramses){
            if(paraId == params.getParaId()){
                return params;
            }
        }
        return null;
    }

    private class WaveViewHolder extends ViewHolder{
        public View wave;
        public WaveViewHolder(View itemView,int type) {
            super(itemView);
            wave =  itemView.findViewById(R.id.wave);
            if(wave instanceof IWaveData){
                IWaveData waveData = (IWaveData) wave;
                waveData.reset();
                waveData.setTitle(findParamsByParaId(type).getParaValue(), type);
            }

        }
    }

    private class ECGViewHolder extends ViewHolder{
        private ECGView ecgView;
        public ECGViewHolder(View itemView) {
            super(itemView);
            ecgView = (ECGView) itemView;
        }
    }

    private class DefaultViewHolder extends ViewHolder{
        public TempDataView tempDataView;
        public NibpDataView nibpDataView;
        public DefaultViewHolder(View itemView) {
            super(itemView);
            tempDataView = (TempDataView) itemView.findViewById(R.id.temp_data_view);
            nibpDataView = (NibpDataView) itemView.findViewById(R.id.nibp_data_view);
        }
    }

}
