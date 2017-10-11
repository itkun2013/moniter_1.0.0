package com.konsung.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by chengminghui on 15/8/29.
 */
@DatabaseTable(tableName = "SYS_Dict")
public class Dict {

    @DatabaseField(id = true)
    private int DictID;

    @DatabaseField
    private String DictName;

    @ForeignCollectionField(eager = true)
    private Collection<DictAttr> dictAttrs;

    public int getDictID() {
        return DictID;
    }

    public void setDictID(int dictID) {
        DictID = dictID;
    }

    public String getDictName() {
        return DictName;
    }

    public void setDictName(String dictName) {
        DictName = dictName;
    }

    public Collection<DictAttr> getDictAttrs() {
        return dictAttrs;
    }

    public void setDictAttrs(Collection<DictAttr> dictAttrs) {
        this.dictAttrs = dictAttrs;
    }
}
