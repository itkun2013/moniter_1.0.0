package com.konsung.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 */
@DatabaseTable(tableName = "SYS_SortAttr")
public class SortAttr {

    @DatabaseField(id = true)
    private int AttrID;

    @DatabaseField(foreign = true,columnName = "SortID")
    private Sort sort;

    @DatabaseField
    private String AttrName;

    @DatabaseField
    private int ShowType;

    @DatabaseField
    private int DataType;

    @DatabaseField
    private String Unit;

    @DatabaseField
    private boolean IsAppDevice;

    @DatabaseField
    private int OrderType;

    @DatabaseField
    private String AttrValue;

    @DatabaseField(foreign = true,columnName = "DictID")
    private Dict dict;

    private DictAttr attr;

    public DictAttr getAttr() {
        return attr;
    }

    public void setAttr(DictAttr attr) {
        this.attr = attr;
    }

    public int getOrderType() {
        return OrderType;
    }

    public void setOrderType(int orderType) {
        OrderType = orderType;
    }

    public int getAttrID() {
        return AttrID;
    }

    public void setAttrID(int attrID) {
        AttrID = attrID;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public String getAttrName() {
        return AttrName;
    }

    public void setAttrName(String attrName) {
        AttrName = attrName;
    }

    public int getShowType() {
        return ShowType;
    }

    public void setShowType(int showType) {
        ShowType = showType;
    }

    public int getDataType() {
        return DataType;
    }

    public void setDataType(int dataType) {
        DataType = dataType;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getAttrValue() {
        return AttrValue;
    }

    public void setAttrValue(String attrValue) {
        AttrValue = attrValue;
    }

    public Dict getDict() {
        return dict;
    }

    public void setDict(Dict dict) {
        this.dict = dict;
    }

    public boolean isAppDevice() {
        return IsAppDevice;
    }

    public void setIsAppDevice(boolean isAppDevice) {
        IsAppDevice = isAppDevice;
    }
}
