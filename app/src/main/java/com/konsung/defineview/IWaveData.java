package com.konsung.defineview;

/**
 * Created by chengminghui on 15/8/30.
 * 波形view的接口
 */
public interface IWaveData {
    /**
     * 设置数据
     * @param data byte【】数据
     */
    void setData(byte[] data);

    /**
     * 设置标题
     * @param title 标题
     * @param type 类型
     */
    void setTitle(String title,int type);

    /**
     * 重置
     */
    void reset();

    /**
     * 停止
     */
    void stop();
}
