package com.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.common.R;

/**
 * @author Stefen
 */
public class ClearableEditText extends AppCompatEditText implements
        OnFocusChangeListener, TextWatcher {

    private Drawable clearBtnDrawable;
    private int clearBtnDrawableHeight;
    private int clearBtnDrawableWidth;

    private OnClearBtnClickListener mClearBtnClickListener;
    private OnTextWatcher mOnTextWatcher;

    public ClearableEditText(Context context) {
        this(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ClearableEditText);
        clearBtnDrawable = a
                .getDrawable(R.styleable.ClearableEditText_clearBtnDrawable);
        if (isInEditMode()) {
            return;
        }
        if (clearBtnDrawable == null) {
            clearBtnDrawable = context.getResources().getDrawable(
                    R.drawable.common_input_box_clear);
        }
        clearBtnDrawableHeight = a.getDimensionPixelSize(
                R.styleable.ClearableEditText_clearBtnDrawableHeight, 0);
        if (clearBtnDrawableHeight == 0) {
            clearBtnDrawableHeight = clearBtnDrawable.getIntrinsicHeight();
        }
        clearBtnDrawableWidth = a.getDimensionPixelSize(
                R.styleable.ClearableEditText_clearBtnDrawableWidth, 0);
        if (clearBtnDrawableWidth == 0) {
            clearBtnDrawableWidth = clearBtnDrawable.getIntrinsicWidth();
        }
        a.recycle();

        init();
    }

    private void init() {
        // 获取EditText的DrawableRight,假如没设置,就使用默认的图片
        Drawable right = getCompoundDrawables()[2];
        if (right == null) {
            right = clearBtnDrawable;
        }

        if (isInEditMode()) {
            return;
        }
        right.setBounds(0, 0, clearBtnDrawableWidth, clearBtnDrawableHeight);

        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 因为不能直接给EditText设置点击事件，所以用记住按下的位置来模拟点击事件.当按下的位置在 </br>EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距</br>之间, 就算点击了图标，竖直方向就没有考虑
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                    if (mClearBtnClickListener != null) {
                        mClearBtnClickListener.onClearBtnClick();
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible boolean
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? clearBtnDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        // 当输入字符长度 > 0，并且该EditText获得焦点时，显示Clear Icon
        setClearIconVisible(s.length() > 0 && this.isFocused());
        if (mOnTextWatcher != null) {
            mOnTextWatcher.onTextChanged(s, start, count, after);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        if (mOnTextWatcher != null) {
            mOnTextWatcher.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mOnTextWatcher != null) {
            mOnTextWatcher.afterTextChanged(s);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 设置Clearable Icon Click Listener
     *
     * @param listener OnClearBtnClickListener
     */
    public void setOnClearBtnClickListener(OnClearBtnClickListener listener) {
        mClearBtnClickListener = listener;
    }

    /**
     * Clearable Icon Click Listener
     *
     * @author Administrator
     */
    public interface OnClearBtnClickListener {
        void onClearBtnClick();
    }

    /**
     * 设置 Text Change Listener
     */
    public void setOnTextWatcher(OnTextWatcher watcher) {
        mOnTextWatcher = watcher;
    }

    /**
     * Text Change Listener
     *
     * @author Administrator
     */
    public interface OnTextWatcher {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int count, int after);

        void afterTextChanged(Editable s);
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation(int counts) {
        this.startAnimation(shakeAnimation(counts));
    }

    /**
     * 设置晃动动画(Default:5次晃动)
     */
    public void setShakeAnimation() {
        this.startAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return Animation
     */
    private static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}
