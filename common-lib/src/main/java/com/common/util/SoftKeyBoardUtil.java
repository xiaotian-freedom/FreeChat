package com.common.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘显示与隐藏
 * Created by tianshutong on 16/7/22.
 */

public class SoftKeyBoardUtil {

    /**
     * 强制显示软键盘
     *
     * @param editText EditText
     */
    public static void showKeyBoard(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏键盘
     *
     * @param activity Activity
     */
    public static boolean hideSoftKeyboard(Activity activity) {
        if (activity == null) {
            return false;
        }

        if (activity.getCurrentFocus() != null) {
            IBinder ib = activity.getCurrentFocus().getWindowToken();
            if (null != ib) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != imm) {
                    return imm.hideSoftInputFromWindow(ib, 0);
                }
            }
        }
        return false;
    }

    /**
     * 显示键盘
     *
     * @param activity Activity
     */
    public static void showSoftKeyboard(final Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Service.INPUT_METHOD_SERVICE);
                if (null != imm) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 30);
    }

    /**
     * 此方法并不会显示键盘,使用最上面的方法
     * 强制显示键盘
     *
     * @param activity Activity
     * @param v        View
     */
    public static void showSoftKeyboard(Activity activity, View v) {
        if (activity == null || v == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 强制隐藏键盘
     *
     * @param activity Activity
     * @param v        View
     */
    public static void hideSoftKeyboard(Activity activity, View v) {
        if (activity == null || v == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 监测是否弹出键盘
     *
     * @param activity Activity
     * @param listener OnSoftKeyboardChangeListener
     */
    public static void observeSoftKeyboard(Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - displayHeight;
                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(height, keyboardHeight, !hide);
                }

                previousKeyboardHeight = height;

            }
        });
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int decorViewHeiht, int softKeybardHeight, boolean visible);
    }

}
