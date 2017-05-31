package com.common.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

public class DensityUtil {

	/**
	 * 得到设备屏幕的宽度
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 得到设备屏幕的高度
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 得到设备的密度
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = getScreenDensity(context);
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = getScreenDensity(context);
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取状态栏高度
	 * @param v
	 * @return
     */
	public static int getStatusBarHeight(View v) {
		if (v == null) {
			return 0;
		}
		Rect frame = new Rect();
		v.getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}
}
