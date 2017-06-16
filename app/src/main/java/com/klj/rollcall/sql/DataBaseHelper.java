package com.klj.rollcall.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.klj.rollcall.utils.ConstantUtil;

/**
 * Created by 娱乐人物 on 2016/10/30.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private String userRole;

    private static final String DB_NAME = "rollcall.db";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_SC = "create table " + ConstantUtil.TABLE_SC + "(" +
            "_id integer primary key autoincrement,c_cno varchar(12),c_cname varchar(20)," +
            "c_year varchar(20),c_term varchar(10),c_week varchar(12)," +
            "c_weekday int, c_start int ,c_step int ,c_place varchar(20),c_score float," +
            "c_period int,c_dname varchar(12),c_class varchar(20),c_tname varchar(20))";

    private static final String SQL_CREATE_TC = "create table " + ConstantUtil.TABLE_TC + "(" +
            "_id integer primary key autoincrement,c_cno varchar(12),c_cname varchar(20)," +
            "c_year varchar(20),c_term varchar(10),c_week varchar(12)," +
            "c_weekday int, c_start int ,c_step int ,c_place varchar(20),c_score float," +
            "c_period int,c_dname varchar(12),c_class varchar(20),c_studentnum int)";
    private static final String SQL_CREATE_MAC= "create table " + ConstantUtil.TABLE_MAC + "(" +
            "_id integer primary key autoincrement,sno varchar(12),mac varchar(20) unique,className varchar(20))";
    public DataBaseHelper(Context context, String userRole) {
        super(context, DB_NAME, null, DB_VERSION);
        this.userRole = userRole;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        if (ConstantUtil.STUDENT.equals(userRole)) {
        db.execSQL(SQL_CREATE_SC);
//        } else if (ConstantUtil.TEACHER.equals(userRole)) {
        db.execSQL(SQL_CREATE_TC);
        db.execSQL(SQL_CREATE_MAC);
//        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 插入
     *
     * @param table
     * @param values
     * @return
     */
    public long insert(String table, ContentValues values) {
        long insert = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            insert = sqLiteDatabase.insert(table, null, values);
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        } finally {
            sqLiteDatabase.close();
        }
        return insert;
    }

    /**
     * 删除
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public boolean delete(String table, String whereClause, String[] whereArgs) {
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            int delete = sqLiteDatabase.delete(table, whereClause, whereArgs);
            flag = delete > 0;
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        } finally {
            sqLiteDatabase.close();
        }
        return flag;
    }

    /**
     * 修改
     *
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public boolean update(String table, ContentValues values, String whereClause,
                          String[] whereArgs) {
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            int update = sqLiteDatabase.update(table, values, whereClause, whereArgs);
            flag = update > 0;
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        } finally {
            sqLiteDatabase.close();
        }
        return flag;
    }

    /**
     * 查询
     *
     * @param table
     * @param selection
     * @param selectionArgs
     * @return
     */
    public Cursor query(String table, String selection, String[] selectionArgs, String orderBy) {
        Cursor query = null;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            query = sqLiteDatabase.query(table, null, selection, selectionArgs, null, null, orderBy);
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        } finally {
//            sqLiteDatabase.close();
        }
        return query;
    }
}
