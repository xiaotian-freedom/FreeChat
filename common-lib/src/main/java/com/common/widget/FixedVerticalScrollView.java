package com.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by tianshutong on 16/7/25.
 */

public class FixedVerticalScrollView extends ScrollView {

    private GestureDetector mGestureDetector;

    public FixedVerticalScrollView(Context context) {
        super(context);
        init();
    }

    public FixedVerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FixedVerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(new VerticalDetector());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    private class VerticalDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) > Math.abs(distanceX);
        }
    }
}
