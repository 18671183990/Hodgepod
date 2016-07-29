package com.alan.hodgepod.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alan.hodgepod.R;
import com.alan.hodgepod.utils.ImageUtils;
import com.alan.hodgepod.view.fragments.JokeFragment;
import com.alan.hodgepod.view.fragments.WeatherFragment;
import com.alan.hodgepod.view.fragments.WeiXinFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Toolbar mToolbar;

    private DrawerLayout mDrawer;

    private NavigationView mNavigationView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MainViewPagerAdapter mMainViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

    }


    @SuppressWarnings("ConstantConditions")
    private void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        ImageView mNavIconIv = (ImageView) headerView.findViewById(R.id.main_nav_icon_iv);
        mNavIconIv.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

    }

    private void initData() {
        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mMainViewPagerAdapter.addFragment(new WeiXinFragment(), "微信精选");
        mMainViewPagerAdapter.addFragment(new JokeFragment(), "笑话大全");
        mMainViewPagerAdapter.addFragment(new WeatherFragment(), "天气预报");
        mViewPager.setAdapter(mMainViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            //TODO: 相机
        } else if (id == R.id.nav_gallery) {
            chiocePicFromAlbum();
        } else if (id == R.id.nav_collection) {
            showCollections();
        } else if (id == R.id.nav_tools) {
            //TODO: 工具
        } else if (id == R.id.nav_scan) {
            //TODO:扫一扫
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showCollections() {
        mDrawer.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, CollectionsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.transition_enter_pop_in, R.anim.transition_enter_pop_out);
            }
        }, 250);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        int id = v.getId();
        switch (id) {
            case R.id.main_nav_icon_iv:
                //TODO:     图像icon被点击了
                break;
        }
    }

    private void chiocePicFromAlbum() {
        //激活相册选择的意图
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            //开启activity,并设置requestCode为请求裁剪图片
            startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        }
    }

    private class MainViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private ArrayList<String> mTitleList = new ArrayList<>();

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        /**
         * @param fm
         * @param title
         */
        public void addFragment(Fragment fm, String title) {
            mFragmentList.add(fm);
            mTitleList.add(title);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}
