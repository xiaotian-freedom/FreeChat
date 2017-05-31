package com.storn.freechat.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.common.util.SoftKeyBoardUtil;

public class KeySoftListView extends AutoRefreshListView implements OnGestureListener, OnTouchListener {

	private GestureDetector mGestureDetector;
	private Activity activity;
	
	public KeySoftListView(Context context) {
		super(context);
	}

	public KeySoftListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KeySoftListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void bindActivity(Activity activity) {
		this.activity = activity;
		initGesture();
	}
	
	private void initGesture() {
		mGestureDetector = new GestureDetector(activity, this);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		SoftKeyBoardUtil.hideSoftKeyboard(activity);
//		if (activity instanceof ChatActivity) {
//			((ChatActivity) activity).hideEmotionLayout();
//			((ChatActivity) activity).hideBottomLayout();
//		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

}
