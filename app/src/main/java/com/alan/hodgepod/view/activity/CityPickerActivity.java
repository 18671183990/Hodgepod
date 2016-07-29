package com.alan.hodgepod.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alan.hodgepod.BaseApplication;
import com.alan.hodgepod.R;
import com.alan.hodgepod.module.bean.City;
import com.alan.hodgepod.module.bean.LocateState;
import com.alan.hodgepod.module.db.DBManager;
import com.alan.hodgepod.utils.ToastUtils;
import com.alan.hodgepod.view.SideLetterBar;
import com.alan.hodgepod.view.adapter.CityListAdapter;
import com.alan.hodgepod.view.adapter.ResultListAdapter;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class CityPickerActivity extends AppCompatActivity
        implements View.OnClickListener, CityListAdapter.OnCityClickListener, SideLetterBar.OnLetterChangedListener, TextWatcher, AdapterView.OnItemClickListener {

    public static final int REQUEST_CODE_PICK_CITY = 2333;
    public static final String KEY_PICKED_CITY = "picked_city";
    private static final String TAG = CityPickerActivity.class.getSimpleName();


    private ListView mListView;             //显示城市的ListView
    private ListView mResultListView;   //显示结果的ListView
    private SideLetterBar mLetterBar;   //右侧导航的View
    private EditText mSearchBox;
    private ImageView mClearBtn;             //清空的ImageView
    private ImageView mBackBtn;              //返回的ImageView
    private ViewGroup mEmptyView;        //空的ViewGroup

    private CityListAdapter mCityAdapter;        //正常的Adapter
    private ResultListAdapter mResultAdapter;   //显示结果Adapter
    private List<City> mAllCities;
    private DBManager mDBManager;                    //数据库管理类

    private LocationClient mBaiduLocationClient = null;
    private BDLocationListener mBaiDuLocationListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        initView();
        initData();
        initBaiDuLocation();
        initEvent();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_all_city);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        TextView mOverlayTv = (TextView) findViewById(R.id.tv_letter_overlay);
        mLetterBar = (SideLetterBar) findViewById(R.id.side_letter_bar);
        assert mLetterBar != null;
        mLetterBar.setOverlay(mOverlayTv);
        mSearchBox = (EditText) findViewById(R.id.et_search);
        mEmptyView = (ViewGroup) findViewById(R.id.empty_view);
        mClearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        mBackBtn = (ImageView) findViewById(R.id.back);
    }

    private void initData() {
        mDBManager = new DBManager(this);
        mDBManager.copyDBFile();
        mAllCities = mDBManager.getAllCities();
        mCityAdapter = new CityListAdapter(this, mAllCities);
        mListView.setAdapter(mCityAdapter);

        mResultAdapter = new ResultListAdapter(this, null);
        mResultListView.setAdapter(mResultAdapter);
    }


    private void initEvent() {
        mLetterBar.setOnLetterChangedListener(this);
        mSearchBox.addTextChangedListener(this);
        mResultListView.setOnItemClickListener(this);
        mClearBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mCityAdapter.setOnCityClickListener(this);
    }

    private void back(String city) {
        ToastUtils.showToast(this, "点击的城市：" + city);
        Intent data = new Intent();
        data.putExtra(KEY_PICKED_CITY, city);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_clear:
                mSearchBox.setText("");
                mClearBtn.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mResultListView.setVisibility(View.GONE);
                break;
            case R.id.back:
                finish();
                break;
        }
    }


    @Override
    public void onCityClick(String name) {
        back(name);
    }

    @Override
    public void onLocateClick() {
        mCityAdapter.updateLocateState(LocateState.LOCATING, null);
        mBaiduLocationClient.start();
    }


    @Override
    public void onLetterChanged(String letter) {
        int position = mCityAdapter.getLetterPosition(letter);
        mListView.setSelection(position);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResultListView.setVisibility(View.GONE);
        } else {
            mClearBtn.setVisibility(View.VISIBLE);
            mResultListView.setVisibility(View.VISIBLE);
            List<City> result = mDBManager.searchCity(keyword);
            if (result == null || result.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mResultAdapter.changeData(result);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        back(mResultAdapter.getItem(position).getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduLocationClient.stop();
    }

    private void initBaiDuLocation() {
        mBaiduLocationClient = new LocationClient(BaseApplication.mContext);     //声明LocationClient类
        mBaiduLocationClient.registerLocationListener(mBaiDuLocationListener);    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        //        int span = 1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mBaiduLocationClient.setLocOption(option);

        mBaiduLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            //            //Receive Location
            //            StringBuffer sb = new StringBuffer(256);
            //            sb.append("time : ");
            //            sb.append(location.getTime());
            //            sb.append("\nerror code : ");
            //            sb.append(location.getLocType());
            //            sb.append("\nlatitude : ");
            //            sb.append(location.getLatitude());
            //            sb.append("\nlontitude : ");
            //            sb.append(location.getLongitude());
            //            sb.append("\nradius : ");
            //            sb.append(location.getRadius());
            //            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            //                sb.append("\nspeed : ");
            //                sb.append(location.getSpeed());// 单位：公里每小时
            //                sb.append("\nsatellite : ");
            //                sb.append(location.getSatelliteNumber());
            //                sb.append("\nheight : ");
            //                sb.append(location.getAltitude());// 单位：米
            //                sb.append("\ndirection : ");
            //                sb.append(location.getDirection());// 单位度
            //                sb.append("\naddr : ");
            //                sb.append(location.getAddrStr());
            //                sb.append("\ndescribe : ");
            //                sb.append("gps定位成功");
            //
            //            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            //                sb.append("\naddr : ");
            //                sb.append(location.getAddrStr());
            //                //运营商信息
            //                sb.append("\noperationers : ");
            //                sb.append(location.getOperators());
            //                sb.append("\ndescribe : ");
            //                sb.append("网络定位成功");
            //            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            //                sb.append("\ndescribe : ");
            //                sb.append("离线定位成功，离线定位结果也是有效的");
            //            } else if (location.getLocType() == BDLocation.TypeServerError) {
            //                sb.append("\ndescribe : ");
            //                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            //            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            //                sb.append("\ndescribe : ");
            //                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            //            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            //                sb.append("\ndescribe : ");
            //                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            //            }
            //            sb.append("\nlocationdescribe : ");
            //            sb.append(location.getLocationDescribe());// 位置语义化信息
            //            List<Poi> list = location.getPoiList();// POI数据
            //            if (list != null) {
            //                sb.append("\npoilist size = : ");
            //                sb.append(list.size());
            //                for (Poi p : list) {
            //                    sb.append("\npoi= : ");
            //                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            //                }
            //            }
            //            Log.i("BaiduLocationApiDem", sb.toString());
            String city = location.getCity().substring(0, 2);
            Log.d(TAG, "onReceiveLocation: " + city);
            mCityAdapter.updateLocateState(LocateState.SUCCESS, city);
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.transition_exit_pop_in, R.anim.transition_exit_pop_out);
    }
}
