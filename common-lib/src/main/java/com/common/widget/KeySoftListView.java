package com.common.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.common.util.DensityUtil;
import com.common.util.SoftKeyBoardUtil;

/**
 * Created by tianshutong on 2017/6/19.
 */

public class KeySoftListView extends TopAutoRefreshListView implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private Activity mActivity;
    private int flingHeight;
    private static final int flingSpeed = 10;

    public KeySoftListView(Context context) {
        super(context);
    }

    public KeySoftListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeySoftListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindActivity(Activity activity) {
        this.mActivity = activity;
        initGesture(activity);
        flingHeight = DensityUtil.getScreenHeight(activity) / 10;
    }

    private void initGesture(Context context) {
        mGestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        SoftKeyBoardUtil.hideSoftKeyboard(mActivity);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) {
            return false;
        }
        if (Math.abs(e1.getY() - e2.getY()) > flingHeight &&
                Math.abs(velocityY) > flingSpeed) {
            SoftKeyBoardUtil.hideSoftKeyboard(mActivity);
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
