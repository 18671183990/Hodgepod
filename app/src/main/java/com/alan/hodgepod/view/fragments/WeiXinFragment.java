package com.alan.hodgepod.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.hodgepod.R;
import com.alan.hodgepod.module.api.WeiXInJingXuan;
import com.alan.hodgepod.module.bean.WeiXinJingXuanBean;
import com.alan.hodgepod.module.db.WeixinLikeDao;
import com.alan.hodgepod.view.activity.WeiXinDetailActivity;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 16-5-31.微信分享的Fragment
 */
public class WeiXinFragment extends Fragment
        implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, AbsListView.OnScrollListener, View.OnTouchListener {

    public static final String WEIXINJX_URL = "http://v.juhe.cn/weixin/";
    public static final String WEIXINJINGXUAN_KEY = "db3e424945a4c667ba3035a6445fd17c";
    public static final String PARCELABLE_KEY_BEAN = "parcelable_key_bean";
    private static final String TAG = "WeiXinFragment";

    public static final int REQUEST_STATUS_ERROR = 0; //请求失败
    public static final int REQUEST_STATUS_SUCCESS = 1; //请求成功
    private static final int LOADMORE_DATA_FROM_NET_SUCCESS = 10010;
    //private static final int DO_ANIMATION_SUCCESS = 10011;

    private boolean isLoadMore = false;      //用来标记是否正在加载更多
    private int pno = 1;                               //当前请求的是第几页数据
    public int mRequest_state;                  //用来标记网络请求状态   成功or失败

    private View mRootView;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeLayout;
    private AlertDialog mShareDialog;
    private View mFooterView;
    private CircleProgressBar mCircleProgressBar;

    private List<WeiXinJingXuanBean.ResultBean.ListBean> mList;                     //详细数据的list
    private MyListAdapter mMyListAdapter;
    private WeiXinJingXuanBean mBean;
    private WeiXinJingXuanBean mCacheBean;                                                   //缓存的bean
    private WeiXinJingXuanBean.ResultBean.ListBean mContextSelectedBean;    //被上下文菜单选中的bean
    private Response<WeiXinJingXuanBean> mResponse;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADMORE_DATA_FROM_NET_SUCCESS:            //网络数据获取成功
                    Log.d(TAG, "handleMessage: 收到数据加载完成的通知");
                    //noinspection unchecked
                    mResponse = (Response<WeiXinJingXuanBean>) msg.obj;
                    loadMoreDissAnimation();
                    break;
            }
        }
    };


    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mRequest_state = REQUEST_STATUS_SUCCESS;
            mBean = mResponse.body();
            pno = mBean.getResult().getPno();
            mList.addAll(mBean.getResult().getList());
            Log.d(TAG, "onAnimationEnd: ");
            mListView.removeFooterView(mFooterView);
            mCircleProgressBar.setVisibility(View.GONE);
            mMyListAdapter.notifyDataSetChanged();
            isLoadMore = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    /**
     * U盟需要的权限
     */

    /**
     * U盟分享列表
     */

    /*友盟分享监听*/
    private UMShareListener mUMShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(getActivity(), "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(getActivity(), "分享取消", Toast.LENGTH_SHORT).show();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_weixin_root, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ActivityCompat.requestPermissions(getActivity(), mPermissionList, 100);    //友盟分享需要的操作,针对6.0平台
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.weixin_swipelayout);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mListView = (ListView) mRootView.findViewById(R.id.weixin_listview);
        mFooterView = View.inflate(getActivity(), R.layout.layout_main_listview_footer, null);
        mCircleProgressBar = (CircleProgressBar) mFooterView.findViewById(R.id.progressBar);
        autoRefresh();
    }

    /**
     * 自动刷新
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
        }, 700);
    }

    /**
     * 加载数据
     */
    private void initData() {
        boolean hasCache = getDataFromCache();
        if (hasCache) {
            //加载缓存数据
            loadCacheData();
        } else {
            //加载网络数据
            getDataFromNet();
        }
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mSwipeLayout.setOnRefreshListener(this);
        mFooterView.setOnTouchListener(this);

        //注册上下文菜单
        this.registerForContextMenu(mListView);
    }


    /**
     * 加载缓存数据
     */
    private void loadCacheData() {
        mList = mCacheBean.getResult().getList();
        if (mList == null) {
            return;
        }
        mMyListAdapter = new MyListAdapter();
        mListView.setAdapter(mMyListAdapter);
    }


    private void getDataFromNet() {
        useRetrofit();
    }

    /**
     * 获取本地的缓存数据
     *
     * @return 返回false代表没有缓存数据, 返回true表示有缓存数据
     */
    private boolean getDataFromCache() {
        boolean hasCache = false;
        FileInputStream fileInputStream;
        ObjectInputStream objInputStream = null;
        try {
            fileInputStream = getActivity().openFileInput("weixinjingxuan.txt");
            objInputStream = new ObjectInputStream(fileInputStream);
            //反序列化得到JavaBean    ClassNotFoundException,IOException,OptionalDataException
            WeiXinJingXuanBean bean = (WeiXinJingXuanBean) objInputStream.readObject();
            if (bean != null) {
                mCacheBean = bean;
                hasCache = true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            hasCache = false;
        } finally {
            try {
                if (objInputStream != null) {
                    objInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hasCache;
    }

    /**
     * 使用retrofit请求网络
     */
    private void useRetrofit() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(WEIXINJX_URL).addConverterFactory(GsonConverterFactory.create()).build();
        WeiXInJingXuan weixin = retrofit.create(WeiXInJingXuan.class);
        retrofit2.Call<WeiXinJingXuanBean> call = weixin.getCall(WEIXINJINGXUAN_KEY, 1, 10);    //初次加载
        call.enqueue(new Callback<WeiXinJingXuanBean>() {

            @Override
            public void onResponse(retrofit2.Call<WeiXinJingXuanBean> call, Response<WeiXinJingXuanBean> response) {
                //这里的做法有待考证,call.enqueue是异步执行,但是onResponse回调是在主线程中执行的
                mRequest_state = REQUEST_STATUS_SUCCESS;
                mBean = response.body();
                pno = mBean.getResult().getPno();
                mList = mBean.getResult().getList();
                if (mList == null) {
                    return;
                }
                mMyListAdapter = new MyListAdapter();
                mListView.setAdapter(mMyListAdapter);

                //TODO:存储缓存,缓存方案有待考证
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cacheData(mBean);
                    }
                }).start();
            }

            @Override
            public void onFailure(retrofit2.Call<WeiXinJingXuanBean> call, Throwable t) {
                mRequest_state = REQUEST_STATUS_ERROR;
            }
        });
    }

    /**
     * 缓存方案:  此Fragment中使用数据库缓存 1.数据库缓存 2.文件缓存 3.SharePreference缓存
     */
    private void cacheData(WeiXinJingXuanBean bean) {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objOutPutStream = null;
        try {
            fileOutputStream = getActivity().openFileOutput("weixinjingxuan.txt", Context.MODE_PRIVATE);
            objOutPutStream = new ObjectOutputStream(fileOutputStream);
            objOutPutStream.writeObject(bean);
            objOutPutStream.flush();

        } catch (IOException e) {
            Log.d(TAG, "cacheData: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (objOutPutStream != null) {
                    objOutPutStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //数据库缓存
    }

    @Override
    public void onRefresh() {
        isLoadMore = false;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(WEIXINJX_URL).addConverterFactory(GsonConverterFactory.create()).build();
        WeiXInJingXuan weixin = retrofit.create(WeiXInJingXuan.class);
        retrofit2.Call<WeiXinJingXuanBean> call = weixin.getCall(WEIXINJINGXUAN_KEY, 1, 10);
        //异步操作
        call.enqueue(new Callback<WeiXinJingXuanBean>() {

            @Override
            public void onResponse(retrofit2.Call<WeiXinJingXuanBean> call, Response<WeiXinJingXuanBean> response) {
                mRequest_state = REQUEST_STATUS_SUCCESS;
                mBean = response.body();
                mList = mBean.getResult().getList();
                if (mList == null) {
                    return;
                }
                mMyListAdapter = new MyListAdapter();
                mListView.setAdapter(mMyListAdapter);

                //刷新回调
                mSwipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                        mMyListAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();

                        //缓存数据
                        cacheData(mBean);
                    }
                }, 700);
            }

            @Override
            public void onFailure(retrofit2.Call<WeiXinJingXuanBean> call, Throwable t) {
                mRequest_state = REQUEST_STATUS_ERROR;
                mSwipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "无网络连接,刷新失败", Toast.LENGTH_SHORT).show();
                    }
                }, 1500);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WeiXinJingXuanBean.ResultBean.ListBean bean = mList.get(position);
        Intent intent = new Intent(getActivity(), WeiXinDetailActivity.class);
        intent.putExtra(PARCELABLE_KEY_BEAN, (Parcelable) bean);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.transition_enter_pop_in, R.anim.transition_enter_pop_out);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_tv_weixin:
                //微信分享
                shareToWeixin();
                break;
            case R.id.share_tv_pengyou:
                shareToPengyou();
                break;
            case R.id.share_tv_qq:
                //分享到QQ暂时不做
                break;
            case R.id.share_tv_weibo:
                shareToWeibo();
                break;
        }
    }

    /**
     * 分享到微信
     */
    private void shareToWeixin() {

        if (doSomeBeforeShare()) {
            return;
        }
        UMImage image = new UMImage(getActivity(), mContextSelectedBean.getFirstImg());
        new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(mUMShareListener)
                .withText(mContextSelectedBean.getTitle())
                .withTitle(mContextSelectedBean.getTitle())
                .withTargetUrl(mContextSelectedBean.getUrl())
                .withMedia(image)
                .share();
    }

    private boolean doSomeBeforeShare() {
        if (mContextSelectedBean == null) {
            return true;
        }
        if (mShareDialog != null) {
            mShareDialog.dismiss();
        }
        return false;
    }

    /**
     * 分享到朋友圈
     */
    private void shareToPengyou() {
        if (doSomeBeforeShare()) {
            return;
        }
        UMImage image = new UMImage(getActivity(), mContextSelectedBean.getFirstImg());
        new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(mUMShareListener)
                .withText(mContextSelectedBean.getTitle())
                .withTitle(mContextSelectedBean.getTitle())
                .withTargetUrl(mContextSelectedBean.getUrl())
                .withMedia(image)
                .share();
    }

    /**
     * 分享到新浪微博
     */
    private void shareToWeibo() {
        if (doSomeBeforeShare()) {
            return;
        }
        UMImage image = new UMImage(getActivity(), mContextSelectedBean.getFirstImg());
        new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.SINA)
                .setCallback(mUMShareListener)
                .withText(mContextSelectedBean.getTitle())
                .withTargetUrl(mContextSelectedBean.getUrl())
                .withMedia(image)
                .share();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastVisiblePosition = mListView.getLastVisiblePosition();
        if ((totalItemCount - 1) == lastVisiblePosition) {
            //说明已经滑动到底部,并且当前不是loadMore状态,加载更多
            if (!isLoadMore) {
                loadMoreLikeZhiHu();
            }
        }
    }

    /**
     * 仿照知乎的加载更多
     */
    private synchronized void loadMoreLikeZhiHu() {
        isLoadMore = true;
        mCircleProgressBar.clearAnimation();
        mListView.addFooterView(mFooterView);
        mCircleProgressBar.setVisibility(View.VISIBLE);
        //从网络获取数据
        loadMoreDataFromNet();
    }

    /**
     * 上拉从网络加载更多数据
     */
    private void loadMoreDataFromNet() {
        int index = pno + 1;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(WEIXINJX_URL).addConverterFactory(GsonConverterFactory.create()).build();
        WeiXInJingXuan weixin = retrofit.create(WeiXInJingXuan.class);
        retrofit2.Call<WeiXinJingXuanBean> call = weixin.getCall(WEIXINJINGXUAN_KEY, index, 10);

        call.enqueue(new Callback<WeiXinJingXuanBean>() {
            @Override
            public void onResponse(retrofit2.Call<WeiXinJingXuanBean> call, final Response<WeiXinJingXuanBean> response) {
                //数据获取完成,使用Handler实现线程间通信
                final Message msg = Message.obtain();
                msg.what = LOADMORE_DATA_FROM_NET_SUCCESS;
                msg.obj = response;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendMessage(msg);
                    }
                }, 700);
            }

            @Override
            public void onFailure(retrofit2.Call<WeiXinJingXuanBean> call, Throwable t) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.removeFooterView(mFooterView);
                        mCircleProgressBar.setVisibility(View.GONE);
                        isLoadMore = false;
                    }
                }, 3000);
            }
        });
    }

    /**
     * FooterView消失的动画
     */
    private void loadMoreDissAnimation() {
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.3f);
        mTranslateAnimation.setInterpolator(new AccelerateInterpolator());
        mTranslateAnimation.setDuration(200);
        mTranslateAnimation.setFillAfter(false);
        mTranslateAnimation.setAnimationListener(mAnimationListener);
        mFooterView.clearAnimation();
        mFooterView.startAnimation(mTranslateAnimation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }


    /**
     * 收藏操作,将被选中的item保存到数据库
     *
     * @param position 被选中的item
     */
    private void doCollection(int position) {
        WeiXinJingXuanBean.ResultBean.ListBean listBean = mList.get(position);
        //将选中的信息插入到数据库中
        WeixinLikeDao dao = new WeixinLikeDao(getActivity());
        long insert = dao.insert(listBean.getId(), listBean.getTitle(), listBean.getFirstImg(), listBean.getUrl());
        if (insert > 0) {
            Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "您已经收藏过此文章了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 社会化分享操作
     *
     * @param position 分享的item
     */
    private void doShare(int position) {
        mContextSelectedBean = mList.get(position);
        View view = View.inflate(getActivity(), R.layout.layout_share_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mShareDialog = builder.setCancelable(true).setTitle("分享到").setView(view).create();
        view.findViewById(R.id.share_tv_weixin).setOnClickListener(this);
        view.findViewById(R.id.share_tv_pengyou).setOnClickListener(this);
        view.findViewById(R.id.share_tv_qq).setOnClickListener(this);
        view.findViewById(R.id.share_tv_weibo).setOnClickListener(this);
        mShareDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.weixindetail_contextmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //item中包含的被选中的position
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        //long id = menuInfo.id;
        //menuInfo .targetView;
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.weixindetail_context_copy:
                doCollection(position);
                break;
            case R.id.weixindetail_context_share:
                doShare(position);
                break;
        }
        return true;    //true表明自己消费(处理)此事件
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ListView的Adapter
     */
    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_weixin_lv_item, null);
                holder = new ViewHolder();
                holder.pic = (ImageView) convertView.findViewById(R.id.weixin_item_iv);
                holder.title = (TextView) convertView.findViewById(R.id.weixin_item_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final WeiXinJingXuanBean.ResultBean.ListBean bean = mList.get(position);
            if (!TextUtils.isEmpty(bean.getFirstImg())) {
                Picasso.with(getActivity()).load(bean.getFirstImg()).fit().into(holder.pic);
            }
            holder.title.setText(bean.getTitle());
            return convertView;
        }

        class ViewHolder {

            ImageView pic;
            TextView title;
        }
    }
}
