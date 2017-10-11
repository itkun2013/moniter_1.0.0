package com.konsung.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chengminghui on 15/8/30.
 */
@DatabaseTable(tableName = "SYS_Params")
public class Params {

    public static int SHOW_ALL = 0x00;
    public static int SHOW_NIBP = 0x01;

    @DatabaseField(id = true)
    private int ParaId;

    @DatabaseField
    private String ParaType;

    @DatabaseField
    private String ParaValue;

    @DatabaseField
    private String ParaName;

    @DatabaseField
    private int Sort;

    @DatabaseField
    private int Orderby;

    private int showType;

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getParaId() {
        return ParaId;
    }

    public void setParaId(int paraId) {
        ParaId = paraId;
    }

    public String getParaType() {
        return ParaType;
    }

    public void setParaType(String paraType) {
        ParaType = paraType;
    }

    public String getParaValue() {
        return ParaValue;
    }

    public void setParaValue(String paraValue) {
        ParaValue = paraValue;
    }

    public String getParaName() {
        return ParaName;
    }

    public void setParaName(String paraName) {
        ParaName = paraName;
    }

    public int getSort() {
        return Sort;
    }

    public void setSort(int sort) {
        Sort = sort;
    }

    public int getOrderby() {
        return Orderby;
    }

    public void setOrderby(int orderby) {
        Orderby = orderby;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null){
            return false;
        }
        if(o instanceof Params){
            Params p = (Params) o;
            if(ParaId == p.getParaId()){
                return true;
            }
        }
        return super.equals(o);
    }
}
