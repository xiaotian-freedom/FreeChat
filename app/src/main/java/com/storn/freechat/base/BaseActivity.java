package com.storn.freechat.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.storn.freechat.util.ActivityManagerUtil;

/**
 * 基类
 * Created by tianshutong on 2017/3/29.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerUtil.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
