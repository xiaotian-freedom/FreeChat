package com.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by tianshutong on 16/8/4.
 */

public class FixedVerticalListView extends ListView {

    private GestureDetector mGestureDetector;
    private OnTouchListener mOnTouchListener;

    public FixedVerticalListView(Context context) {
        super(context);
        init();
    }

    public FixedVerticalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FixedVerticalListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(new YScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return mGestureDetector.onTouchEvent(ev);
    }

    private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) / Math.abs(distanceX) > 2;
        }
    }
}
