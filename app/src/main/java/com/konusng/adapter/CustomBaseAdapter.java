package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义baseadapter
 * @param <T>
 */
public abstract class CustomBaseAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> listData;
    protected LayoutInflater listContainer;

    /**
     * CustomBaseAdapter
     */
    public CustomBaseAdapter(Context context) {
        this.context = context;
        this.listContainer = LayoutInflater.from(context);
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        if (data == null) {
            return;
        }
        this.listData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(T t) {
        if(t == null){
            return;
        }
        if (listData == null) {
            listData = new ArrayList<T>();
        }
        this.listData.add(t);
        notifyDataSetChanged();
    }

    public boolean hasData() {
        if (listData == null || listData.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public List<T> getListData() {
        if (listData == null) {
            listData = new ArrayList<T>();
        }
        return listData;
    }

    @Override
    public int getCount() {
        if (listData != null) {
            return listData.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

}

