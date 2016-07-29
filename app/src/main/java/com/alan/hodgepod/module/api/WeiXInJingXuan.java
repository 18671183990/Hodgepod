package com.alan.hodgepod.module.api;

import com.alan.hodgepod.module.bean.WeiXinJingXuanBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 16-6-2.
 */
public interface WeiXInJingXuan {

    /**
     * @param key AppKey
     * @param pno 加载指定页
     * @return WeiXinJingXuanBean
     */
    @GET("query")
    Call<WeiXinJingXuanBean> getCall(@Query("key") String key, @Query("pno") int pno);

    /**
     * @param key AppKey
     * @param pno 加载指定页
     * @param ps  每页数据
     */
    @GET("query")
    Call<WeiXinJingXuanBean> getCall(@Query("key") String key, @Query("pno") int pno, @Query("ps") int ps);

}
