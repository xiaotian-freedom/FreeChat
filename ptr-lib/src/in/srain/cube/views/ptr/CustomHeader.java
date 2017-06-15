package in.srain.cube.views.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by tianshutong on 16/3/16.
 */
public class CustomHeader extends FrameLayout implements PtrUIHandler {

    private ImageView lineView;
    private ImageView loadingView;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private RotateAnimation mRotateAnimation;
    private int mRotateAniTime = 150;
    private int mLoadingAniTime = 1000;
    private static final int DEFAULT_HEIGHT = 30;
    private MyHandler myHandler;


    public CustomHeader(Context context) {
        super(context);
        initViews(null);
    }

    public CustomHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public CustomHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    protected void initViews(AttributeSet attrs) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.PtrClassicHeader, 0, 0);
        if (arr != null) {
            mRotateAniTime = arr.getInt(R.styleable.PtrClassicHeader_ptr_rotate_ani_time, mRotateAniTime);
            arr.recycle();
        }
        buildAnimation();
        View header = LayoutInflater.from(getContext()).inflate(R.layout.custom_refresh_header, this);

        loadingView = (ImageView) header.findViewById(R.id.ptr_classic_header_rotate_view);

        lineView = (ImageView) header.findViewById(R.id.ptr_classic_header_rotate_view_header);

        resetView();

        myHandler = new MyHandler(this);
    }

    private void resetView() {
        loadingView.clearAnimation();
        loadingView.setVisibility(GONE);
//        lineView.setVisibility(GONE);
//        lineView.setLayoutParams(new FrameLayout.LayoutParams(2, DEFAULT_HEIGHT));
    }

    private void buildAnimation() {
        mFlipAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);

        mRotateAnimation = new RotateAnimation(0, 359, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(mLoadingAniTime);
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.setFillAfter(true);
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        resetView();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        lineView.setVisibility(VISIBLE);
        lineView.setBackgroundColor(Color.RED);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) lineView.getLayoutParams();
        layoutParams.height = 100;
        layoutParams.width = 1;
        lineView.setLayoutParams(layoutParams);

        loadingView.setVisibility(VISIBLE);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
//        lineView.setLayoutParams(new FrameLayout.LayoutParams(2, DEFAULT_HEIGHT));
//        lineView.setVisibility(VISIBLE);
        loadingView.setVisibility(VISIBLE);
        loadingView.clearAnimation();
        loadingView.startAnimation(mRotateAnimation);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                if (loadingView != null) {
                    loadingView.clearAnimation();
                    loadingView.startAnimation(mReverseFlipAnimation);
                }
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                if (loadingView != null) {
                    loadingView.clearAnimation();
                    loadingView.startAnimation(mFlipAnimation);
                }
            }
        }

        Message message = new Message();
        message.arg1 = currentPos;
        myHandler.sendMessage(message);
    }

    private static class MyHandler extends Handler {
        private CustomHeader theInstance;

        public MyHandler(CustomHeader instance) {
            WeakReference<CustomHeader> wrInstance = new WeakReference<>(instance);
            theInstance = wrInstance.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int scrollY = msg.arg1;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) theInstance.lineView.getLayoutParams();
            layoutParams.height = scrollY;
            layoutParams.width = 2;
            theInstance.lineView.setLayoutParams(layoutParams);
        }
    }
}
