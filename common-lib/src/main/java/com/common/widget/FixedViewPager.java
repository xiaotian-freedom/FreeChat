package com.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Solve: IllegalArgumentException (pointerIndex out of range) while using many
 * fingers to zoom in and out
 *
 * @author Administrator
 */
public class FixedViewPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public FixedViewPager(Context context) {
        super(context);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return isPagingEnabled && super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return isPagingEnabled && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public void setPagingEnabled(boolean isPagingEnable) {
        this.isPagingEnabled = isPagingEnable;
    }
}
