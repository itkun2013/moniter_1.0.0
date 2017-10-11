package com.konsung.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.konsung.R;
import com.konsung.bean.DictAttr;
import com.konusng.adapter.SelectAttrAdapter;

import java.util.List;

/**
 * Created by Cmad on 2015/9/1.
 * 配置界面弹出选择的popup
 */
public class SelectAttrPopup
        extends PopupWindow
        implements AdapterView.OnItemClickListener {
    private ListView listView;
    private Context context;
    private SelectAttrAdapter mAdapter;
    private OnSelectListener mListener;

    public SelectAttrPopup(Context context) {
        super(context);
        this.context = context;
        View contentView = LayoutInflater.from(context)
                .inflate(R.layout.select_attr_popup, null);
        setContentView(contentView);
        listView = (ListView) contentView.findViewById(R.id.listview);
        mAdapter = new SelectAttrAdapter(context);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(this);
        //        listView.setItemChecked();
        //        setWidth(100);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setDicAttrs(List<DictAttr> attrs) {
        mAdapter.setListData(attrs);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        DictAttr attr = mAdapter.getItem(position);
        //接口让子类去实现
        if (mListener != null) {
            mListener.onSelect(attr);
        }
        dismiss();
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.mListener = listener;
    }

    /**
     * 选择监听接口
     */
    public interface OnSelectListener {
        void onSelect(DictAttr attr);
    }
}
