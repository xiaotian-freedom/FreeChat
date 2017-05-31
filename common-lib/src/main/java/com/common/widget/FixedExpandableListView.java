package com.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by tianshutong on 16/5/4.
 */
public class FixedExpandableListView extends ExpandableListView {
    public FixedExpandableListView(Context context) {
        super(context);
    }

    public FixedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}
