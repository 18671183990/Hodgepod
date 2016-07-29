package com.alan.hodgepod.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alan.hodgepod.R;
import com.alan.hodgepod.module.bean.WeiXinJingXuanBean;
import com.alan.hodgepod.module.db.WeixinLikeDao;
import com.alan.hodgepod.view.fragments.WeiXinFragment;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Administrator on 16-6-3.
 */
public class WeiXinDetailActivity extends SwipeBackActivity implements View.OnLongClickListener, View.OnClickListener {

    private static final String TAG = "doCollection";
    public static final String KEY_URL = "key_url";
    private SwipeBackLayout mSwipeBackLayout;
    private Toolbar mToolBar;
    private WebView mWebView;
    private MaterialProgressBar mProgressBar;
    private WeiXinJingXuanBean.ResultBean.ListBean mIntentListBean;
    private AlertDialog mShareDialog;

    private UMShareListener mUMShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Log.d(TAG, "onResult: 分享成功");
            Toast.makeText(WeiXinDetailActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(WeiXinDetailActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onResult: 分享失败");

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(WeiXinDetailActivity.this, "分享取消", Toast.LENGTH_SHORT).show();

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixindetail);
        mIntentListBean = getIntent().getParcelableExtra(WeiXinFragment.PARCELABLE_KEY_BEAN);
        initView();
        initData();
        initEvent();
    }


    private void initEvent() {
        mWebView.setOnLongClickListener(this);
        this.registerForContextMenu(mWebView);
    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.weixin_detail_toolbar);
        setSupportActionBar(mToolBar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.weixin_detail_webview);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.weixindetail_progressbar);

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    private void initData() {
        initWebViewClient();
        if (mIntentListBean == null) {
            //说明当前页面是从CollectionsActivity跳转过来
            String url = getIntent().getStringExtra(KEY_URL);
            mWebView.loadUrl(url);
        } else {
            //说明当前页面是从WeiXinFragment跳转过来
            mWebView.loadUrl(mIntentListBean.getUrl());
        }

    }

    private void initWebViewClient() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            //TODO:

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                mToolBar.setTitle(title);
                super.onReceivedTitle(view, title);

            }
        });
    }

    //收藏操作,将被选中的item保存到数据库
    private void doCollection() {

        //将选中的信息插入到数据库中
        WeixinLikeDao dao = new WeixinLikeDao(this);
        Log.d(TAG, "doCollection: " + mWebView.getTitle());
        long insert = dao.insert(mIntentListBean.getId(), mIntentListBean.getTitle(), mIntentListBean.getFirstImg(), mIntentListBean.getUrl());
        if (insert > 0) {
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "您已经收藏过此文章了", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weixin_detailmenu, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_detail_like) {
            doCollection();
        } else if (item.getItemId() == R.id.action_detail_share) {
            doShare();
        } else if (item.getItemId() == R.id.action_detail_settings) {
            //TODO:设置操作
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 分享操作
     */
    private void doShare() {
        View view = View.inflate(this, R.layout.layout_share_item, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mShareDialog = builder.setCancelable(true).setTitle("分享到").setView(view).create();

        view.findViewById(R.id.share_tv_weixin).setOnClickListener(this);
        view.findViewById(R.id.share_tv_pengyou).setOnClickListener(this);
        view.findViewById(R.id.share_tv_qq).setOnClickListener(this);
        view.findViewById(R.id.share_tv_weibo).setOnClickListener(this);

        mShareDialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onLongClick(View v) {

        //弹出上下文菜单 TODO:
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.weixindetail_contextmenu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
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
                //TODO:分享到QQ暂时不做
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
        UMImage image = new UMImage(this, mIntentListBean.getFirstImg());
        new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(mUMShareListener)
                .withText(mIntentListBean.getTitle())
                .withTitle(mIntentListBean.getTitle())
                .withTargetUrl(mIntentListBean.getUrl())
                .withMedia(image)

                .share();
    }

    private boolean doSomeBeforeShare() {
        if (mIntentListBean == null) {
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
        UMImage image = new UMImage(this, mIntentListBean.getFirstImg());
        new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(mUMShareListener)
                .withText(mIntentListBean.getTitle())
                .withTitle(mIntentListBean.getTitle())
                .withTargetUrl(mIntentListBean.getUrl())
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
        UMImage image = new UMImage(this, mIntentListBean.getFirstImg());
        new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
                .setCallback(mUMShareListener)
                .withText(mIntentListBean.getTitle())
                .withTargetUrl(mIntentListBean.getUrl())
                .withMedia(image)
                .share();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.transition_exit_pop_in, R.anim.transition_exit_pop_out);
    }
}
