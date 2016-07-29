package com.alan.hodgepod.module.api;

import com.alan.hodgepod.module.bean.JokeBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 16-6-13.
 */
public interface XiaoHuaDaQuan {

    /**
     * 普通查询
     */
    @GET("list.from")
    Call<JokeBean> callNoLimit(@Query("sort") String sort, @Query("pagesize") int pageSize, @Query("time") String time, @Query("key") String key);


    /**
     * 分页查询
     */
    @GET("list.from")
    Call<JokeBean> callNeedLimit(@Query("sort") String sort,
                                 @Query("page") int page,
                                 @Query("pagesize") int pageSize,
                                 @Query("time") String time,
                                 @Query("key") String key);

    @GET("list.from")
    Call<ResponseBody> callNeedLimitBackString(@Query("sort") String sort,
                                               @Query("page") int page,
                                               @Query("pagesize") int pageSize,
                                               @Query("time") String time,
                                               @Query("key") String key);

}
