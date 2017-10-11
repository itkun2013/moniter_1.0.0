package com.konsung.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.konsung.R;
import com.konsung.bean.Confine;
import com.konsung.bean.DictAttr;
import com.konsung.bean.Sort;
import com.konsung.bean.SortAttr;
import com.konsung.netty.EchoServerEncoder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by chengminghui on 15/9/5.
 * 向socket发送数据的处理类
 */
public class DPUtils {

    private static final int MEDICALRECORD = 1001;//病例号
    private static final int PATIENTSURNAME = 1002;//病人姓
    private static final int PATIENTNAME = 1003;//病人名
    private static final int DOCTORNAME = 1004;//医生姓名
    private static final int OFFICE = 1005;//科室
    private static final int BED_NUM = 1006;//床号
    private static final String DEFULT_PATIENT_INFOR = "";//默认病人信息为空，方便编辑
    private static final int USERID = 10; //用户sortiId

    public static void changePatientConfig(Context context, Sort sort) {
        savePatient2DB(context, sort);
    }

    //向APPDevice发送病人信息配置
    public static void sendPatientConfig(Context context) {
        short cycle = getSelectValueBySortAttrId(context, 1008);
        short bedNum = getValueBySortAttrId(context, BED_NUM);
        short sex = getSelectValueBySortAttrId(context, 1007);
        short blood = getSelectValueBySortAttrId(context, 1009);
        float weight = getFloatValueBySortAttrId(context, 1011);
        float height = getFloatValueBySortAttrId(context, 1010);
        short isbegin = 0; //起博 配置里暂无此设置，因文档说明病人信息只需要病人类型，这里随便设置了一个值
        String medicalRecord = getStringValueBySortAttrId(context, MEDICALRECORD);
        String patientSurname = getStringValueBySortAttrId(context, PATIENTSURNAME);
        String patientName = getStringValueBySortAttrId(context, PATIENTNAME);
        String doctorName = getStringValueBySortAttrId(context, DOCTORNAME);
        String office = getStringValueBySortAttrId(context, OFFICE);
        String creatTime = getCreatTimeBySortAttrId(context, USERID);
        EchoServerEncoder.setPatientConfig(cycle, sex, blood, weight, height, isbegin,
                medicalRecord, patientSurname, patientName, doctorName, office, creatTime, bedNum);
    }

    //保存病人信息到数据库
    private static void savePatient2DB(Context context, Sort sort) {
        //这一段是刷入限制，暂时屏蔽
        for (SortAttr sortAttr : sort.getSortAttrs()) {
//            if (sortAttr.getAttrID() == 1001) { //科室
//                if (TextUtils.isEmpty(sortAttr.getAttrValue().trim())) {
//                    LogUtils.e("KKK", "科室为空");
//                    Toast.makeText(context, "科室为空", Toast.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
            if (sortAttr.getAttrID() == 1002) { //病历号
                if (TextUtils.isEmpty(sortAttr.getAttrValue().trim())) {
                    Toast.makeText(context, UIUtils.getContext().getString(R.string.not_empty_2),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
         /*   if (sortAttr.getAttrID() == 1003) { //医生
                if (TextUtils.isEmpty(sortAttr.getAttrValue().trim())) {
                    Toast.makeText(context, UIUtils.getContext().getString(R.string.not_empty_3),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }*/
            if (sortAttr.getAttrID() == 1004) { //姓氏
                if (TextUtils.isEmpty(sortAttr.getAttrValue().trim())) {
                    Toast.makeText(context, UIUtils.getContext().getString(R.string.not_empty_4),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (sortAttr.getAttrID() == 1005) { //名字
                if (TextUtils.isEmpty(sortAttr.getAttrValue().trim())) {
                    Toast.makeText(context, UIUtils.getContext().getString(R.string.not_empty_5),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (sortAttr.getAttrID() == 1006) { //床号
                if (TextUtils.isEmpty(sortAttr.getAttrValue())) {
                    Toast.makeText(context, UIUtils.getContext().getString(R.string.not_empty_6),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
//
//            //类型
//            if (sortAttr.getAttrID() == 1008) { //居民类型
//                if (TextUtils.isEmpty(sortAttr.getAttrValue())) {
//                    Toast.makeText(context, "病人类型不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//            //性别
//            if (sortAttr.getAttrID() == 1007) {
//                Log.e("abc", "1007value:" + sortAttr.getAttrValue());
//                if (TextUtils.isEmpty(sortAttr.getAttrValue())) {
//                    Toast.makeText(context, "性别不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//
//            //血型
//            if (sortAttr.getAttrID() == 1009) {
//                Log.e("abc", "value:" + sortAttr.getAttrValue());
//                if (TextUtils.isEmpty(sortAttr.getAttrValue())) {
//                    Toast.makeText(context, "血型不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
////            //体重
////            if (sortAttr.getAttrID() == 1011) {
////                if (TextUtils.isEmpty(sortAttr.getAttrValue())) {
////                    sortAttr.setAttrValue("0.00");
////                }
////            }
////            //身高
////            if (sortAttr.getAttrID() == 1010) {
////                if (TextUtils.isEmpty(sortAttr.getAttrValue())) {
////                    sortAttr.setAttrValue("0.00");
////                }
////            }
//            //体重,身高要求保留小数点后2位
////            if (sortAttr.getAttrID() == 1011 || sortAttr.getAttrID() == 1010) {
////                if (sortAttr.getAttrValue().length() > 2 && sortAttr.getAttrValue().contains
// (".")) {
//////                    String str = String.format("%.2f",sortAttr.getAttrValue());
////                    String str = sortAttr.getAttrValue();
////                    sortAttr.setAttrValue(formateRate(str, context));
////                }
////            }
//
        }
        DBManager.getConfigDBHelper(context).getSortDao().createOrUpdate(sort);
        for (SortAttr sortAttr : sort.getSortAttrs()) {
            DBManager.getConfigDBHelper(context).getSortAttrDao().createOrUpdate(sortAttr);
        }
        ToastUtils.toastContent(context, context.getString(R.string.save_patient_sucess));
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
        context.sendBroadcast(new Intent("com.kongsung.notify.switchpatient")); //切换病人 关闭警告 重置无效值
        sendPatientConfig(context);
    }

    /**
     * 通过sortAttr id获取对应选择的值
     * @param context 上下文
     * @param id SortAttrId
     * @return 值
     */
    public static short getSelectValueBySortAttrId(Context context, int id) {
        RuntimeExceptionDao<SortAttr, Integer> sortAttrDao = DBManager.getConfigDBHelper(context)
                .getSortAttrDao();
        RuntimeExceptionDao<DictAttr, Integer> dictAttrDao = DBManager.getConfigDBHelper(context)
                .getDictAttrDao();

        String dictAttrId = sortAttrDao.queryForId(id).getAttrValue();
//        WriterLog.saveLog("通过sortAttr id获取对应选择的值dictAttrId:"+dictAttrId);
//        Log.e("abc", ":"+dictAttrId);
        try {
            DictAttr dictAttr = dictAttrDao.queryForId(Integer.valueOf(dictAttrId));
            short value = Short.valueOf(dictAttr.getDictAttrValue());
            return value;
        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * 通过sortAttr id获取对应选择的name
     * @param context 上下文
     * @param id SortAttrId
     * @return DictAttrName
     */
    public static String getSelectValueBySortAttrName(Context context, int id) {
        RuntimeExceptionDao<SortAttr, Integer> sortAttrDao = DBManager.getConfigDBHelper(context)
                .getSortAttrDao();
        RuntimeExceptionDao<DictAttr, Integer> dictAttrDao = DBManager.getConfigDBHelper(context)
                .getDictAttrDao();

        String dictAttrId = sortAttrDao.queryForId(id).getAttrValue();
        DictAttr dictAttr = dictAttrDao.queryForId(Integer.valueOf(dictAttrId));
        String name = dictAttr.getDictAttrName();
        return name;
    }

    /**
     * 通过dictAttr id获取对应short值
     * @param context
     * @param id
     * @return
     */
    public static short getValueByDictAttrId(Context context, String id) {
        try {
            RuntimeExceptionDao<DictAttr, Integer> dictAttrDao = DBManager.getConfigDBHelper
                    (context).getDictAttrDao();
            DictAttr dictAttr = dictAttrDao.queryForId(Integer.valueOf(id));
            short value = Short.valueOf(dictAttr.getDictAttrValue());
            return value;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 通过sortAttr id获取对应的String值
     * @param context 上下文
     * @param id attrId
     * @return 字符串
     */
    public static short getValueBySortAttrId(Context context, int id) {
        RuntimeExceptionDao<SortAttr, Integer> sortAttrDao = DBManager.getConfigDBHelper(context)
                .getSortAttrDao();

        SortAttr sortAttr = sortAttrDao.queryForId(id);
        try {
            short value = Short.valueOf(sortAttr.getAttrValue());
            return value;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 通过sortAttr id获取对应的String值
     * @param context 上下文
     * @param id attrId
     * @return 字符串
     */
    public static String getStringValueBySortAttrId(Context context, int id) {
        RuntimeExceptionDao<SortAttr, Integer> sortAttrDao = DBManager.getConfigDBHelper(context)
                .getSortAttrDao();

        SortAttr sortAttr = sortAttrDao.queryForId(id);
        String value = "";
        try {
            value = sortAttr.getAttrValue();
        } catch (Exception e) {

        }
        return value;
    }

    /**
     * 根据id获取对应的设备值
     * @param context 上下文
     * @param id sortId
     * @return 返回创建的设备号
     */
    public static String getCreatTimeBySortAttrId(Context context, int id) {
        RuntimeExceptionDao<Sort, Integer> sortDao = DBManager.getConfigDBHelper(context)
                .getSortDao();

        Sort sort = sortDao.queryForId(id);
        String value = null;
        try {
            value = sort.getCreateTime();
        } catch (Exception e) {

        }
        return value;
    }

    /**
     * 通过sortAttr id获取对应的float值
     * @param context 上下文
     * @param id SortAttrId
     * @return 浮点值
     */
    public static float getFloatValueBySortAttrId(Context context, int id) {
        RuntimeExceptionDao<SortAttr, Integer> sortAttrDao = DBManager.getConfigDBHelper(context)
                .getSortAttrDao();

        SortAttr sortAttr = sortAttrDao.queryForId(id);
        float value = 0;
        try {
            value = Float.valueOf(sortAttr.getAttrValue());
        } catch (Exception e) {

        }
        return value;
    }

    /**
     * 通过sortAttr id获取对应的String值 ip  port 设备值
     * @param context 上下文
     * @param id AttrID
     * @return ip或者port
     */
    public static String getStringBySortAttrId(Context context, int id) {
        RuntimeExceptionDao<SortAttr, Integer> sortAttrDao = DBManager.getConfigDBHelper(context)
                .getSortAttrDao();

        SortAttr sortAttr = sortAttrDao.queryForId(id);
        String value = null;
        if (sortAttr != null) {
            try {
                value = sortAttr.getAttrValue();
                if (value != null) {
                    //本地为空串，给一个默认
                    if (value.trim().equals("")) {
                        if (id == GlobalConstant.SERVER_IP_CONFIG) {
                            value = GlobalConstant.SERVER_IP_DEFULT;
                        } else if (id == GlobalConstant.SERVER_PORT_CONFIG) {
                            value = GlobalConstant.SERVER_PORT_DEFULT;
                        } else if (id == GlobalConstant.SERVER_DEVICE_CONFIG) {
                            value = GlobalConstant.SERVER_DEVICE_DEFULT;
                        }
                    }
                }
            } catch (Exception e) {

            }
        }

        return value;
    }

    /**
     * 判断SortAttrId是否获取对应的数据，没有则直接赋值
     * @param id SortAttrId
     * @param sortAttrDao 数据dao
     * @param sortAttr 表
     * @return 是否有值
     */
    private static boolean switchSortAttrId(int id, RuntimeExceptionDao<SortAttr, Integer>
            sortAttrDao,
            SortAttr sortAttr) {
        if (id == GlobalConstant.SERVER_IP_CONFIG) {
            sortAttr.setAttrValue(GlobalConstant.SERVER_IP_DEFULT);
            sortAttrDao.update(sortAttr);
            return true;
        } else if (id == GlobalConstant.SERVER_PORT_CONFIG) {
            sortAttr.setAttrValue(GlobalConstant.SERVER_PORT_DEFULT);
            sortAttrDao.update(sortAttr);
            return true;
        } else if (id == GlobalConstant.SERVER_DEVICE_CONFIG) {
            sortAttr.setAttrValue(GlobalConstant.SERVER_DEVICE_DEFULT);
            sortAttrDao.update(sortAttr);
            return true;
        }
        return false;
    }

    /**
     * 配置修改以后发送配置包
     */
    public static void sendConfig(Context context, SortAttr sortAttr) {
        byte comdId = (byte) sortAttr.getSort().getParent().getOrderID();
        short configType = (short) sortAttr.getOrderType();
        int configValue = getValueByDictAttrId(context, sortAttr.getAttrValue());
        //如果切换12导联，发2次命令，避免v2-v6没有波形数据9(该阶段屏蔽)
//        int configValue = Integer.valueOf(sortAttr.getAttr().getDictAttrValue());
        EchoServerEncoder.setConfig(comdId, configType, configValue);
        EchoServerEncoder.setConfig(comdId, configType, configValue);
    }

    /**
     * 发送所有配置信息
     * @param context
     */
    public static void sendAllConfig(Context context) {
        List<Sort> sorts = DBManager.getConfigDBHelper(context).getRuntimeExceptionDao(Sort
                .class).queryForEq("ParentID", 0);
        for (Sort sort : sorts) {//一级目录(病人信息、心电、血氧……)
            if (sort.getSorts() != null && sort.getSorts().size() > 0) {
                for (Sort s : sort.getSorts()) { //二级目录（心电设置、报警设置……）
                    if (s.getSortAttrs() != null && s.getSortAttrs().size() > 0) {
                        for (SortAttr sortAttr : s.getSortAttrs()) { //三级目录（具体设置项）
                            if (sortAttr.isAppDevice() && sortAttr.getSort().getParent() != null)
                            { //发送配置包
                                sendConfig(context, sortAttr);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void sendInitConfig(Context context) {
        //发送病人信息
        DPUtils.sendPatientConfig(context);
        //获取本地保存的数据
        String ip = DPUtils.getStringBySortAttrId(UIUtils.getContext(), GlobalConstant
                .SERVER_IP_CONFIG);
        String port = DPUtils.getStringBySortAttrId(UIUtils.getContext(), GlobalConstant
                .SERVER_PORT_CONFIG);
        //发送appdevices连接
        EchoServerEncoder.setServerAddress(ip.trim(), Short.parseShort(port.trim()));

        int v = DPUtils.getSelectValueBySortAttrId(context, 110101);
        EchoServerEncoder.setEcgConfig((short) 0x02, v);//设置导联类型
        //发送体温探头接触式命令
        EchoServerEncoder.setTempConfig((short) 0x01, 0);
        DPUtils.sendAllConfig(context);
    }

    /**
     * 设置值是否有效
     * @param context context
     * @param sortAttr 修改前的sortArrt
     * @param text 修改后的值
     * @return 是否有效
     */
    public static boolean isValid(Context context, SortAttr sortAttr, String text) {
        if (sortAttr.getDataType() == 0) {
            return true;
        }

//        if()

        try {
            RuntimeExceptionDao<Confine, Integer> confineDao = DBManager.getConfigDBHelper
                    (context).getConfineDao();
            RuntimeExceptionDao<SortAttr, Integer> sortAttrDao = DBManager.getConfigDBHelper
                    (context).getSortAttrDao();
            List<Confine> confines = confineDao.queryForEq("AttrID", sortAttr.getAttrID());
            if (confines != null && confines.size() > 0) {
                Confine confine = confines.get(0);
                sortAttrDao.refresh(confine.getSortAttr());
                float value = Float.valueOf(text);
                //判断设置值是否超过对应的上下限
                if (confine.getType() == 0) {
                    return value > Float.valueOf(confine.getSortAttr().getAttrValue());
                } else {
                    return value < Float.valueOf(confine.getSortAttr().getAttrValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //格式化 电子化移交完成率 保留两位
    public static String formateRate(String rateStr, Context context) {
        if (rateStr.indexOf(".") != -1) {
            //获取小数点的位置
            int num = 0;
            num = rateStr.indexOf(".");//0.333
            if (rateStr.length() > (num + 2)) {
                //这里需要四舍五入精确取值
                BigDecimal b = new BigDecimal(Double.parseDouble(rateStr));
                b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String dd = String.valueOf(b);
//                DecimalFormat fnum   =   new   DecimalFormat("##0.00");
//                String dd=fnum.format(Double.valueOf(rateStr));
                Log.e("保留2位小数", dd);
                return dd;
            } else if (rateStr.length() == (num + 2)) {
                return rateStr + "0";
            } else if (rateStr.length() == (num + 1)) {
                return rateStr + "00";
            }
        } else {
            if (rateStr.length() < 4) {
                return rateStr + "." + "00";
            } else {
                Toast.makeText(context, "身高或体重输入有误！", Toast.LENGTH_SHORT).show();
                return "0.00";
            }
        }
        return rateStr;
    }
}
