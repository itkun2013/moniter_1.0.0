package com.konsung.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.konsung.bean.Confine;
import com.konsung.bean.Dict;
import com.konsung.bean.DictAttr;
import com.konsung.bean.Sort;
import com.konsung.bean.SortAttr;

/**
 * 数据库DBHelper类
 * @author ouyangfan
 * @version 0.0.1
 *          2015-01-12 12:27
 */
public class ConfigDBHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your
    // app
    private static final String DATABASE_NAME = "DB_KonSung.db3";
    // any time you make changes to your database objects, you may have to increase the database
    // version
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<SortAttr, Integer> sortAttrDao;
    private RuntimeExceptionDao<DictAttr, Integer> dictAttrDao;
    private RuntimeExceptionDao<Sort, Integer> sortDao;
    private RuntimeExceptionDao<Dict, Integer> dictDao;
    private RuntimeExceptionDao<Confine, Integer> confineDao;

    /**
     * 构造方法
     * @param context 上下文
     */
    public ConfigDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * 创建Sqlite数据库
     * @param db
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        // 创建表
//        try {
////            TableUtils.createTable(connectionSource, Sort.class);
////            TableUtils.createTable(connectionSource, SortAttr.class);
////            TableUtils.createTable(connectionSource, Dict.class);
////            TableUtils.createTable(connectionSource, DictAttr.class);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    /*
     * 更新Sqlite数据库
     * 当应用程序升级，数据库改变时调用
     * @param db 数据库名
     * @param connectionSource 连接源
     * @param oldVersion 老版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
            int newVersion) {

    }

    /**
     * 返回数据库dao类
     * @return sortDao
     */
    public RuntimeExceptionDao<Sort, Integer> getSortDao() {
        if (sortDao == null) {
            sortDao = getRuntimeExceptionDao(Sort.class);
        }
        return sortDao;
    }

    /**
     * 返回数据库dao类
     * @return SortAttrDao
     */
    public RuntimeExceptionDao<SortAttr, Integer> getSortAttrDao() {
        if (sortAttrDao == null) {
            sortAttrDao = getRuntimeExceptionDao(SortAttr.class);
        }
        return sortAttrDao;
    }

    /**
     * 返回数据库dao类
     * @return DictDao
     */
    public RuntimeExceptionDao<Dict, Integer> getDictDao() {
        if (dictDao == null) {
            dictDao = getRuntimeExceptionDao(Dict.class);
        }
        return dictDao;
    }

    /**
     * 返回数据库dao类
     * @return DictAttrDao
     */
    public RuntimeExceptionDao<DictAttr, Integer> getDictAttrDao() {
        if (dictAttrDao == null) {
            dictAttrDao = getRuntimeExceptionDao(DictAttr.class);
        }
        return dictAttrDao;
    }

    /**
     * 返回数据库dao类
     * @return ConfineDao
     */
    public RuntimeExceptionDao<Confine, Integer> getConfineDao() {
        if (confineDao == null) {
            confineDao = getRuntimeExceptionDao(Confine.class);
        }
        return confineDao;
    }

    /**
     * 添加线
     * @param db 数据库
     * @param table 表
     * @param line 线
     * @param var 字符
     */
    private void addLine(SQLiteDatabase db, String table, String line, String var) {
        String sql = "alter table " + table + " " + "add " + line + " " + var;
        db.execSQL(sql);
    }

    /*
     * 关闭数据库
     */
    @Override
    public void close() {
        super.close();
    }
}