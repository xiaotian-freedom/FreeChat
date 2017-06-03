package com.storn.freechat.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.storn.freechat.util.ActivityManagerUtil

/**
 * 基类
 * Created by tianshutong on 2017/3/29.
 */

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManagerUtil.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
