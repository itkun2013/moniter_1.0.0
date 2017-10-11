package com.konsung.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by chengminghui on 15/8/29.
 * 菜单类别
 */
@DatabaseTable(tableName = "SYS_Sort")
public class Sort {
    @DatabaseField(id = true)
    private int SortID;

    @DatabaseField
    private String SortName;

    @DatabaseField(foreign = true,columnName = "ParentID")
    private Sort parent;

    @DatabaseField
    private int Orderby;

    @DatabaseField
    private String CreateTime;

    @DatabaseField
    private int OrderID;

    @ForeignCollectionField(eager = true)
    private Collection<Sort> sorts;

    @ForeignCollectionField(eager = true)
    private Collection<SortAttr> sortAttrs;

    private boolean isSecondMenu;

    public boolean isSecondMenu() {
        return isSecondMenu;
    }

    public void setIsSecondMenu(boolean isSecondMenu) {
        this.isSecondMenu = isSecondMenu;
    }

    public Collection<SortAttr> getSortAttrs() {
        return sortAttrs;
    }

    public void setSortAttrs(Collection<SortAttr> sortAttrs) {
        this.sortAttrs = sortAttrs;
    }

    public int getSortID() {
        return SortID;
    }

    public void setSortID(int sortID) {
        SortID = sortID;
    }

    public String getSortName() {
        return SortName;
    }

    public void setSortName(String sortName) {
        SortName = sortName;
    }

    public Sort getParent() {
        return parent;
    }

    public void setParent(Sort parent) {
        this.parent = parent;
    }

    public int getOrderby() {
        return Orderby;
    }

    public void setOrderby(int orderby) {
        Orderby = orderby;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public Collection<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(Collection<Sort> sorts) {
        this.sorts = sorts;
    }
}
