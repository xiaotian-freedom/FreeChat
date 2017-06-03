package com.storn.freechat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
import android.transition.Fade
import android.view.WindowManager
import com.common.common.Constants
import com.common.util.PreferenceTool
import com.storn.freechat.base.BaseActivity

/**
 * Created by tianshutong on 2017/3/29.
 */

class SplashActivity : BaseActivity() {

    private var mHandler: Handler? = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        setupWindowTransition()
        init()
    }

    private fun init() {
        mHandler!!.postDelayed(SplashRunnable(), Constants.DELAY_1000.toLong())
    }

    private fun setupWindowTransition() {
        val fadeIn = Fade(Fade.IN)
        val fadeOut = Fade(Fade.OUT)
        window.enterTransition = fadeIn
        window.exitTransition = fadeOut
    }

    private inner class SplashRunnable : Runnable {

        override fun run() {
            val isLogin = PreferenceTool.getBoolean(Constants.LOGIN_STATUS, false)
            val intent = Intent()
            var classZ: Class<*>? = null
            try {
                if (isLogin) {
                    classZ = Class.forName("com.storn.freechat.HomeActivity")
                } else {
                    classZ = Class.forName("com.storn.freechat.login.ui.LoginActivity")
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

            intent.setClass(this@SplashActivity, classZ)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity).toBundle())
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mHandler != null) {
            mHandler = null
        }
    }
}
