package com.alan.hodgepod.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alan.hodgepod.R;
import com.alan.hodgepod.module.api.TianQiApi;
import com.alan.hodgepod.module.bean.WeatherInfoBean;
import com.alan.hodgepod.utils.ToastUtils;
import com.alan.hodgepod.view.activity.CityPickerActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 16-5-31.
 */
public class WeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = "WeatherFragment";
    private static final String APP_KEY = "a0c40e46750fc5024ab0007b25bf6f43";
    private String mCurrentCityName = "十堰";

    @BindView(R.id.weather_root_swipelayout)
    SwipeRefreshLayout mSwipeLayout;

    @BindView(R.id.weather_root_tv_cityname)
    TextView mCityNameTv;
    @BindView(R.id.weather_root_tv_realtime_today)
    TextView mRealtimeDateTV;
    @BindView(R.id.weather_root_tv_realtime_nongli)
    TextView mRealtimeNongliTV;
    @BindView(R.id.weather_root_tv_realtime_weekly)
    TextView mRealtimeWeeklyTV;
    @BindView(R.id.weather_root_tv_realtime_weather)
    TextView mRealtimeWeatherTv;
    @BindView(R.id.weather_root_tv_realtime_temperature)
    TextView mRealtimeTemperatureTv;
    @BindView(R.id.weather_root_tv_realtime_fengli)
    TextView mRealtimeFengliTv;
    @BindView(R.id.weather_root_tv_realtime_fengli_level)
    TextView mRealtimeFengliLevelTv;
    @BindView(R.id.weather_root_tv_realtime_updatetime)
    TextView mRealtimeUpdateTimeTv;


    @BindView(R.id.weather_root_tv_future_date0)
    TextView mFutureDate0Tv;
    @BindView(R.id.weather_root_tv_future_date1)
    TextView mFutureDate1Tv;
    @BindView(R.id.weather_root_tv_future_date2)
    TextView mFutureDate2Tv;
    @BindView(R.id.weather_root_tv_future_date3)
    TextView mFutureDate3Tv;
    @BindView(R.id.weather_root_tv_future_date4)
    TextView mFutureDate4Tv;
    @BindView(R.id.weather_root_tv_future_date5)
    TextView mFutureDate5Tv;
    @BindView(R.id.weather_root_tv_future_date6)
    TextView mFutureDate6Tv;

    @BindView(R.id.weather_root_tv_future_nongli0)
    TextView mFutureNongli0Tv;
    @BindView(R.id.weather_root_tv_future_nongli1)
    TextView mFutureNongli1Tv;
    @BindView(R.id.weather_root_tv_future_nongli2)
    TextView mFutureNongli2Tv;
    @BindView(R.id.weather_root_tv_future_nongli3)
    TextView mFutureNongli3Tv;
    @BindView(R.id.weather_root_tv_future_nongli4)
    TextView mFutureNongli4Tv;
    @BindView(R.id.weather_root_tv_future_nongli5)
    TextView mFutureNongli5Tv;
    @BindView(R.id.weather_root_tv_future_nongli6)
    TextView mFutureNongli6Tv;


    @BindView(R.id.weather_root_tv_future_week0)
    TextView mFutureWeek0Tv;
    @BindView(R.id.weather_root_tv_future_week1)
    TextView mFutureWeek1Tv;
    @BindView(R.id.weather_root_tv_future_week2)
    TextView mFutureWeek2Tv;
    @BindView(R.id.weather_root_tv_future_week3)
    TextView mFutureWeek3Tv;
    @BindView(R.id.weather_root_tv_future_week4)
    TextView mFutureWeek4Tv;
    @BindView(R.id.weather_root_tv_future_week5)
    TextView mFutureWeek5Tv;
    @BindView(R.id.weather_root_tv_future_week6)
    TextView mFutureWeek6Tv;


    @BindView(R.id.weather_root_tv_future_weather0)
    TextView mFutureWeather0Tv;
    @BindView(R.id.weather_root_tv_future_weather1)
    TextView mFutureWeather1Tv;
    @BindView(R.id.weather_root_tv_future_weather2)
    TextView mFutureWeather2Tv;
    @BindView(R.id.weather_root_tv_future_weather3)
    TextView mFutureWeather3Tv;
    @BindView(R.id.weather_root_tv_future_weather4)
    TextView mFutureWeather4Tv;
    @BindView(R.id.weather_root_tv_future_weather5)
    TextView mFutureWeather5Tv;
    @BindView(R.id.weather_root_tv_future_weather6)
    TextView mFutureWeather6Tv;

    @BindView(R.id.weather_root_tv_future_temperature0)
    TextView mFutureTemperature0Tv;
    @BindView(R.id.weather_root_tv_future_temperature1)
    TextView mFutureTemperature1Tv;
    @BindView(R.id.weather_root_tv_future_temperature2)
    TextView mFutureTemperature2Tv;
    @BindView(R.id.weather_root_tv_future_temperature3)
    TextView mFutureTemperature3Tv;
    @BindView(R.id.weather_root_tv_future_temperature4)
    TextView mFutureTemperature4Tv;
    @BindView(R.id.weather_root_tv_future_temperature5)
    TextView mFutureTemperature5Tv;
    @BindView(R.id.weather_root_tv_future_temperature6)
    TextView mFutureTemperature6Tv;

    @BindView(R.id.weather_root_tv_future_fengli0)
    TextView mFutureFengli0Tv;
    @BindView(R.id.weather_root_tv_future_fengli1)
    TextView mFutureFengli1Tv;
    @BindView(R.id.weather_root_tv_future_fengli2)
    TextView mFutureFengli2Tv;
    @BindView(R.id.weather_root_tv_future_fengli3)
    TextView mFutureFengli3Tv;
    @BindView(R.id.weather_root_tv_future_fengli4)
    TextView mFutureFengli4Tv;
    @BindView(R.id.weather_root_tv_future_fengli5)
    TextView mFutureFengli5Tv;
    @BindView(R.id.weather_root_tv_future_fengli6)
    TextView mFutureFengli6Tv;

    @BindView(R.id.weather_root_linear_container)
    View mLinearContainer;


    private WeatherInfoBean mWeatherInfoBean;
    private View mRootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_weather_root, container, false);
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
        initData(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    private void initView() {
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void initData(String city) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(TianQiApi.BaseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        @SuppressWarnings("SpellCheckingInspection") TianQiApi tianQiApi = retrofit.create(TianQiApi.class);
        Call<WeatherInfoBean> call = null;
        if (TextUtils.isEmpty(city)) {

            call = tianQiApi.getWeatherInfo(mCurrentCityName, APP_KEY);
        } else {
            call = tianQiApi.getWeatherInfo(city, APP_KEY);
        }
        call.enqueue(new Callback<WeatherInfoBean>() {
            @Override
            public void onResponse(Call<WeatherInfoBean> call, Response<WeatherInfoBean> response) {
                mWeatherInfoBean = response.body();
                if (mWeatherInfoBean == null) {
                    return;
                }
                setDisplayInfo();
            }

            @Override
            public void onFailure(Call<WeatherInfoBean> call, Throwable t) {

            }
        });
    }

    /**
     * 设置界面上的信息
     */
    private void setDisplayInfo() {

        mCityNameTv.setText(mWeatherInfoBean.result.data.realtime.city_name);
        mRealtimeDateTV.setText(mWeatherInfoBean.result.data.realtime.date);
        mRealtimeNongliTV.setText(mWeatherInfoBean.result.data.realtime.moon);
        mRealtimeWeeklyTV.setText("星期" + mWeatherInfoBean.result.data.realtime.week);
        mRealtimeWeatherTv.setText(mWeatherInfoBean.result.data.realtime.weather.info);
        mRealtimeTemperatureTv.setText(mWeatherInfoBean.result.data.realtime.weather.temperature + "°");
        mRealtimeFengliTv.setText(mWeatherInfoBean.result.data.realtime.wind.direct);
        mRealtimeFengliLevelTv.setText(mWeatherInfoBean.result.data.realtime.wind.power);

        long dataUptime = mWeatherInfoBean.result.data.realtime.dataUptime;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd日hh:mm");
        String date = sdf.format(new Date(dataUptime * 1000));
        mRealtimeUpdateTimeTv.setText(date);

        //未来7天天气
        List<WeatherInfoBean.ResultBean.DataBean.WeatherBean> weather = mWeatherInfoBean.result.data.weather;
        mFutureDate0Tv.setText(weather.get(0).date.substring(6, mWeatherInfoBean.result.data.weather.get(0).date.length()));
        mFutureDate1Tv.setText(weather.get(1).date.substring(6, mWeatherInfoBean.result.data.weather.get(1).date.length()));
        mFutureDate2Tv.setText(weather.get(2).date.substring(6, mWeatherInfoBean.result.data.weather.get(2).date.length()));
        mFutureDate3Tv.setText(weather.get(3).date.substring(6, mWeatherInfoBean.result.data.weather.get(3).date.length()));
        mFutureDate4Tv.setText(weather.get(4).date.substring(6, mWeatherInfoBean.result.data.weather.get(4).date.length()));
        mFutureDate5Tv.setText(weather.get(5).date.substring(6, mWeatherInfoBean.result.data.weather.get(5).date.length()));
        mFutureDate6Tv.setText(weather.get(6).date.substring(6, mWeatherInfoBean.result.data.weather.get(6).date.length()));

        //农历
        mFutureNongli0Tv.setText(weather.get(0).nongli);
        mFutureNongli1Tv.setText(weather.get(1).nongli);
        mFutureNongli2Tv.setText(weather.get(2).nongli);
        mFutureNongli3Tv.setText(weather.get(3).nongli);
        mFutureNongli4Tv.setText(weather.get(4).nongli);
        mFutureNongli5Tv.setText(weather.get(5).nongli);
        mFutureNongli6Tv.setText(weather.get(6).nongli);

        //星期
        mFutureWeek0Tv.setText("周" + weather.get(0).week);
        mFutureWeek1Tv.setText("周" + weather.get(1).week);
        mFutureWeek2Tv.setText("周" + weather.get(2).week);
        mFutureWeek3Tv.setText("周" + weather.get(3).week);
        mFutureWeek4Tv.setText("周" + weather.get(4).week);
        mFutureWeek5Tv.setText("周" + weather.get(5).week);
        mFutureWeek6Tv.setText("周" + weather.get(6).week);

        //天气
        mFutureWeather0Tv.setText(weather.get(0).info.day.get(1));
        mFutureWeather1Tv.setText(weather.get(1).info.day.get(1));
        mFutureWeather2Tv.setText(weather.get(2).info.day.get(1));
        mFutureWeather3Tv.setText(weather.get(3).info.day.get(1));
        mFutureWeather4Tv.setText(weather.get(4).info.day.get(1));
        mFutureWeather5Tv.setText(weather.get(5).info.day.get(1));
        mFutureWeather6Tv.setText(weather.get(6).info.day.get(1));

        //温度
        mFutureTemperature0Tv.setText(weather.get(0).info.day.get(2) + "°");
        mFutureTemperature1Tv.setText(weather.get(1).info.day.get(2) + "°");
        mFutureTemperature2Tv.setText(weather.get(2).info.day.get(2) + "°");
        mFutureTemperature3Tv.setText(weather.get(3).info.day.get(2) + "°");
        mFutureTemperature4Tv.setText(weather.get(4).info.day.get(2) + "°");
        mFutureTemperature5Tv.setText(weather.get(5).info.day.get(2) + "°");
        mFutureTemperature6Tv.setText(weather.get(6).info.day.get(2) + "°");

        //风力
        mFutureFengli0Tv.setText(weather.get(0).info.day.get(4));
        mFutureFengli1Tv.setText(weather.get(1).info.day.get(4));
        mFutureFengli2Tv.setText(weather.get(2).info.day.get(4));
        mFutureFengli3Tv.setText(weather.get(3).info.day.get(4));
        mFutureFengli4Tv.setText(weather.get(4).info.day.get(4));
        mFutureFengli5Tv.setText(weather.get(5).info.day.get(4));
        mFutureFengli6Tv.setText(weather.get(6).info.day.get(4));
    }

    private void initEvent() {
        mSwipeLayout.setOnRefreshListener(this);
        mCityNameTv.setOnClickListener(this);
    }

    /**
     * 自动刷新
     */
    private void autoRefresh() {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
                mLinearContainer.setVisibility(View.GONE);
            }
        });

        mSwipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
                mLinearContainer.setVisibility(View.VISIBLE);
                ToastUtils.showToast(getActivity(), "刷新完成");
            }
        }, 1300);
    }


    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(true);
        mLinearContainer.setVisibility(View.GONE);
        mSwipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
                initData(mCurrentCityName);
                mLinearContainer.setVisibility(View.VISIBLE);
            }
        }, 1500);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.weather_root_tv_cityname:
                citySelect();
                break;
        }
    }

    private void citySelect() {
        //TODO: 弹出城市选择界面
        //        startActivity(new Intent(getActivity(), CityPickerActivity.class));
        startActivityForResult(new Intent(getActivity(), CityPickerActivity.class), CityPickerActivity.REQUEST_CODE_PICK_CITY);
        getActivity().overridePendingTransition(R.anim.transition_enter_pop_in, R.anim.transition_enter_pop_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CityPickerActivity.REQUEST_CODE_PICK_CITY) {
            return;
        }
        if (resultCode == CityPickerActivity.RESULT_OK) {
            String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
            mCurrentCityName = city;
            initData(city);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        autoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
