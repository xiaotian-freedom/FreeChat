package com.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 多行排列RadioGroup
 *
 * Created by tianshutong on 16/7/23.
 */

public class MultiRadioGroup extends LinearLayout {

    private int mCheckedId = -1;

    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;

    private boolean mProtectFromCheckedChange = false;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    private PassThroughHierarchyChangeListener mPassThroughListener;

    public MultiRadioGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);
        init();
    }

    public MultiRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiRadioGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    public void setCheckWithoutNotif(int id) {
        if (id != -1 && (id == mCheckedId)) {
            return;
        }

        mProtectFromCheckedChange = true;
        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        mCheckedId = id;
        mProtectFromCheckedChange = false;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        List<RadioButton> btns = getAllRadioButton(child);
        if (btns != null && btns.size() > 0) {
            for (RadioButton button : btns) {
                if (button.isChecked()) {
                    mProtectFromCheckedChange = true;
                    if (mCheckedId != -1) {
                        setCheckedStateForView(mCheckedId, false);
                    }
                    mProtectFromCheckedChange = false;
                    setCheckedId(button.getId());
                }
            }
        }
        super.addView(child, index, params);
    }

    private List<RadioButton> getAllRadioButton(View child) {
        List<RadioButton> btns = new ArrayList<>();
        if (child instanceof RadioButton) {
            btns.add((RadioButton) child);
        } else if (child instanceof ViewGroup) {
            int counts = ((ViewGroup) child).getChildCount();
            for (int i = 0; i < counts; i++) {
                btns.addAll(getAllRadioButton(((ViewGroup) child).getChildAt(i)));
            }
        }
        return btns;
    }

    public void check(int id) {
        // don't even bother
        if (id != -1 && (id == mCheckedId)) {
            return;
        }

        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    private void setCheckedId(int id) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof RadioButton) {
            ((RadioButton) checkedView).setChecked(checked);
        }
    }

    public int getCheckedRadioButtonId() {
        return mCheckedId;
    }

    public void clearCheck() {
        check(-1);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MultiRadioGroup.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MultiRadioGroup.LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MultiRadioGroup.class.getName());
    }

    @SuppressLint("NewApi")
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MultiRadioGroup.class.getName());
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(MultiRadioGroup group, int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            if (mCheckedId != -1) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            int id = buttonView.getId();
            setCheckedId(id);
        }
    }

    private class PassThroughHierarchyChangeListener implements
            ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @SuppressLint("NewApi")
        public void onChildViewAdded(View parent, View child) {
            if (parent == MultiRadioGroup.this) {
                List<RadioButton> btns = getAllRadioButton(child);
                if (btns != null && btns.size() > 0) {
                    for (RadioButton btn : btns) {
                        int id = btn.getId();
                        // generates an id if it's missing
                        if (id == View.NO_ID && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            id = View.generateViewId();
                            btn.setId(id);
                        }
                        btn.setOnCheckedChangeListener(
                                mChildOnCheckedChangeListener);
                    }
                }
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == MultiRadioGroup.this) {
                List<RadioButton> btns = getAllRadioButton(child);
                if (btns != null && btns.size() > 0) {
                    for (RadioButton btn : btns) {
                        btn.setOnCheckedChangeListener(null);
                    }
                }
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }
}
