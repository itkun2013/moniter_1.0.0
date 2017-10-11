package com.konsung.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Cmad on 2016/1/8.
 *
 */
@DatabaseTable(tableName = "SYS_Confine")
public class Confine {

    @DatabaseField(id = true)
    private int ConfineID;

    @DatabaseField
    private String ConfineDes;

    @DatabaseField
    private int AttrID;

    @DatabaseField(foreign = true, columnName = "RelateAttrID")
    private SortAttr sortAttr;

    @DatabaseField
    private int Type;

    public int getConfineID() {
        return ConfineID;
    }

    public void setConfineID(int confineID) {
        ConfineID = confineID;
    }

    public String getConfineDes() {
        return ConfineDes;
    }

    public void setConfineDes(String confineDes) {
        ConfineDes = confineDes;
    }

    public int getAttrID() {
        return AttrID;
    }

    public void setAttrID(int attrID) {
        AttrID = attrID;
    }

    public SortAttr getSortAttr() {
        return sortAttr;
    }

    public void setSortAttr(SortAttr sortAttr) {
        this.sortAttr = sortAttr;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        this.Type = type;
    }
}
