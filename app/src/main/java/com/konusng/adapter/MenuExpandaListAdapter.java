package com.konusng.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.konsung.bean.Sort;
import com.konsung.bean.SortAttr;
import com.konsung.defineview.ConfigValueView;

import java.util.List;

/**
 * Created by chengminghui on 15/8/29.
 * 配置界面 二级菜单adapter
 */
public class MenuExpandaListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Sort> sorts;

    public MenuExpandaListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Sort> sorts) {
        this.sorts = sorts;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        if (sorts == null) {
            return 0;
        }
        return sorts.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (sorts.get(groupPosition).getSortAttrs() == null) {
            return 0;
        }
        return sorts.get(groupPosition).getSortAttrs().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sorts.get(groupPosition).getSortAttrs().toArray()[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup
            parent) {
        TextView textView;
        if (convertView == null) {
            convertView = textView = new TextView(context);
        } else {
            textView = (TextView) convertView;
        }
        Sort sort = sorts.get(groupPosition);
        //病人信息没有二级菜单 不显示二级菜单
        if (sort.isSecondMenu()) {
            textView.setText("");
            textView.setTextSize(1);
            textView.setPadding(0, 0, 0, 0);
        } else {
            textView.setTextSize(20);
            textView.setText(sort.getSortName());
            textView.setPadding(20, 20, 0, 20);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        ConfigValueView configValueView = null;
        if (convertView == null) {
//            configValueView = new ConfigValueView(context);
//            convertView = configValueView;
//            convertView.setTag(configValueView);

            convertView = configValueView = new ConfigValueView(context);
        } else {
//            configValueView = (ConfigValueView) convertView.getTag();
            configValueView = (ConfigValueView) convertView;
        }
        SortAttr sortAttr = (SortAttr) getChild(groupPosition, childPosition);
        configValueView.setConfigData(sortAttr);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
