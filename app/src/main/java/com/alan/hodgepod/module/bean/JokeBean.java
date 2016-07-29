package com.alan.hodgepod.module.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-6-13.
 */
public class JokeBean {

    public int error_code;
    public String reason;
    public ResultBean result;

    public static class ResultBean {

        public ArrayList<DataBean> data;

        public static class DataBean {

            public String content;
            public String hashId;
            public long unixtime;
            public String updatetime;
        }
    }


}
