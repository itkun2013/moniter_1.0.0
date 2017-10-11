package com.konsung.defineview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.konsung.R;
import com.konsung.bean.Dict;
import com.konsung.bean.DictAttr;
import com.konsung.bean.Sort;
import com.konsung.bean.SortAttr;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.popup.SelectAttrPopup;
import com.konsung.util.DBManager;
import com.konsung.util.DPUtils;
import com.konsung.util.DateUtil;
import com.konsung.util.DisplayUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.ToastUtils;
import com.konsung.util.UIUtils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cmad on 2015/8/28.
 * 配置界面三级菜单的item
 */
public class ConfigValueView extends LinearLayout implements View.OnClickListener,
        SelectAttrPopup.OnSelectListener, TextWatcher {
    /**
     * 显示名称
     */
    private TextView tvName;
    /**
     * 是否显示星星
     */
    private TextView tvStart;
    /**
     * 显示值，可编辑
     */
    private EditText etValue;
    /**
     * 显示值，不可输入，可选择
     */
    private TextView tvValue;

    /**
     * 只显示值，不可编辑，不可点击，没有背景
     */
    private TextView tvInfo;
    /**
     * 单位
     */
    private TextView tvUnit;
    private RelativeLayout llValue;
    private Button btn;
    private SortAttr sortAttr;
    /**
     * 弹出选择的popup
     */

    private SelectAttrPopup popup;
    private Context context;
    // AppDevice包名
    private static final String APP_DEVICE_PACKAGE_NAME = "org.qtproject.qt5.android.bindings";

    public ConfigValueView(Context context) {
        this(context, null);
    }

    public ConfigValueView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfigValueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.config_value_view, this);
        tvName = (TextView) findViewById(R.id.name_tv);
        etValue = (EditText) findViewById(R.id.value_et);
        tvValue = (TextView) findViewById(R.id.value_tv);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        llValue = (RelativeLayout) findViewById(R.id.value_layout);
        tvStart = (TextView) findViewById(R.id.tv_start);

        btn = (Button) findViewById(R.id.btn);
        tvUnit = (TextView) findViewById(R.id.unit_tv);
        etValue.addTextChangedListener(this);
        etValue.setOnEditorActionListener(new MyTextEndListener());
        etValue.setOnFocusChangeListener(new MyEditFocusListener());
    }

    public boolean isNumber = false;

    /**
     * 根据sortAttr属性进行界面的配置
     * @param sortAttr 数据字典
     */
    public void setConfigData(SortAttr sortAttr) {

        this.sortAttr = sortAttr;
        if (sortAttr.getSort().getSortID() == 10) {
            //病人信息sortId，显示是否必填项，10对应的是病人信息sortId
            switch (sortAttr.getAttrID()) {
                case 1002:
                    //病历号
                    tvStart.setVisibility(VISIBLE);
                    break;
                case 1004:
                    //姓
                    tvStart.setVisibility(VISIBLE);
                    break;
                case 1005:
                    //名
                    tvStart.setVisibility(VISIBLE);
                    break;
                case 1006:
                    //床号
                    tvStart.setVisibility(VISIBLE);
                    break;
                default:
                    tvStart.setVisibility(GONE);
                    break;
            }
            tvName.setText(sortAttr.getAttrName() + ":");
            etValue.setText(sortAttr.getAttrValue());

        } else {
            tvStart.setVisibility(GONE);
            tvName.setText(sortAttr.getAttrName() + ":");
        }
        switch (sortAttr.getShowType()) {
            case GlobalConstant.CONFIG_VALUE_TYPE_INPUT: //输入类型
                showView(etValue);

                if (sortAttr.getDataType() == 0) { //文字
                    etValue.setInputType(InputType.TYPE_CLASS_TEXT);
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    isNumber = false;
                } else if (sortAttr.getDataType() == 1) { //数字
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etValue.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    isNumber = true;
                } else if (sortAttr.getDataType() == 2) { //带小数点和负号的数字

                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etValue.setKeyListener(DigitsKeyListener.getInstance("0123456789.-"));
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    isNumber = true;
                } else if (sortAttr.getDataType() == 4) { // 心率 血氧 氧气补偿 大气压值
//                    Toast.makeText(getContext(), "4", Toast.LENGTH_SHORT).show();
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType
                            .TYPE_NUMBER_FLAG_DECIMAL);
//                    etValue.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                    isNumber = true;
                } else if (sortAttr.getDataType() == 5) { //体重 身高cm
//                    Toast.makeText(getContext(), "5", Toast.LENGTH_SHORT).show();
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType
                            .TYPE_NUMBER_FLAG_DECIMAL);
                    isNumber = true;
                } else if (sortAttr.getDataType() == 6) { //ST上下限输入 带小数点和负号的数字

                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType
                            .TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    isNumber = true;
                } else if (sortAttr.getDataType() == 7) { //体温
                    //                    Toast.makeText(getContext(), "7", Toast.LENGTH_SHORT)
                    // .show();
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType
                            .TYPE_NUMBER_FLAG_DECIMAL);
                    isNumber = true;
                } else if (sortAttr.getDataType() == 8) {
                    //8带表ip,设置ip
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etValue.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                    isNumber = true;
                } else if (sortAttr.getDataType() == 9) {
                    //9代表端口，设置端口
                    etValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etValue.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    isNumber = true;
                } else if (sortAttr.getDataType() == 10) {
                    //10代表设备号
                    String digists =
                            "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    etValue.setKeyListener(DigitsKeyListener.getInstance(digists));
                    etValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                    isNumber = true;
                }
                etValue.setText(sortAttr.getAttrValue());
                break;
            case GlobalConstant.CONFIG_VALUE_TYPE_CHOOICE: //选择
                showView(tvValue);
                DictAttr dictAttr = new DictAttr();
//                Log.e("abc", "value:"+sortAttr.getAttrValue());
                try {
                    dictAttr.setDictAttrID(Integer.valueOf(sortAttr.getAttrValue()));
                } catch (Exception e) {
                }

                try {
                    DBManager.getConfigDBHelper(getContext())
                            .getRuntimeExceptionDao(DictAttr.class).refresh(dictAttr);
                    tvValue.setText(dictAttr.getDictAttrName());
                } catch (Exception e) {
                    e.printStackTrace();
                    tvValue.setText(DPUtils.getSelectValueBySortAttrName(getContext(), sortAttr
                            .getAttrID()));
                }
                DBManager.getConfigDBHelper(getContext())
                        .getRuntimeExceptionDao(Dict.class).refresh(sortAttr.getDict());
//                Log.e("选择框AttrName",dictAttr.getDictAttrName());
                tvValue.setOnClickListener(this);
                break;
            case GlobalConstant.CONFIG_VALUE_TYPE_BUTTON: //按钮
                tvName.setVisibility(View.GONE);
                llValue.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(this);
                btn.setText(sortAttr.getAttrName());
                //对id为140103（见数据库）的id进行特殊处理
                if (sortAttr.getAttrID() == 140103) { //血压测量启动
                    if (NibpDataView.NIBP_MEASURE_START) {
                        //自动测量模式
//                        MyApplication.isStopMeasureNibp = true;

//                        PreferenceUtils.putBoolean(getContext(), NibpDataView
// .NIBP_MEASURE_STOP, true);
                        btn.setEnabled(false);
                    } else {
                        //手动测量模式
//                        MyApplication.isStopMeasureNibp = false;

//                        PreferenceUtils.putBoolean(getContext(), NibpDataView
// .NIBP_MEASURE_STOP, false);
                        btn.setEnabled(true);
                    }
                }
                //血压测量停止  TODO
                if (sortAttr.getAttrID() == 140104) {
                    //MyApplication.isStopMeasureNibp
                    if (!NibpDataView.NIBP_MEASURE_START) {
//                        MyApplication.isAutoMeasureNibp = false;

//                        PreferenceUtils.putBoolean(getContext(), NibpDataView
// .NIBP_MEASURE_AUTO, false);
                        btn.setEnabled(false);
                    } else {
                        //需要明确返回 是在测量了
//                        MyApplication.isAutoMeasureNibp = true;

//                        PreferenceUtils.putBoolean(getContext(), NibpDataView
// .NIBP_MEASURE_AUTO, true);
                        btn.setEnabled(true);
                    }
                }
                //漏气检测
                if (sortAttr.getAttrID() == 140105) {
                    //在测量的时候能不能启动?
                    btn.setEnabled(true);
                }
                //血压测试复位
                if (sortAttr.getAttrID() == 140106) {
                    //在测量的时候能不能启动?
                    btn.setEnabled(true);
                }
                break;
            case GlobalConstant.CONFIG_VALUE_TYPE_VIEW: // 显示类型
                showView(tvInfo);
                if (sortAttr.getDataType() == 11) {
                    //11代表软件版本
                    tvInfo.setText(getAppInfo());
                } else if (sortAttr.getDataType() == 12) {
                    //12代表适配器版本
                    tvInfo.setText(getAppDeviceInfo());
                }
                break;
            default:
                break;
        }
        //设置单位
        if (!TextUtils.isEmpty(sortAttr.getUnit())) {
            tvUnit.setText(sortAttr.getUnit());
        } else {
            tvUnit.setText("");
        }
    }

    /**
     * 获取软件版本信息
     * @return 软件版本信息字符串
     */
    private String getAppInfo() {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName
                    (), PackageManager.GET_CONFIGURATIONS);
            return info.versionName + "-" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取AppDevice版本号
     * @return AppDevice版本号
     */
    private String getAppDeviceInfo() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(APP_DEVICE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        if (packageInfo == null) {
            return "";
        }
        int code = packageInfo.versionCode;
        return String.valueOf(code);
    }

    /**
     * 设置显示的view
     * @param view 要显示的view
     */
    private void showView(View view) {
        etValue.setVisibility(View.GONE);
        tvValue.setVisibility(View.GONE);
        tvInfo.setVisibility(GONE);
        btn.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        llValue.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        //点击后对弹出的子菜单栏进行赋值。例：导联类型：3、5； 计算导联I、II
        if (view.getId() == R.id.value_tv) {
            if (popup == null) {
                popup = new SelectAttrPopup(getContext());
                popup.setOnSelectListener(this);
                popup.setWidth(tvValue.getWidth());
            }
            DictAttr[] attrs = new DictAttr[sortAttr.getDict().getDictAttrs().size()];
            List<DictAttr> dictList = Arrays.asList(sortAttr.getDict().getDictAttrs()
                    .toArray(attrs));
            boolean isLead5 = (int) DPUtils.getSelectValueBySortAttrId(getContext(),
                    GlobalConstant.ECG_DL) == 1;
            //5导联下计算导联设置项不需要III选项
            if (dictList.size() == 3 && dictList.get(2).getDictAttrName().equals("III")
                    && isLead5) {
                popup.setDicAttrs(dictList.subList(0, 2));
            } else {
                popup.setDicAttrs(dictList);
            }
            int[] location = new int[2];
            view.getLocationInWindow(location);
            int x = location[0];
            int y = location[1] + getHeight();
            int height = DisplayUtils.getScreenHeight(getContext()) / 3 * 2;
            int offset = attrs.length * (20 + DisplayUtils.dip2px(getContext(), 12));
            if (y > height - offset) {
                popup.showAsDropDown(view, 0, -(y - (height - offset - getHeight())));
            } else {
                popup.showAsDropDown(view, 0, -10);
            }
        } else if (view.getId() == R.id.btn) {
            if (sortAttr.getSort().getSortID() == 10) {
                //创建病人时间
                RuntimeExceptionDao<Sort, Integer> sortDao = DBManager.getConfigDBHelper
                        (getContext()).getSortDao();
                sortAttr.getSort().setCreateTime(DateUtil.getCurrentDate());
                //更新sort
                sortDao.update(sortAttr.getSort());
                DPUtils.changePatientConfig(getContext(), sortAttr.getSort());
            } else if (sortAttr.getSort().getSortID() == 1401) {
                if (sortAttr.getAttrID() == 140103) {
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                } else if (sortAttr.getAttrID() == 140104) {

//                    MyApplication.isAutoMeasureNibp = false;
//                    MyApplication.isStopMeasureNibp = false;

//                    PreferenceUtils.putBoolean(getContext(), NibpDataView.NIBP_MEASURE_AUTO,
// false);
//                    PreferenceUtils.putBoolean(getContext(), NibpDataView.NIBP_MEASURE_STOP,
// false);
//                    PreferenceUtils.putBoolean(getContext(), NibpDataView.NIBP_MEASURE_ING,
// false);
                }
                //TODO  漏气检测  复位检测
                getContext().sendBroadcast(new Intent(GlobalConstant.ACTION_UPDATE_DATA +
                        sortAttr.getAttrID()));
            }
        }
    }

    @Override
    public void onSelect(DictAttr attr) {
        //在切换导联类型的时候需要刷新界面，计算导联默认为初始状态II导联。
        //110109为计算导联在数据库的id，120002为II导联在表里的id
        if (attr.getDict().getDictID() == GlobalConstant.LEAD_MOLD) {
            List<SortAttr> sortAttrs = DBManager.getConfigDBHelper(getContext())
                    .getRuntimeExceptionDao(SortAttr.class)
                    .queryForEq(GlobalConstant.ATTR_ID, "110109");
            SortAttr sortAttr = sortAttrs.get(0);
            sortAttr.setAttrValue("120002");
            DBManager.getConfigDBHelper(getContext())
                    .getRuntimeExceptionDao(SortAttr.class).update(sortAttr);
            //切换
            EchoServerEncoder.setConfig(GlobalConstant.NET_ECG_CONFIG, (short) 3, 1);
        }
        sortAttr.setAttrValue(attr.getDictAttrID() + "");
        sortAttr.setAttr(attr);
        tvValue.setText(attr.getDictAttrName());
        updateSortAttr();
    }

    /**
     * 保存更新修改值
     * 需要发送配置包的则发送配置包
     * 发送相应的广播
     */
    private void updateSortAttr() {
        //个人信息不实时保存，点击保存的时候再保存
        if (sortAttr.getSort().getSortID() == GlobalConstant.TYPE_PATIENT) {
            return;
        }
        //保存到数据库
        DBManager.getConfigDBHelper(getContext()).getRuntimeExceptionDao(SortAttr.class).update
                (sortAttr);
        //波形增益发送广播
        if (sortAttr.isAppDevice()) { //发送配置包
            if (sortAttr.getSort().getParent() != null) {
                DPUtils.sendConfig(getContext(), sortAttr);
            }
        }
        //发送广播
        getContext().sendBroadcast(new Intent(GlobalConstant.ACTION_UPDATE_DATA + sortAttr
                .getAttrID()));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String s = charSequence.toString();
        if (sortAttr.getDataType() == 1 || sortAttr.getDataType() == 2 || sortAttr.getDataType()
                == 9) {
            //正整数
            if (s.startsWith(" ") || s.startsWith("0") || s.startsWith(".")) {
                etValue.setText("");
            }
        } else if (sortAttr.getDataType() == 4 || sortAttr.getDataType() == 5 ||
                sortAttr.getDataType() == 7 || sortAttr.getDataType() == 8) {
            //含正负的小数
            if (s.startsWith(" ") || s.startsWith(".")) {
                etValue.setText("");
                return;
            }
            if (s.equals("0.")) {
                return;
            }
            if (s.startsWith("0") && s.length() > 1) {
                if (!s.substring(1, 2).equals(".")) {
                    etValue.setText(s.substring(0, 1));
                    etValue.setSelection(1);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        changeText = etValue.getText().toString();
        //edittext数据变化，且为输入类型时，对象同步修改。因为数据是保存对象
        if (sortAttr.getSort().getSortID() == GlobalConstant.TYPE_PATIENT
                && sortAttr.getShowType() == GlobalConstant.CONFIG_VALUE_TYPE_INPUT) {
            sortAttr.setAttrValue(changeText);
        }
        mHandler.removeCallbacks(textChangeRun);//删除队列中未执行的线程对象
        //延迟2秒执行判断值是否有效操作（即时判断会导致输入出现问题）
        mHandler.postDelayed(textChangeRun, 500); //200ms  2s
    }

    private String changeText = "";
    //    private boolean isNotNull = false;
    private Handler mHandler = new Handler();
    private Runnable textChangeRun = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(changeText.trim())) {
                /**
                 * 由于病人与网络2个模块为空，监听频繁弹出toast,特屏蔽提示
                 */
                if (sortAttr.getSort().getSortID() == 10) {
                    //病人信息，不提示
                    return;
                }
                if (sortAttr.getSort().getSortID() == 1901) {
                    //网络ip ,端口，不提示
                    return;
                }

                //其他输入，为空，提示为无效值
                ToastUtils.toastContent(getContext(), getContext().getString(R.string
                        .not_valid));

                return;
            }
            //判断是否有效，有效则保存，否则提升无效
            if (DPUtils.isValid(getContext(), sortAttr, changeText)) {
                //当它为输入类型才改变。
                if (sortAttr.getShowType() == GlobalConstant.CONFIG_VALUE_TYPE_INPUT) {
                    sortAttr.setAttrValue(changeText);
                }
                //更新数据库
                updateSortAttr();
                if (sortAttr.getDataType() == 8) {
                    String port = DPUtils.getStringBySortAttrId(UIUtils.getContext(),
                            GlobalConstant.SERVER_PORT_CONFIG);
                    //改变ip,给appdevice发送
                    EchoServerEncoder.setServerAddress(sortAttr.getAttrValue().trim(), Short
                            .parseShort(port.trim()));
                }
                if (sortAttr.getDataType() == 9) {
                    String ip = DPUtils.getStringBySortAttrId(UIUtils.getContext(),
                            GlobalConstant.SERVER_IP_CONFIG);
                    EchoServerEncoder.setServerAddress(ip.trim(), Short.parseShort(sortAttr
                            .getAttrValue().trim()));
                }
                if (sortAttr.getDataType() == 10) {
                    //设备号改变，发送数据给appdevices
                    DPUtils.sendPatientConfig(getContext());
                }
            } else {
                //选择是哪个参数超出限制
                switch (sortAttr.getAttrID()) {
                    case GlobalConstant.BREATHE_UP:
                        //呼吸上限
                        ToastUtils.toastContent(getContext(), getContext().getString(R.string
                                .over_breath_top));
                        break;
                    case GlobalConstant.BREATHE_DOWN:
                        //呼吸下限
                        ToastUtils.toastContent(getContext(), getContext().getString(R.string
                                .over_breath_down));
                        break;
                    default:
                        ToastUtils.toastContent(getContext(), getContext().getString(R.string
                                .not_valid));
                        break;
                }
            }
        }
    };

    /**
     * axx
     * 输入完成监听 2017-2-15 15:38:16
     */
    class MyTextEndListener implements EditText.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            /*if(KeyEvent.ACTION_DOWN == event.getAction()){
//                if(actionId== EditorInfo.IME_ACTION_SEARCH || actionId== EditorInfo
.IME_ACTION_NEXT || actionId== EditorInfo.IME_ACTION_DONE){
                if(actionId==KeyEvent.ACTION_DOWN || actionId== EditorInfo.IME_ACTION_DONE){
                    Toast.makeText(getContext(),"input:down",Toast.LENGTH_SHORT).show();

                    return true;
                }
            }*/

            if (sortAttr.getDataType() == 5) {
                //体重,身高

            } else if (sortAttr.getDataType() == 6) {
                //心率的ST
                String s = etValue.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    float f = Float.parseFloat(s);
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    s = decimalFormat.format(f);
                    String ss = String.format("%.2f", f);
//                Toast.makeText(getContext(),"input:end-"+f+">"+s+">"+ss, Toast.LENGTH_SHORT)
// .show();
                    etValue.setText(ss);
                }
            } else if (sortAttr.getDataType() == 7) {
                //体温
                String tw = etValue.getText().toString();
                if (!TextUtils.isEmpty(tw)) {
                    float ftw = Float.parseFloat(tw);
                    String tws = String.format("%.1f", ftw);
                    etValue.setText(tws);
                }
            }

            return false;
        }
    }

    public class MyEditFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
//            Toast.makeText(getContext(), "MyEditFocusListener", Toast.LENGTH_SHORT).show();

            if (!hasFocus) {
                String s = etValue.getText().toString();
//                没有焦点了
                if (sortAttr.getDataType() == 5) {
                    if (TextUtils.isEmpty(s) || !s.matches("[0-9]+\\.?[0-9]*")) {
                        return;
                    }
                    float f = Float.parseFloat(s);
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    s = decimalFormat.format(f);
                    String ss = String.format("%.1f", f);
                    etValue.setText(ss);
//                    etValue.removeTextChangedListener(ConfigValueView.this);
//                    etValue.addTextChangedListener(ConfigValueView.this);
                } else if (sortAttr.getDataType() == 0) {
                    //文字
                    if (TextUtils.isEmpty(s)) {
                        etValue.setText(" ");
                    } else {
                        etValue.setText(s);
                    }
                } else if (sortAttr.getDataType() == 1) {
                    //数字
                    if (TextUtils.isEmpty(s)) {
                        etValue.setText(" ");
                    } else {
                        etValue.setText(s);
                    }
                } else if (sortAttr.getDataType() == 8) {
                    //8代表是修改ip地址，限制数字规范输入
                    if (TextUtils.isEmpty(s)) {
                        etValue.setText(" ");
                    } else {
                        etValue.setText(s);
                    }
                }
            }
        }
    }
}
