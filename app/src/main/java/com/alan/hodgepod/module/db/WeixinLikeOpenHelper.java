package com.alan.hodgepod.module.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 16-6-4.
 */
public class WeixinLikeOpenHelper extends SQLiteOpenHelper {


    public WeixinLikeOpenHelper(Context context) {
        super(context, DBConstants.SQL_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表语句,onCreate方法会执行一次,如果数据库存在就不再执行onCreate方法
        db.execSQL(DBConstants.SQL_CREATE_TAB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    interface DBConstants {

        String SQL_DB_NAME = "weixinlike.db";
        String SQL_TABLE_NAME = "like";

        String SQL_FIELD_ID = "_id";
        String SQL_FIELD_DATAID = "data_id";
        String SQL_FIELD_TITLE = "title";
        String SQL_FIELD_FIRSTIMG = "firstImg";
        String SQL_FIELD_URL = "url";

        String SQL_CREATE_TAB = "create table " + DBConstants.SQL_TABLE_NAME +
                "(" + SQL_FIELD_ID + " integer primary key autoincrement, " + SQL_FIELD_DATAID +
                " varchar(30) unique, " + SQL_FIELD_TITLE + " varchar(50), " + SQL_FIELD_FIRSTIMG + " varchar(50), " +
                SQL_FIELD_URL + " varchar(50)" + ");";

    }
}
