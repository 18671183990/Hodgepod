package com.alan.hodgepod.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.hodgepod.R;
import com.alan.hodgepod.module.api.XiaoHuaDaQuan;
import com.alan.hodgepod.module.bean.JokeBean;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 16-5-31. 功能: 笑话大全的Fragment
 */
public class JokeFragment extends Fragment implements View.OnFocusChangeListener, View.OnTouchListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String JOKE_BASE_URL = "http://japi.juhe.cn/joke/content/";
    public static final String JOKE_REQUEST_TYPE_SORT_DESC = "desc"; //指定时间之前发布
    public static final String JOKE_REQUEST_TYPE_SORT_ASC = "asc";       //指定时间之后发布
    public static final String JOKE_REQUEST_KEY = "133c8bf3f102322b3f5fbb99ed992cb2";       //KEY

    private static final String TAG = "JokeFragment";
    private int mCurrentPage;           //当前加载页

    private View mRootView;
    private ListView mListView;
    private EditText mEditText;
    private RelativeLayout mInputRl;
    private Button mSendButton;

    private InputMethodManager mIm;

    private JokeBean mJokeBean;
    private ArrayList<JokeBean.ResultBean.DataBean> mList;
    private JokeListAdapter mJokeListAdapter;

    private int[] icons = new int[]{R.mipmap.mm1,
            R.mipmap.mm2,
            R.mipmap.mm3,
            R.mipmap.mm4,
            R.mipmap.mm5,
            R.mipmap.mm6,
            R.mipmap.mm7,
            R.mipmap.mm8,
            R.mipmap.mm9,
            R.mipmap.mm10,
            R.mipmap.mm11,
            R.mipmap.mm12,
            R.mipmap.mm13};
    private SwipeRefreshLayout mSwipeLayout;
    private FloatingActionButton mRefreshFabBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO:
        mRootView = inflater.inflate(R.layout.fragment_joke_root, container, false);
        return mRootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {

        //输入法管理器
        mIm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mListView = (ListView) mRootView.findViewById(R.id.joke_listview);
        mListView.setOnTouchListener(this);

        mInputRl = (RelativeLayout) mRootView.findViewById(R.id.joke_input_rl);
        mEditText = (EditText) mRootView.findViewById(R.id.joke_edittext);

        //为EditText设置焦点监听,当EditText失去焦点后 隐藏评论框
        mEditText.setOnFocusChangeListener(this);
        mSendButton = (Button) mRootView.findViewById(R.id.joke_btn_send);

        mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.joke_root_swipelayout);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mRefreshFabBtn = (FloatingActionButton) mRootView.findViewById(R.id.joke_fbcbtn_refresh);
        autoRefresh();
    }

    /**
     * 创建Fragment会执行自动刷新效果
     */
    private void autoRefresh() {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);

            }
        });

        mSwipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);

            }
        }, 1300);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();

        initEvent();


    }

    private void initEvent() {
        mSwipeLayout.setOnRefreshListener(this);
        mRefreshFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoRefresh();
                initData();
            }
        });
    }

    private void initData() {
        //获得当前时间戳
        String currentTime = Long.toString(System.currentTimeMillis()).substring(0, 10);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(JOKE_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        //获得接口的实例
        XiaoHuaDaQuan xiaoHua = retrofit.create(XiaoHuaDaQuan.class);

        Call<JokeBean> call = xiaoHua.callNeedLimit(JOKE_REQUEST_TYPE_SORT_DESC, mCurrentPage, 10, currentTime, JOKE_REQUEST_KEY);

        call.enqueue(new Callback<JokeBean>() {
            @Override
            public void onResponse(Call<JokeBean> call, Response<JokeBean> response) {

                mJokeBean = response.body();
                /**
                 * 下拉刷新是从网络拉取新的数据,加载更多是从缓存中获取数据
                 */
                //把获取到数据存储到数据库/或者存储到文件,
                //1.创建数据库
                //从数据库中查找数据,然后分批加载
                if (mJokeBean == null) {
                    return;
                }
                if (mJokeBean.error_code != 0) {
                    return;
                }
                mList = mJokeBean.result.data;
                if (mList == null) {
                    return;
                }
                mJokeListAdapter = new JokeListAdapter(mList);
                mListView.setAdapter(mJokeListAdapter);
                mCurrentPage++;
            }

            @Override
            public void onFailure(Call<JokeBean> call, Throwable t) {

            }
        });
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //当EditText失去焦点后 隐藏评论框
        if (!hasFocus) {
            mInputRl.setVisibility(View.GONE);
            mRefreshFabBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mListView) {
            //隐藏软键盘
            mIm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
            mListView.requestFocus();       //让listView请求获得焦点
        }
        return false;
    }

    @Override
    public void onRefresh() {
        initData();
        mSwipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "刷新完成", Toast.LENGTH_SHORT).show();
            }
        }, 1300);
    }


    private class JokeListAdapter extends BaseAdapter {

        private ArrayList<JokeBean.ResultBean.DataBean> list;

        public JokeListAdapter(ArrayList<JokeBean.ResultBean.DataBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_joke_lv_item, null);
                holder = new ViewHolder();
                holder.content = (TextView) convertView.findViewById(R.id.joke_item_tv_content);
                holder.updateTime = (TextView) convertView.findViewById(R.id.joke_item_tv_time);
                holder.icon = (ImageView) convertView.findViewById(R.id.joke_item_iv_icon);

                holder.like = (ImageView) convertView.findViewById(R.id.joke_item_iv_like);
                holder.cry = (ImageView) convertView.findViewById(R.id.joke_item_iv_cry);
                holder.pinglun = (ImageView) convertView.findViewById(R.id.joke_item_iv_pinglun);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            JokeBean.ResultBean.DataBean bean = mList.get(position);
            holder.content.setText(bean.content);
            holder.updateTime.setText(bean.updatetime);
            //holder.icon.setImageResource(icons[new Random().nextInt(icons.length)]);

            holder.icon.setImageResource(icons[4]);

            //赞的点击事件监听
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:
                }
            });

            //不喜欢的点击事件监听
            holder.cry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:
                }
            });

            // //评论的点击事件监听
            holder.pinglun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //评论被点击了,弹出评论框
                    mRefreshFabBtn.setVisibility(View.GONE);
                    mInputRl.setVisibility(View.VISIBLE);
                    mEditText.setFocusable(true);
                    mEditText.requestFocus();

                    //打开软键盘
                    mIm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    mSendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //发送按钮被点击了
                            //隐藏评论框
                            mIm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
                            mListView.requestFocus();       //让listView请求获得焦点

                            //TODO:请求数据到网络服务器,然后更新数据
                        }
                    });
                }
            });

            return convertView;
        }

        private class ViewHolder {

            ImageView icon;
            TextView content;
            TextView updateTime;

            ImageView like;
            ImageView cry;
            ImageView pinglun;
        }
    }

}
