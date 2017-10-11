package com.konsung.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.konsung.R;
import com.konsung.bean.Sort;
import com.konsung.util.DBManager;
import com.konsung.util.DisplayUtils;
import com.konsung.util.GlobalConstant;
import com.konusng.adapter.MenuExpandaListAdapter;
import com.konusng.adapter.SortAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chengminghui on 15/8/29.
 * 参数配置界面
 */
public class ConfigActivity extends Activity implements AdapterView.OnItemClickListener,
        ExpandableListView.OnGroupClickListener {
    /**
     * 左边菜单的listview
     */
    private ListView mFirstMenuListView;
    /**
     * 左边第二级菜单列表
     */
    private ExpandableListView mExpandableListView;
    /**
     * 右边一级菜单的adapter
     */
    private SortAdapter mSortAdapter;
    /**
     * 右边第二级菜单的adapter
     */
    private MenuExpandaListAdapter menuExpandaListAdapter;
    /**
     * 是否可以展开
     */
    private boolean canExpand;
    private int sortId;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //home键标志
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.config_popup);
        initScreenSize();
        sortId = getIntent().getIntExtra(GlobalConstant.SORT_ID, 10);
        initView();
    }

    /**
     * 初始化弹出窗口的大小
     */
    private void initScreenSize() {
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = DisplayUtils.getScreenHeight(this) / 3 * 2;
        p.width = DisplayUtils.getScreenWidth(this) / 3 * 2;
        p.alpha = 1.0f;      //设置本身透明度
        p.dimAmount = 0.0f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
    }

    private void initView() {
        mFirstMenuListView = (ListView) findViewById(R.id.listview);
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandedListView);

        //从数据库获取配置菜单数据
        List<Sort> sorts = DBManager.getConfigDBHelper(this).getRuntimeExceptionDao(Sort.class)
                .queryForEq("ParentID", 0);

        mSortAdapter = new SortAdapter(this);
        mSortAdapter.setListData(sorts);
        mFirstMenuListView.setAdapter(mSortAdapter);

        menuExpandaListAdapter = new MenuExpandaListAdapter(this);
        mExpandableListView.setAdapter(menuExpandaListAdapter);

        mFirstMenuListView.setOnItemClickListener(this);
        mExpandableListView.setOnGroupClickListener(this);

        //设置左边菜单选中，并手动调用你ItemClick方法更新右边菜单数据
        int selectPosition = getSelectPosition();
        onItemClick(null, null, selectPosition, 0);

        //添加广播刷新菜单。用于血压测试时是否禁止启动停止按钮的可点击状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140104);
        filter.addAction(GlobalConstant.ACTION_UPDATE_DATA + 140101);
        registerReceiver(receiver, filter);
    }

    /**
     * 获取左边菜单选中项的position值
     * @return position值
     */
    private int getSelectPosition() {
        List<Sort> sorts = mSortAdapter.getListData();
        for (int i = 0; i < sorts.size(); i++) {
            Sort sort = sorts.get(i);
            if (sort.getSortID() == sortId) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clearFocus();
        Sort sort = mSortAdapter.getItem(position);
        //如果没有第二级菜单则直接显示第三级菜单内容。如：病人信息
        if (sort.getSorts() == null || sort.getSorts().size() == 0) {
            sort.setIsSecondMenu(true);
            //手动添加一个空的二级菜单
            ArrayList<Sort> s = new ArrayList<>();
            s.add(sort);
            menuExpandaListAdapter.setData(s);
            canExpand = true;
            for (int i = 0; i < menuExpandaListAdapter.getGroupCount(); i++) {
                mExpandableListView.collapseGroup(i);
                mExpandableListView.expandGroup(i);
            }
        } else {
            Sort[] ss = new Sort[sort.getSorts().size()];
            sort.getSorts().toArray(ss);
            List<Sort> list = Arrays.asList(ss);
            menuExpandaListAdapter.setData(list);
            canExpand = false;
            for (int i = 0; i < menuExpandaListAdapter.getGroupCount(); i++) {
                ss[i].getSortName();
           /*     Toast.makeText(ConfigActivity.this, ss[i].getSortName()+"-"+ss[i].getSorts()
           .size()+"-"+ss[i].getSortID(), Toast.LENGTH_SHORT)
                     .show();
                for (SortAttr sa:ss[i].getSortAttrs()){
                    Toast.makeText(ConfigActivity.this, sa.getAttrName()+"-"+sa.getAttrValue() ,
                    Toast.LENGTH_SHORT).show();
                }*/

                mExpandableListView.collapseGroup(i);
            }
        }
        mExpandableListView.setSelection(0);

        setTitle(sort.getSortName());
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        clearFocus();
        return canExpand;
    }
    @Override
    public void onBackPressed() {
        //复写该返回键，不继承原来的方法，屏蔽掉
    }
    /**
     * 清除输入框焦点，防止输入框获得焦点时其他操作导致应用crash的情况
     */
    private void clearFocus() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity
                    .INPUT_METHOD_SERVICE);
            if (getCurrentFocus() == null) {
                return;
            }
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager
                    .HIDE_NOT_ALWAYS); //强制隐藏键盘
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GlobalConstant.ACTION_UPDATE_DATA + 140104) ||
                    intent.getAction().equals(GlobalConstant.ACTION_UPDATE_DATA + 140101)) {
                //刷新菜单
                menuExpandaListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
