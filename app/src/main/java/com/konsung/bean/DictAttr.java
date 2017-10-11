package com.konsung.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chengminghui on 15/8/29.
 *
 */
@DatabaseTable(tableName = "SYS_DictAttr")
public class DictAttr {

    @DatabaseField(id = true)
    private int DictAttrID;

    @DatabaseField
    private String DictAttrName;

    @DatabaseField
    private String DictAttrValue;

    @DatabaseField(foreign = true,columnName = "DictID")
    private Dict dict;

    public int getDictAttrID() {
        return DictAttrID;
    }

    public void setDictAttrID(int dictAttrID) {
        DictAttrID = dictAttrID;
    }

    public String getDictAttrName() {
        return DictAttrName;
    }

    public void setDictAttrName(String dictAttrName) {
        DictAttrName = dictAttrName;
    }

    public String getDictAttrValue() {
        return DictAttrValue;
    }

    public void setDictAttrValue(String dictAttrValue) {
        DictAttrValue = dictAttrValue;
    }

    public Dict getDict() {
        return dict;
    }

    public void setDict(Dict dict) {
        this.dict = dict;
    }
}
