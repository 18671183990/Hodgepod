package com.alan.hodgepod;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alan.hodgepod.module.bean.CollectionsBean;
import com.alan.hodgepod.module.db.WeixinLikeDao;

import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    public void testDaoInsert() {
        WeixinLikeDao dao = new WeixinLikeDao(getContext());
        long insert = dao.insert("yi", "Hell word", "1.jpg", "www.baidu.com");
        assertEquals(1, insert);
    }

    public void testDaoQueryAll() {
        WeixinLikeDao dao = new WeixinLikeDao(getContext());
        ArrayList<CollectionsBean> list = dao.queryAll();
        assertEquals(list.size() > 2, true);
    }

    public void testDaoDeleteOne() {
        WeixinLikeDao dao = new WeixinLikeDao(getContext());
        int delete = dao.deleteOne("yi");
        assertEquals(1, delete);
    }

    public void testClearAll() {
        WeixinLikeDao dao = new WeixinLikeDao(getContext());
        int delete = dao.clearAll();
        assertEquals(true, delete > 0);
    }

    //    public void testJokeDaoInsert(){
    //        JokeDao dao = new JokeDao(getContext());
    //        long insert = dao.insertOne("haha", "sdfasdfasdfas", 123456789, "2016-6-17");
    //        assertEquals(1,insert);
    //    }

}