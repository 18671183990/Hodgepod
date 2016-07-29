package com.alan.hodgepod.module.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alan.hodgepod.module.bean.CollectionsBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-6-4. 用来实现增删改查操作
 */
public class WeixinLikeDao {

    private static final String TAG = "WeixinLikeDao";
    private WeixinLikeOpenHelper mDBHelper;

    public WeixinLikeDao(Context context) {
        mDBHelper = new WeixinLikeOpenHelper(context);
    }

    /**
     * 添加记录的方法
     *
     * @param dataId   收藏的唯一ID
     * @param title    title
     * @param firstImg 图片地址
     * @param url      详细信息页面
     * @return 返回-1表明添加失败
     */
    public long insert(String dataId, String title, String firstImg, String url) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        //insert into exam values(null,'关羽',85,76,70);  //如果插入全部内容,插入的key可以忽略

        ContentValues values = new ContentValues();
        values.put(WeixinLikeOpenHelper.DBConstants.SQL_FIELD_DATAID, dataId);
        values.put(WeixinLikeOpenHelper.DBConstants.SQL_FIELD_TITLE, title);
        values.put(WeixinLikeOpenHelper.DBConstants.SQL_FIELD_FIRSTIMG, firstImg);
        values.put(WeixinLikeOpenHelper.DBConstants.SQL_FIELD_URL, url);

        long insert = db.insert(WeixinLikeOpenHelper.DBConstants.SQL_TABLE_NAME, null, values);
        db.close();
        return insert;
    }

    public ArrayList<CollectionsBean> queryAll() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ArrayList<CollectionsBean> list = new ArrayList<>();
        //serlet * frome exam where dataid= ?
        //select name,url from exam where name='liwei' and math>100;

        String table = WeixinLikeOpenHelper.DBConstants.SQL_TABLE_NAME;
        String[] columns = null;     //查询需要返回的列,null返回所有,   columns??列
        String selection = null;     //查询条件(where id = ?) null表示查询所有
        String[] selectionArgs = null;   //需要查询的实际参数
        String groupBy = null;
        String having = null;
        String orderBy = null;
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dataId = cursor.getString(1);
            String title = cursor.getString(2);
            String firstImg = cursor.getString(3);
            String url = cursor.getString(4);

            CollectionsBean bean = new CollectionsBean();
            bean.setId(id);
            bean.setData_id(dataId);
            bean.setTitle(title);
            bean.setFirstImg(firstImg);
            bean.setUrl(url);
            list.add(bean);
        }
        cursor.close();
        db.close();

        return list;
    }

    /**
     * 根据指定的data_id删除数据
     */
    public int deleteOne(String data_id) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String whereClause = WeixinLikeOpenHelper.DBConstants.SQL_FIELD_DATAID + " =? ";
        String[] whereArgs = new String[]{data_id};
        int delete = db.delete(WeixinLikeOpenHelper.DBConstants.SQL_TABLE_NAME, whereClause, whereArgs);
        db.close();
        return delete;

    }

    /**
     * 清空所有收藏
     */
    public int clearAll() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int delete = db.delete(WeixinLikeOpenHelper.DBConstants.SQL_TABLE_NAME, null, null);
        db.close();
        return delete;
    }
}
