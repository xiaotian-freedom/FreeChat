package com.storn.freechat.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.storn.freechat.R;
import com.storn.freechat.interfac.OnChatRefreshListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AutoRefreshListView extends ListView implements OnScrollListener,
        OnClickListener {
    private View header;// 顶部布局；
    private View footer;// 底部布局
    private int headerHeight;// 顶部布局文件的高度；
    private int firstVisibleItem;// 当前第一个可见的item的位置；
    private int lastVisibleItem;
    private int totalItemCount;

    private int scrollState;// listview 当前滚动状态；
    private boolean isRemark;// 标记，是否按下；
    private int startY;// 按下时的Y值
    private int endY;
    private int directionY;
    // 往下拉刷新 header的几种状态
    private int header_state;
    private final int NONE = 0;// 正常状态；
    private final int PULL = 1;// 提示下拉可以刷新状态；
    private final int RELESE = 2;// 提示释放刷新状态；
    private final int REFLASHING = 3;// 正在刷新状态；
    private TextView tv_header_tip;
    private ImageView igv_header_arrow;

    private RelativeLayout rl_footerLayout;
    private TextView tv_footer_tip;
    private ImageView igv_progressbar_footer;
    private ImageView igv_footer_arrow;
    private ImageView igv_header_progress;

    private OnChatRefreshListener iRefreshListener;

    private boolean isHeaderPullDownRefresh = false;// 顶部下拉刷新
    private boolean isHeaderAutoLoadMore = false;
    private boolean isHeaderPullDownLoadMore = false;
    private boolean isFooterAutoLoadMore = false;// 底部自动加载更多
    private boolean isFooterClickLoadMore = false;// 底部点击加载更多

    private int mMaxYOverscrollDistance;

    public AutoRefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public AutoRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TypedArray a = context.obtainStyledAttributes(attrs,
        // R.styleable.RefreshListView);
        // int n = a.getIndexCount();
        // for (int i = 0; i < n; i++) {
        // int attr = a.getIndex(i);
        // if (attr == R.styleable.RefreshListView_isHeaderPullDownRefresh) {
        // this.isHeaderPullDownRefresh = a.getBoolean(attr, false);
        // } else if (attr == R.styleable.RefreshListView_isHeaderAutoLoadMore)
        // {
        // this.isHeaderAutoLoadMore = a.getBoolean(attr, false);
        // } else if (attr == R.styleable.RefreshListView_isFooterAutoLoadMore)
        // {
        // this.isFooterAutoLoadMore = a.getBoolean(attr, false);
        // } else if (attr == R.styleable.RefreshListView_isFooterClickLoadMore)
        // {
        // this.isFooterClickLoadMore = a.getBoolean(attr, false);
        // } else if (attr ==
        // R.styleable.RefreshListView_isHeaderPullDownLoadMore) {
        // this.isHeaderPullDownLoadMore = a.getBoolean(attr, false);
        // }
        //
        // }
        // a.recycle();
        initView(context);
    }

    public AutoRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化界面，添加顶部布局文件到 listview
     *
     * @param context
     */
    @SuppressLint("InflateParams")
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        header = inflater.inflate(R.layout.comm_refresh_listview_header, null);
        footer = inflater.inflate(R.layout.comm_refresh_listview_footer, null);
        tv_header_tip = (TextView) header.findViewById(R.id.tv_header_tip);
        igv_header_arrow = (ImageView) header
                .findViewById(R.id.igv_header_arrow);
        igv_header_progress = (ImageView) header
                .findViewById(R.id.igv_header_progress);
        rl_footerLayout = (RelativeLayout) footer.findViewById(R.id.rl_footer);
        tv_footer_tip = (TextView) footer.findViewById(R.id.tv_footer_tip);
        igv_progressbar_footer = (ImageView) footer
                .findViewById(R.id.igv_footer_progress);

        igv_footer_arrow = (ImageView) footer
                .findViewById(R.id.igv_footer_arrow);
        measureView(header);
        measureView(footer);

        if (isFooterAutoLoadMore) {
            setFooterDismiss();
            this.addFooterView(footer);
        } else if (isFooterClickLoadMore) {
            setFooterClickLoadMore();
            this.addFooterView(footer);
        }

        headerHeight = header.getMeasuredHeight();
        topPadding(-headerHeight);
        this.addHeaderView(header);

        this.setOnScrollListener(this);
        header_state = NONE;

        final DisplayMetrics metrics = context.getResources()
                .getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    public void removeHeard() {
        removeHeaderView(header);
    }

    private static final int MAX_Y_OVERSCROLL_DISTANCE = 0;
    private static final float SCROLL_RATIO = 0.4f;// 阻尼系数

    @SuppressLint("NewApi")
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX,
                mMaxYOverscrollDistance, isTouchEvent);
    }

    /**
     * 通知父布局，占用的宽，高；
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 设置header 布局 上边距；
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        this.setSelection(0);
        header.setPadding(0, topPadding, 0, 0);
        header.invalidate();
    }

    /**
     * 滚动状态改变
     *
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        if (this.isFooterAutoLoadMore == true) {
            // 停止不动了,没必要判断方向
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    setFooterLoadingState();
                    iRefreshListener.onLoadMoreFooter();
                }

            }
        }
        if (this.isHeaderAutoLoadMore == true) {
            // 停止不动了
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.getFirstVisiblePosition() == 0) {
                    setHeaderLoadingState();
                    Log.d("look", "loading");
                    iRefreshListener.onLoadMoreHeader();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isHeaderPullDownRefresh == true || isHeaderPullDownLoadMore == true) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (firstVisibleItem == 0) {
                        isRemark = true;
                        startY = (int) ev.getY();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    onMove(ev);
                    break;
                case MotionEvent.ACTION_UP:
                    if (header_state == RELESE) {
                        header_state = REFLASHING;
                        reflashViewByState();
                        iRefreshListener.onRefreshing();
                    } else if (header_state == PULL) {
                        header_state = NONE;
                        isRemark = false;
                        reflashViewByState();
                    }
                    break;
            }

        }

        if (isFooterAutoLoadMore) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = (int) ev.getY();
                    this.lastVisibleItem = this.getLastVisiblePosition() - 1;
                    break;
                case MotionEvent.ACTION_UP:
                    endY = (int) ev.getY();
                    directionY = endY - startY;
                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程操作；
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (isHeaderPullDownRefresh || isHeaderPullDownLoadMore) {
            if (!isRemark) {
                return;
            }
            int tempY = (int) ev.getY();
            int space = tempY - startY;
            int topPadding = space - headerHeight;
            switch (header_state) {
                case NONE:
                    if (space > 0) {
                        header_state = PULL;
                        reflashViewByState();
                    }
                    break;
                case PULL:
                    topPadding(topPadding);
                    if (space > headerHeight + 50
                            && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                        header_state = RELESE;
                        reflashViewByState();
                    }
                    break;
                case RELESE:
                    topPadding(topPadding);
                    if (space < headerHeight + 50) {
                        header_state = PULL;
                        reflashViewByState();
                    } else if (space <= 0) {
                        header_state = NONE;
                        isRemark = false;
                        reflashViewByState();
                    }
                    break;
            }
        }
    }

    /**
     * 根据当前状态，改变header界面显示；
     */
    private void reflashViewByState() {
        if (isHeaderPullDownRefresh || isHeaderPullDownLoadMore) {
            RotateAnimation anim = new RotateAnimation(0, 180,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500);
            anim.setFillAfter(true);
            RotateAnimation anim1 = new RotateAnimation(180, 0,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            anim1.setDuration(500);
            anim1.setFillAfter(true);
            switch (header_state) {
                case NONE:
                    igv_header_arrow.clearAnimation();
                    topPadding(-headerHeight);
                    break;

                case PULL:
                    if (isHeaderPullDownRefresh) {
                        igv_header_arrow.setVisibility(View.VISIBLE);
                        igv_header_progress.setVisibility(View.GONE);
                        tv_header_tip.setText(getContext().getResources()
                                .getString(R.string.pull_to_refresh));
                        igv_header_arrow.clearAnimation();
                        igv_header_arrow.setAnimation(anim1);
                    } else {
                        igv_header_arrow.setVisibility(View.VISIBLE);
                        igv_header_progress.setVisibility(View.GONE);
                        tv_header_tip.setText(getContext().getResources()
                                .getString(R.string.pull_to_load_more));
                        igv_header_arrow.clearAnimation();
                        igv_header_arrow.setAnimation(anim1);
                    }

                    break;
                case RELESE:
                    if (isHeaderPullDownRefresh) {
                        igv_header_arrow.setVisibility(View.VISIBLE);
                        igv_header_progress.setVisibility(View.GONE);
                        tv_header_tip.setText(getContext().getResources()
                                .getString(R.string.release_to_refresh));
                        igv_header_arrow.clearAnimation();
                        igv_header_arrow.setAnimation(anim);
                    } else {
                        igv_header_arrow.setVisibility(View.VISIBLE);
                        igv_header_progress.setVisibility(View.GONE);
                        tv_header_tip.setText(getContext().getResources()
                                .getString(R.string.release_to_load_more));
                        igv_header_arrow.clearAnimation();
                        igv_header_arrow.setAnimation(anim);
                    }

                    break;
                case REFLASHING:
                    topPadding(0);
                    if (isHeaderPullDownRefresh) {
                        igv_header_arrow.setVisibility(View.GONE);
                        igv_header_progress.setVisibility(View.VISIBLE);
                        tv_header_tip.setText(getContext().getResources()
                                .getString(R.string.refreshing));
                        igv_header_arrow.clearAnimation();
                    } else {
                        igv_header_arrow.setVisibility(View.GONE);
                        igv_header_progress.setVisibility(View.VISIBLE);
                        tv_header_tip.setText(getContext().getResources()
                                .getString(R.string.loading));
                        igv_header_arrow.clearAnimation();
                    }

                    break;
            }
        }
    }

    /**
     * 往下拉刷新获取完数据；
     */
    public void refreshComplete() {
        header_state = NONE;
        isRemark = false;
        reflashViewByState();
        TextView lastupdatetime = (TextView) header
                .findViewById(R.id.lastupdate_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss",
                Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastupdatetime.setText(time);
    }

    public void setOnChatRefresh(OnChatRefreshListener iRefreshListener) {
        this.iRefreshListener = iRefreshListener;
    }

    /**
     * 设置footer的几种呈现状态
     */

    public void setFooterDismiss() {
        tv_footer_tip.setVisibility(View.GONE);
        igv_footer_arrow.setVisibility(View.GONE);
        igv_progressbar_footer.setVisibility(View.GONE);

    }

    public void setFooterClickLoadMore() {
        tv_footer_tip.setText(getResources().getString(R.string.click_to_load_more));
        tv_footer_tip.setClickable(true);
        igv_footer_arrow.setVisibility(View.GONE);
        igv_progressbar_footer.setVisibility(View.GONE);
        rl_footerLayout.setOnClickListener(this);
    }

    public void setFooterLoadingState() {
        tv_footer_tip.setVisibility(View.VISIBLE);
        tv_footer_tip.setText("加载更多...");
        tv_footer_tip.setClickable(false);
        igv_footer_arrow.setVisibility(View.GONE);
        igv_progressbar_footer.setVisibility(View.VISIBLE);
        Drawable top = igv_progressbar_footer.getBackground();
        AnimationDrawable animawableVertical = (AnimationDrawable) top;
        animawableVertical.start();
        // animawableVertical.stop();
    }

    public void setFooterNoMoreDataState() {
        tv_footer_tip.setVisibility(View.VISIBLE);
        tv_footer_tip.setText(getResources().getString(R.string.no_more_data));
        igv_footer_arrow.setVisibility(View.GONE);
        igv_progressbar_footer.setVisibility(View.GONE);
        rl_footerLayout.setFocusable(true);
        rl_footerLayout.setClickable(false);
        this.isFooterAutoLoadMore = false;
        this.isFooterClickLoadMore = false;
    }

    public void setHeaderLoadingState() {
        igv_header_progress.setVisibility(View.VISIBLE);
        igv_header_arrow.setVisibility(View.GONE);
        tv_header_tip.setText("正在加载更多...");
        topPadding(0);
    }

    public void setHeaderNoMoreDataState() {
        isHeaderAutoLoadMore = false;
        igv_header_progress.setVisibility(View.GONE);
        tv_header_tip.setText("没有更多数据了");
        // topPadding(0);
    }

    @Override
    public void onClick(View v) {
        if (this.isFooterClickLoadMore) {
            iRefreshListener.onLoadMoreFooter();
            setFooterLoadingState();
        }
    }

    public void setFooterClickLoadMore(boolean isTrue) {
        this.isFooterClickLoadMore = isTrue;
    }

    public void setFooterAutoLoadMore(boolean isTrue) {
        this.isFooterAutoLoadMore = isTrue;
    }

    public void setHeaderAutoLoadMore(boolean isTrue) {
        this.isHeaderAutoLoadMore = isTrue;
    }

    public void setHeaderPullDownRefresh(boolean isTrue) {
        this.isHeaderPullDownRefresh = isTrue;
    }
}
