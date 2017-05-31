package com.common.util;

import android.app.Activity;
import android.view.Gravity;

import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;

/**
 * 自定义顶部消息通知
 * Created by tianshutong on 16/7/19.
 */

public class ToastUtil {

    private static final int ANIM_DURATION = 500;

    private static final int DISPLAY_DURATION = 1000;

    public static void showToast(Activity context, String msg, int resId, Effects effects) {
        Configuration config = new Configuration.Builder()
                .setAnimDuration(ANIM_DURATION)
                .setDispalyDuration(DISPLAY_DURATION)
                .setBackgroundColor("#FF8200")
                .setTextColor("#FFFFFF")
                .setIconBackgroundColor("#FFFFFFFF")
                .setTextPadding(10)
                .setViewHeight(64)
                .setTextLines(1)
                .setTextGravity(Gravity.CENTER)
                .build();

        NiftyNotificationView.build(context, msg, effects, resId, config)
                .show();
    }
}
