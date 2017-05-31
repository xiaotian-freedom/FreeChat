package com.storn.freechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.common.common.Constants;
import com.common.util.PreferenceTool;
import com.storn.freechat.base.BaseActivity;

/**
 * Created by tianshutong on 2017/3/29.
 */

public class SplashActivity extends BaseActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        mHandler.post(new SplashRunnable());
    }

    private class SplashRunnable implements Runnable {

        @Override
        public void run() {
            boolean isLogin = PreferenceTool.getBoolean(Constants.LOGIN_STATUS, false);
            Intent intent = new Intent();
            Class<?> classZ = null;
            try {
                if (isLogin) {
                    classZ = Class.forName("com.storn.freechat.MainActivity");
                } else {
                    classZ = Class.forName("com.storn.freechat.login.ui.LoginActivity");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            intent.setClass(SplashActivity.this, classZ);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler = null;
        }
    }
}
