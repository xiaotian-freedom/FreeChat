package com.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.R;

public class TabRadioButton extends LinearLayout implements Checkable {

    private boolean mChecked;
    private CustomCheckedTextView checkedTextView;
    private CheckedImageView checkedImageView;
    private ImageView ivMsg;
    private TextView tvMsg;
    private ImageView ivNew;
    private int mButtonResource;
    private boolean mBroadcasting;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    public TabRadioButton(Context context) {
        this(context, null);
    }

    public TabRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        LayoutInflater.from(context).inflate(R.layout.tab_radio_button_layout, this, true);
        checkedTextView = (CustomCheckedTextView) this.findViewById(R.id.tab_item_text);
        checkedImageView = (CheckedImageView) this.findViewById(R.id.tab_item_image);
        ivMsg = (ImageView) this.findViewById(R.id.unchecked_msg_icon);
        tvMsg = (TextView) this.findViewById(R.id.unchecked_msg_num);
        ivNew = (ImageView) this.findViewById(R.id.unchecked_msg_new);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabRadioButton);
        String text = a.getString(R.styleable.TabRadioButton_trbText);
        checkedTextView.setText(text);
        Drawable d = a.getDrawable(R.styleable.TabRadioButton_trbButton);
        if (d != null) {
            setButtonDrawable(d);
        }
        boolean checked = a.getBoolean(R.styleable.TabRadioButton_trbChecked, false);
        setChecked(checked);
        a.recycle();
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            checkedTextView.setChecked(mChecked);
            checkedImageView.setChecked(mChecked);

            // Avoid infinite recursions if setChecked() is called from a
            // listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
            }

            mBroadcasting = false;
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
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes. This callback is used for internal purpose only.
     *
     * @param listener the callback to call on checked state change
     * @hide
     */
    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(TabRadioButton buttonView, boolean isChecked);
    }

    /**
     * Set the background to a given Drawable, identified by its resource id.
     *
     * @param resid the resource id of the drawable to use as the background
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
     * @param d The Drawable to use as the background
     */
    public void setButtonDrawable(Drawable d) {
        if (d != null) {
            checkedImageView.setImageDrawable(d);
        }
    }

    private static class SavedState extends BaseSavedState {
        boolean checked;

        /**
         * Constructor called from {@link CompoundButton#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        @Override
        public String toString() {
            return "CompoundButton.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.checked = isChecked();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    // *********************************************************************
    public void setText(CharSequence text) {
        if (checkedTextView != null) {
            checkedTextView.setText(text);
        }
    }

    public void setText(int resid) {
        if (checkedTextView != null) {
            checkedTextView.setText(resid);
        }
    }

    // ////////////////////////////////////RedPoint////////////////////////////
    public void setMsgIconVisibility(int visibility) {
        if (ivMsg != null) {
            ivMsg.setVisibility(visibility);
        }
    }

    public boolean isMsgIconVisible() {
        return ivMsg != null && ivMsg.getVisibility() == View.VISIBLE;
    }

    public void setMsgNum(CharSequence num) {
        if (tvMsg != null) {
            if (TextUtils.isEmpty(num)) {
                tvMsg.setVisibility(View.GONE);
            } else {
                tvMsg.setText(num);
                tvMsg.setVisibility(View.VISIBLE);
            }
        }
    }

    // ////////////////////////////////////NumPoint////////////////////////////
    public void setMsgNum(int num) {
        if (tvMsg != null) {
            if (0 == num) {
                tvMsg.setText("");
                tvMsg.setVisibility(View.GONE);
            } else if (0 < num) {
                tvMsg.setText(String.valueOf(num));
                tvMsg.setVisibility(View.VISIBLE);
            }
        }
    }

    public int getMsgNum() {
        int num = 0;
        if (tvMsg == null || tvMsg.getVisibility() != View.VISIBLE) {
            return 0;
        }
        String text = tvMsg.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return 0;
        } else if (TextUtils.isDigitsOnly(text)) {
            try {
                num = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (text.endsWith("+")) {
            Object obj = getMsgTag();
            if (obj instanceof Integer) {
                num = (int) obj;
            }
        }
        return num;
    }

    public void setMsgTag(Object tag) {
        tvMsg.setTag(tag);
    }

    public Object getMsgTag() {
        return tvMsg.getTag();
    }

    public boolean isMsgTextVisible() {
        return tvMsg != null && tvMsg.getVisibility() == View.VISIBLE;
    }

    // ////////////////////////////////////NewMark////////////////////////////
    public void setNewIconVisibility(int visibility) {
        if (ivNew != null) {
            ivNew.setVisibility(visibility);
        }
    }

}
