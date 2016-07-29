package com.alan.hodgepod.module.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 16-6-17.
 */
public class JokeOpenHelper extends SQLiteOpenHelper {

    public JokeOpenHelper(Context context) {
        super(context, JokeDBConstants.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(JokeDBConstants.DB_SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    interface JokeDBConstants {

        String DB_NAME = "joke.db";
        String DB_TABLE_NAME = "joke";
        String DB_TABLE_FIELD_ID = "_id";
        String DB_TABLE_FIELD_CONTENT = "content";
        String DB_TABLE_FIELD_HASHID = "hashId";
        String DB_TABLE_FIELD_UNIXTIME = "unixtime";
        String DB_TABLE_FIELD_UPDATETIME = "updatetime";


        /**
         * 建表语句
         */
        String DB_SQL_CREATE_TABLE = "create table " + DB_TABLE_NAME + " ( " + DB_TABLE_FIELD_ID + " integer autoincrement, " + DB_TABLE_FIELD_CONTENT +
                " varchar(30), " + DB_TABLE_FIELD_HASHID + " varchar(30) primary key, " + DB_TABLE_FIELD_UNIXTIME + " integer, " +
                DB_TABLE_FIELD_UPDATETIME + " varchar(30)" + ");";

    }

}
