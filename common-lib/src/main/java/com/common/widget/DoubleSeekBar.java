package com.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.common.R;

import java.math.BigDecimal;

/**
 * 双向滑动seekbar
 * Created by tianshutong on 16/7/27.
 */

public class DoubleSeekBar extends View {

    private static final String TAG = "SeekBarPressure";
    private static final int CLICK_ON_LOW = 1;
    private static final int CLICK_ON_HIGH = 2;
    private static final int CLICK_OUT_AREA = 5;
    private static final int CLICK_INVAILD = 0;
    private String desc[] = {"0", "3", "6", "9", "12", "15", "不限"};

    private static final int[] STATE_NORMAL = {};
    private static final int[] STATE_PRESSED = {
            android.R.attr.state_pressed, android.R.attr.state_window_focused,
    };

    private Drawable mScrollBarBgNormal;
    private Drawable mScrollBarProgress;
    private Drawable mThumbLow;
    private Drawable mThumbHigh;

    private int mScrollBarWidth = 0;     // 滑动条宽度
    private int mScrollBarHeight = 30;    //滑动条高度
    private int mScrollBarTop = 0;       //滑动条顶部距离
    private int mScrollBarBottom = 0;   //滑动条底部距离
    private int mStartY = 120;          //滑动条在Y轴的起始位置,避免滑块显示不全

    private int mThumbWidth;        //滑块宽度
    private int mThumbHeight;       //滑块高度

    private int mProgressLow = 0;
    private int mProgressHigh = 18;
    private int mOffsetLow = 0;     //x方向最低偏移量
    private int mOffsetHigh = 0;    //x方向最高偏移量
    private int mDistance = 0;
    private float mMin = 0;//最低值
    private float mMax = 0;//最高值
    private int mDuration = 0;//每个刻度值
    private int mFlag = CLICK_INVAILD;
    private OnSeekBarChangeListener mBarChangeListener;

    private Paint line_Paint = new Paint();
    private Paint text_Paint = new Paint();
    private Paint flow_Paint = new Paint();

    public DoubleSeekBar(Context context) {
        this(context, null);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DoubleSeekBar, defStyle, 0);
        mMin = a.getFloat(R.styleable.DoubleSeekBar_minValue, mMin);
        mMax = a.getFloat(R.styleable.DoubleSeekBar_maxValue, mMax);
        int width = a.getInt(R.styleable.DoubleSeekBar_seekbar_width, -1);
        if (width == -1) {
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            int margin = (int) res.getDimension(R.dimen.space_c_2);
            mScrollBarWidth = dm.widthPixels - margin * 4;
        }
        mDistance = mScrollBarWidth - mThumbWidth;
        mDuration = (int) Math.rint(a.getFloat(R.styleable.DoubleSeekBar_duration, mDuration) * mDistance / (mMax - mMin));
        mOffsetHigh = (desc.length - 1) * ((mScrollBarWidth - mThumbWidth) / (desc.length - 1));
        if (mMax == 0) {
            throw new RuntimeException(a.getPositionDescription() + ": You must supply a maxValue attribute.");
        }
        if (mMin > mMax) {
            throw new RuntimeException(a.getPositionDescription() + ": The minValue attribute must be smaller than the maxValue attribute.");
        }
        if (mDuration == 0) {
            throw new RuntimeException(a.getPositionDescription() + ": You must supply a duration attribute.");
        }
        a.recycle();
    }

    public void init() {
        Resources resources = getResources();
        mScrollBarBgNormal = resources.getDrawable(R.drawable.grey_stroke_white_solid_round_corner);
        mScrollBarProgress = resources.getDrawable(R.drawable.yellow_solid_round_corner);
        mThumbLow = resources.getDrawable(R.drawable.slide_down);
        mThumbHigh = resources.getDrawable(R.drawable.slide_up);
        mThumbLow.setState(STATE_NORMAL);
        mThumbHigh.setState(STATE_NORMAL);
        mThumbWidth = mThumbLow.getIntrinsicWidth();
        mThumbHeight = mThumbLow.getIntrinsicHeight();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mScrollBarWidth;
        int height = mScrollBarHeight + mThumbHeight * 2 + 10 * 2 + mStartY;
        setMeasuredDimension(width, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "changed: " + changed + "l:" + l + "t:" + t + "r:" + r + "b:" + b);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        line_Paint.setColor(getResources().getColor(R.color.color_e0));
        line_Paint.setStrokeWidth(1.5f);

        text_Paint.setTextAlign(Paint.Align.CENTER);
        text_Paint.setColor(getResources().getColor(R.color.color_3));
        text_Paint.setTextSize(35);

        // 刻度描述及下面线
        int dis = (mScrollBarWidth - mThumbWidth) / (desc.length - 1);
        for (int i = 0; i < desc.length; i++) {
            canvas.drawText(desc[i], mThumbWidth / 2 + i * dis, 90, text_Paint);
            canvas.drawLine(mThumbWidth / 2 + i * dis, 100, mThumbWidth / 2 + i * dis, 190, line_Paint);
        }

        mScrollBarTop = mStartY + mThumbHeight / 2 - mScrollBarHeight / 2;
        mScrollBarBottom = mScrollBarTop + mScrollBarHeight;

        //不动条
        mScrollBarBgNormal.setBounds(mThumbWidth / 2, mScrollBarTop, mScrollBarWidth - mThumbWidth / 2, mScrollBarBottom);
        mScrollBarBgNormal.draw(canvas);

        //滑动条
        mScrollBarProgress.setBounds(mOffsetLow + mThumbWidth / 2, mScrollBarTop, mOffsetHigh + mThumbWidth / 2, mScrollBarBottom);
        mScrollBarProgress.draw(canvas);

        //底部滑块
        mThumbLow.setBounds(mOffsetLow, mScrollBarBottom + 10,
                mOffsetLow + mThumbWidth, mThumbHeight + mScrollBarBottom + 10);
        mThumbLow.draw(canvas);

        //顶部滑块
        mThumbHigh.setBounds(mOffsetHigh, mScrollBarTop - mThumbHeight - 10,
                mOffsetHigh + mThumbWidth, mScrollBarTop - 10);
        mThumbHigh.draw(canvas);

//        if (mBarChangeListener != null) {
        double progressLow = formatDouble(mOffsetLow * (mMax - mMin) / mDistance + mMin);
        double progressHigh = formatDouble(mOffsetHigh * (mMax - mMin) / mDistance + mMin);
        Log.d(TAG, "onDraw-->mOffsetLow: " + mOffsetLow + "  mOffsetHigh: " + mOffsetHigh + "  progressLow: " + progressLow + "  progressHigh: " + progressHigh);

        flow_Paint.setTextAlign(Paint.Align.CENTER);
        flow_Paint.setColor(Color.WHITE);
        flow_Paint.setTextSize(30);

        if (progressLow <= 15) {
            canvas.drawText(formatInt(progressLow) + "", mOffsetLow + mThumbWidth / 2,
                    mScrollBarBottom + mThumbHeight / 2 + 20, flow_Paint);
        } else {
            canvas.drawText("不限", mOffsetLow + mThumbWidth / 2,
                    mScrollBarBottom + mThumbHeight / 2 + 20, flow_Paint);
        }

        if (progressHigh <= 15) {
            canvas.drawText(formatInt(progressHigh) + "", mOffsetHigh + mThumbWidth / 2,
                    mScrollBarTop - mThumbHeight / 2 - 10, flow_Paint);
        } else {
            canvas.drawText("不限", mOffsetHigh + mThumbWidth / 2,
                    mScrollBarTop - mThumbHeight / 2 - 10, flow_Paint);
        }
        mProgressLow = (int) progressLow;
        mProgressHigh = (int) progressHigh;
        if (mBarChangeListener != null) {
            mBarChangeListener.onProgressChanged(this, progressLow, progressHigh, mProgressLow, mProgressHigh, mMax, mMin);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float startX = 0;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFlag = getAreaFlag(e);
                if (mFlag == CLICK_ON_LOW) {
                    mThumbLow.setState(STATE_PRESSED);
                } else if (mFlag == CLICK_ON_HIGH) {
                    mThumbHigh.setState(STATE_PRESSED);
                }
                startX = e.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mFlag == CLICK_ON_LOW) {
                    if (e.getX() < 0 || e.getX() <= mThumbWidth / 2) {
                        mOffsetLow = 0;
                    } else if (e.getX() > (mScrollBarWidth - mThumbWidth - mDuration * 3)) {
                        mOffsetLow = mDistance - mDuration * 3;
                        mOffsetHigh = mOffsetLow + mDuration * 3;
                    } else {
                        mOffsetLow = formatInt(e.getX() - (double) mThumbWidth / 2);

                        if (mOffsetHigh - mOffsetLow <= mDuration * 2) {

                            mOffsetHigh = (mOffsetLow + mDuration <= mDistance) ? (mOffsetLow + mDuration) : mDistance;
                        }

                    }
                } else if (mFlag == CLICK_ON_HIGH) {
                    if (e.getX() < mDuration + mThumbWidth / 2) {
                        mOffsetHigh = mThumbWidth / 2;
                        mOffsetLow = 0;
                    } else if (e.getX() > mScrollBarWidth - mThumbWidth + mDuration) {
                        mOffsetHigh = mDistance;
                    } else {
                        mOffsetHigh = formatInt(e.getX());

                        if (mOffsetHigh - mOffsetLow <= mDuration * 2) {

                            mOffsetLow = (mOffsetHigh - mDuration >= 0) ? (mOffsetHigh - mDuration) : 0;
                        }

                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mThumbLow.setState(STATE_NORMAL);
                mThumbHigh.setState(STATE_NORMAL);
                break;
        }

        setProgressLow(formatDouble(mOffsetLow * (mMax - mMin) / mDistance + mMin));
        setProgressHigh(formatDouble(mOffsetHigh * (mMax - mMin) / mDistance + mMin));

        return true;
    }

    public int getAreaFlag(MotionEvent e) {
        if (e.getY() >= mScrollBarBottom && e.getY() <= mScrollBarBottom + mThumbHeight + 10 &&
                e.getX() >= mOffsetLow && e.getX() <= mOffsetLow + mThumbWidth) {
            return CLICK_ON_LOW;
        } else if (e.getY() >= mScrollBarTop - mThumbHeight - 10 && e.getY() <= mScrollBarBottom
                && e.getX() >= mOffsetHigh && e.getX() <= mOffsetHigh + mThumbWidth) {
            return CLICK_ON_HIGH;
        } else if (!(e.getX() >= 0 && e.getX() <= mScrollBarWidth &&
                e.getY() >= mScrollBarBottom + mThumbHeight + 10 &&
                e.getY() <= mScrollBarTop - mThumbHeight - 10)) {
            return CLICK_OUT_AREA;
        } else {
            return CLICK_INVAILD;
        }
    }

    public void setProgressLow(double progressLow) {
        mOffsetLow = formatInt((progressLow - mMin) / (mMax - mMin) * mDistance);
        invalidate();
    }

    public void setProgressHigh(double progressHigh) {
        mOffsetHigh = formatInt((progressHigh - mMin) / (mMax - mMin) * mDistance);
        invalidate();
    }

    public int getProgressLow() {
        return mProgressLow;
    }

    public int getProgressHigh() {
        return mProgressHigh;
    }

    private int formatInt(double value) {
        BigDecimal bd = new BigDecimal(value);
        BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bd1.intValue();
    }

    public static double formatDouble(double pDouble) {
        BigDecimal bd = new BigDecimal(pDouble);
        BigDecimal bd1 = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        pDouble = bd1.doubleValue();
        return pDouble;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
        this.mBarChangeListener = mListener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(DoubleSeekBar seekBar, double progressLow,
                               double progressHigh, int mprogressLow, int mprogressHigh, double max, double min);
    }

}
