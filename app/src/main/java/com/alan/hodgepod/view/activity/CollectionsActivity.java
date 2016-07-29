package com.alan.hodgepod.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.hodgepod.R;
import com.alan.hodgepod.module.bean.CollectionsBean;
import com.alan.hodgepod.module.db.WeixinLikeDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by Administrator on 16-6-5.
 */
public class CollectionsActivity extends SwipeBackActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "CollectionsActivity";
    private SwipeBackLayout mSwipeBackLayout;
    private ListView mListView;

    private ArrayList<CollectionsBean> mList;
    private CollectionsAdapter mCollectionsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collections);

        initView();
        initData();
        initEvent();

    }

    private void initView() {

        Toolbar mToolBar = (Toolbar) findViewById(R.id.collections_toolbar);
        assert mToolBar != null;
        mToolBar.setTitle("收藏");    //设置标题要放在setSupportActionBar方法之前
        setSupportActionBar(mToolBar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.colllections_listview);
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    private void initData() {
        WeixinLikeDao dao = new WeixinLikeDao(this);
        mList = dao.queryAll();
        Collections.reverse(mList);
        mCollectionsAdapter = new CollectionsAdapter(mList);
        mListView.setAdapter(mCollectionsAdapter);
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
        this.registerForContextMenu(mListView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CollectionsBean bean = mList.get(position);
        String url = bean.getUrl();
        Intent intent = new Intent(this, WeiXinDetailActivity.class);
        intent.putExtra(WeiXinDetailActivity.KEY_URL, url);
        startActivity(intent);
        overridePendingTransition(R.anim.transition_enter_pop_in, R.anim.transition_enter_pop_out);
    }


    private class CollectionsAdapter extends BaseAdapter {

        private List<CollectionsBean> mList;

        public CollectionsAdapter(List list) {
            this.mList = list;
        }

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
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(CollectionsActivity.this, R.layout.layout_collections_item, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.collections_item_tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //            String collectionsTitle = mList.get(position);
            CollectionsBean bean = mList.get(position);
            holder.title.setText(bean.getTitle());

            return convertView;
        }

        private class ViewHolder {

            TextView title;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        //        if (itemId == android.R.id.home) {
        //            finish();
        //            return true;
        //        }
        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                doClear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.collections_contextmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        switch (itemId) {
            case R.id.collections_context_delete:
                doDelete(position);
                break;
            case R.id.collections_context_clear:
                doClear();
        }

        return true;
    }

    /**
     * 根据指定的position去删除数据库中的数据
     */
    private void doDelete(int position) {
        CollectionsBean bean = mList.get(position);
        String data_id = bean.getData_id();
        WeixinLikeDao dao = new WeixinLikeDao(this);
        int delete = dao.deleteOne(data_id);
        if (delete > 0) {
            Toast.makeText(CollectionsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            mList.remove(position);
            mCollectionsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 清空我的收藏中的数据
     */
    private void doClear() {
        //TODO:
        Log.d(TAG, "doClear: 去清除数据");
        WeixinLikeDao dao = new WeixinLikeDao(this);
        int delete = dao.clearAll();
        if (delete > 0) {
            Toast.makeText(CollectionsActivity.this, "清空成功", Toast.LENGTH_SHORT).show();
            mList.clear();
            mCollectionsAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.transition_exit_pop_in, R.anim.transition_exit_pop_out);
    }
}
