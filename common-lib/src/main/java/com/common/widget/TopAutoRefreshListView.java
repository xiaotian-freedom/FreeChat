package com.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 顶部自动刷新listview
 * Created by tianshutong on 2017/6/6.
 */

public abstract class TopAutoRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private boolean isRefreshing = false;
    private boolean isEnable = false;
    private boolean canScroll = true;
    public boolean isBottom = false;
    private int mPosition;
    private RefreshHeaderLayout mHeaderLayout;
    private onTopRefreshListener mTopRefreshListener;

    public TopAutoRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public TopAutoRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopAutoRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mHeaderLayout = new RefreshHeaderLayout(context);
        addHeaderView(mHeaderLayout);
        this.setOnScrollListener(this);
    }

    /**
     * 设置是否可自动刷新
     *
     * @param b
     */
    public void setAutoRefreshEnabled(boolean b) {
        isEnable = b;
        mHeaderLayout.hide();
    }

    /**
     * 设置是否可滚动
     *
     * @param b
     */
    public void setCanScroll(boolean b) {
        canScroll = b;
    }

    /**
     * 加载完成
     */
    public void onLoadFinish() {
        isEnable = false;
        isRefreshing = false;
        mHeaderLayout.setNoMoreData();
    }

    /**
     * 刷新完成
     */
    public void onRefreshComplete() {
        mHeaderLayout.hide();
        isRefreshing = false;
    }

    /**
     * 显示头部刷新效果
     */
    public void showHeader() {
        mHeaderLayout.show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!isEnable) {
            return;
        }
        if (view.getAdapter() == null) {
            return;
        }
        if (view.getChildCount() == 0) {
            return;
        }
        if (scrollState == SCROLL_STATE_IDLE && view.getFirstVisiblePosition() == 0) {
            if (mTopRefreshListener != null) {
                mHeaderLayout.show();
                isRefreshing = true;
                mTopRefreshListener.onRefresh();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
            return super.dispatchTouchEvent(ev);
        }
        if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (!canScroll) {
                return true;
            }
        }

        if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
            if (pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
                super.dispatchTouchEvent(ev);
            } else {
                if (!canScroll) {
                    setPressed(false);
                    invalidate();
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOnTopRefreshListener(onTopRefreshListener listener) {
        mTopRefreshListener = listener;
    }

    public interface onTopRefreshListener {
        void onRefresh();
    }
}
