package com.alan.hodgepod.module.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alan.hodgepod.module.bean.JokeBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-6-17.
 */
public class JokeDao {

    private JokeOpenHelper mJokeOpenHelper;

    public JokeDao(Context context) {
        this.mJokeOpenHelper = new JokeOpenHelper(context);
    }

    /**
     * 添加一条数据
     */
    public long insertOne(String content, String hashId, long unixtime, String updatetime) {

        SQLiteDatabase db = mJokeOpenHelper.getWritableDatabase();

        String tableName = JokeOpenHelper.JokeDBConstants.DB_TABLE_NAME;
        ContentValues values = new ContentValues();
        values.put(JokeOpenHelper.JokeDBConstants.DB_TABLE_FIELD_CONTENT, content);
        values.put(JokeOpenHelper.JokeDBConstants.DB_TABLE_FIELD_HASHID, hashId);
        values.put(JokeOpenHelper.JokeDBConstants.DB_TABLE_FIELD_UNIXTIME, unixtime);
        values.put(JokeOpenHelper.JokeDBConstants.DB_TABLE_FIELD_UPDATETIME, updatetime);

        long insert = db.insert(tableName, null, values);
        db.close();
        return insert;
    }


    /**
     * 查询所有数据
     */
    public ArrayList<JokeBean.ResultBean.DataBean> queryAll() {
        SQLiteDatabase db = mJokeOpenHelper.getReadableDatabase();

        ArrayList<JokeBean.ResultBean.DataBean> list = new ArrayList<>();

        String table = JokeOpenHelper.JokeDBConstants.DB_TABLE_NAME;
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        while (cursor.moveToNext()) {

            JokeBean.ResultBean.DataBean dataBean = new JokeBean.ResultBean.DataBean();

            String content = cursor.getString(1);
            String hashId = cursor.getString(2);
            long unixtime = cursor.getInt(3);
            String updateTime = cursor.getString(4);

            dataBean.content = content;
            dataBean.hashId = hashId;
            dataBean.unixtime = unixtime;
            dataBean.updatetime = updateTime;

            list.add(dataBean);
        }

        cursor.close();
        db.close();

        return list;
    }

}
