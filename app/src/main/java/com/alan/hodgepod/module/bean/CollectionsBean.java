package com.alan.hodgepod.module.bean;

/**
 * Created by Administrator on 16-6-5.
 */
public class CollectionsBean {

    //    String SQL_FIELD_DATAID = "data_id";
    //    String SQL_FIELD_TITLE = "title";
    //    String SQL_FIELD_FIRSTIMG = "firstImg";
    //    String SQL_FIELD_URL = "url";
    private int id;
    private String data_id;
    private String title;
    private String firstImg;
    private String url;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData_id() {
        return data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstImg() {
        return firstImg;
    }

    public void setFirstImg(String firstImg) {
        this.firstImg = firstImg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
