package com.konusng.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsung.bean.Sort;

/**
 * Created by chengminghui on 15/8/29.
 * 配置界面一级菜单的adapter
 */
public class SortAdapter extends CustomBaseAdapter<Sort>{

    /**
     * CustomBaseAdapter
     *
     * @param context
     */
    public SortAdapter(Context context) {
        super(context);
    }

    @Override
    public long getItemId(int position) {
        return listData.get(position).getSortID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView ;
        if(convertView == null){
            convertView = textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(0,20,0,20);
            textView.setTextSize(20);
        }else{
            textView = (TextView) convertView;
        }

        Sort menu = listData.get(position);
        textView.setText(menu.getSortName());

        return convertView;
    }
}
