package com.common.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.ImageView;

public class CheckedImageView extends ImageView implements Checkable {

    private boolean mChecked;

    private Drawable mDrawable;

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    public CheckedImageView(Context context) {
        super(context);
        init();
    }

    public CheckedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckedImageView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mDrawable != null) {
            int[] myDrawableState = getDrawableState();

            // Set the state of the Drawable
            mDrawable.setState(myDrawableState);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mDrawable;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean populated = super.dispatchPopulateAccessibilityEvent(event);
        if (!populated) {
            event.setChecked(mChecked);
        }
        return populated;
    }

    private void init() {
        mDrawable = getDrawable();
    }

}
