package com.common.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.R;

/**
 * Created by tianshutong on 2017/6/6.
 */

public class RefreshHeaderLayout extends RelativeLayout {

    private View refreshLayout;
    private TextView tipView;
    private ImageView loadingView;
    private boolean isShow = false;
    private static final String completeLabel = "已加载全部数据";

    public RefreshHeaderLayout(Context context) {
        super(context);
        init(context);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        refreshLayout = LayoutInflater.from(context).inflate(R.layout.refresh_layout, null);
        tipView = (TextView) refreshLayout.findViewById(R.id.loading_tip);
        loadingView = (ImageView) refreshLayout.findViewById(R.id.loading_view);
        addView(refreshLayout);
        refreshLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private void loadingStart() {
        loadingView.setImageResource(R.drawable.ptr_load_more_footer);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingView.getDrawable();
        animationDrawable.start();
    }

    private void loadingEnd() {
        loadingView.setImageResource(R.drawable.ptr_load_more_footer);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingView.getDrawable();
        animationDrawable.stop();
    }

    public void show() {
        if (isShow) {
            return;
        }
        loadingStart();
        isShow = true;
        ViewGroup.LayoutParams lp = refreshLayout.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        refreshLayout.setLayoutParams(lp);
    }

    public void hide() {
        loadingEnd();
        ViewGroup.LayoutParams lp = refreshLayout.getLayoutParams();
        lp.height = 0;
        refreshLayout.setLayoutParams(lp);
        isShow = false;
    }

    public void setRefreshLabel(String label) {
        if (!TextUtils.isEmpty(label)) {
            tipView.setText(label);
        }
    }

    public void setCompleteLabel(String label) {
        if (!TextUtils.isEmpty(label)) {
            tipView.setText(label);
        }
    }

    public void setNoMoreData() {
        tipView.setText(completeLabel);
        loadingView.setVisibility(GONE);
        show();
    }
}
