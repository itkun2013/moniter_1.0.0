package com.konusng.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.konsung.R;
import com.konsung.bean.DictAttr;
import com.konsung.popup.SelectAttrPopup;

/**
 * Created by Cmad on 2015/9/1.
 * popup弹出选择的adapter
 */
public class SelectAttrAdapter
        extends CustomBaseAdapter<DictAttr>
{
    /**
     * CustomBaseAdapter
     *
     * @param context
     */
    private SelectAttrPopup.OnSelectListener mListener;
    public SelectAttrAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            view = textView = new TextView(context);
            textView.setPadding(0, 10, 0, 10);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(12);
            textView.setBackgroundColor(context.getResources()
                                               .getColor(R.color.popup_select));
        } else {
            textView = (TextView) view;
        }
        DictAttr attr = listData.get(position);
        textView.setText(attr.getDictAttrName());
        return view;
    }

}
