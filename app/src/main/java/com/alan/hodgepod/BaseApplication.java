package com.alan.hodgepod;

import android.app.Application;
import android.content.Context;

import com.umeng.socialize.PlatformConfig;

/**
 * Created by Administrator on 16-6-12.
 */
public class BaseApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initUmeng();
    }

    private void initUmeng() {
        PlatformConfig.setWeixin("wxa218c6967c859e4d", "f3cd29f1c34e2228b6d93577fa911140");
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo("2161047562", "061f8464e53ff6cb99139474f5449cfb");
        //新浪微博 appkey appsecret

        //        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        //        // QQ和Qzone appid appkey
        //        PlatformConfig.setAlipay("2015111700822536");
        //        //支付宝 appid
        //        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
        //        //易信 appkey
        //        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
        //        //Twitter appid appkey
        //        PlatformConfig.setPinterest("1439206");
        //        //Pinterest appid
        //        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
        //        //来往 appid appkey
    }
}
