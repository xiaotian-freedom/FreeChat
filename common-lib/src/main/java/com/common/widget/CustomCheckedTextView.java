package com.common.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.TextView;

public class CustomCheckedTextView extends TextView implements Checkable {

	private boolean mChecked;

	private int mButtonResource;
	private Drawable mButtonDrawable;

	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

	public CustomCheckedTextView(Context context) {
		super(context);
	}

	public CustomCheckedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomCheckedTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
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

	/**
	 * Set the background to a given Drawable, identified by its resource id.
	 * 
	 * @param resid
	 *            the resource id of the drawable to use as the background
	 */
	public void setButtonDrawable(int resid) {
		if (resid != 0 && resid == mButtonResource) {
			return;
		}

		mButtonResource = resid;

		Drawable d = null;
		if (mButtonResource != 0) {
			d = getResources().getDrawable(mButtonResource);
		}
		setButtonDrawable(d);
	}

	/**
	 * Set the background to a given Drawable
	 * 
	 * @param d
	 *            The Drawable to use as the background
	 */
	public void setButtonDrawable(Drawable d) {
		if (d != null) {
			if (mButtonDrawable != null) {
				mButtonDrawable.setCallback(null);
				unscheduleDrawable(mButtonDrawable);
			}
			d.setCallback(this);
			d.setState(getDrawableState());
			d.setVisible(getVisibility() == VISIBLE, false);
			mButtonDrawable = d;
			mButtonDrawable.setState(null);
			setMinHeight(mButtonDrawable.getIntrinsicHeight());
			setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
		}

		refreshDrawableState();
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();

		if (mButtonDrawable != null) {
			int[] myDrawableState = getDrawableState();

			// Set the state of the Drawable
			mButtonDrawable.setState(myDrawableState);
		}
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return super.verifyDrawable(who) || who == mButtonDrawable;
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		boolean populated = super.dispatchPopulateAccessibilityEvent(event);
		if (!populated) {
			event.setChecked(mChecked);
		}
		return populated;
	}

}
