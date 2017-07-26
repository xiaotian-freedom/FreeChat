package com.storn.freechat.login.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import com.common.common.Constants
import com.common.util.CommonUtil
import com.common.util.RegexUtil
import com.common.widget.toast.LoadToast
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.login.presenter.RegisterPresenter
import kotlinx.android.synthetic.main.activity_register.*

/**
 * 注册界面

 * @author tianshutong
 */
@RequiresPresenter(RegisterPresenter::class)
class RegisterActivity : BeamBaseActivity<RegisterPresenter>() {

    val mHandler = Handler(Looper.myLooper())
    var mLoadToast: LoadToast? = null
    var mState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        transparentStatusBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation()
        }
        initToast()
        initListener()
    }

    private fun initListener() {
        fab.setOnClickListener { animateRevealClose() }
        btGo.setOnClickListener {
            val uName = etUsername.text.toString()
            val pwd = etPassword.text.toString()
            if (uName.isEmpty()) {
                etUsername.requestFocus()
                registerScrollView.smoothScrollToTop()
                CommonUtil.setShakeAnimation(etUsername)
            } else if (!TextUtils.isEmpty(RegexUtil.filterUnChinese(uName))
                    && RegexUtil.checkChinese(RegexUtil.filterUnChinese(uName))) {
                etUsername.requestFocus()
                registerScrollView.smoothScrollToTop()
                CommonUtil.setShakeAnimation(etUsername)
                CommonUtil.showToast(this, R.string.error_account_with_chinese)
            } else if (pwd.isEmpty()) {
                etPassword.requestFocus()
                registerScrollView.smoothScrollToTop()
                CommonUtil.setShakeAnimation(etPassword)
            } else if (etRepeatPassword.text.toString().isEmpty()) {
                etRepeatPassword.requestFocus()
                registerScrollView.smoothScrollToTop()
                CommonUtil.setShakeAnimation(etRepeatPassword)
            } else if (pwd != etRepeatPassword.text.toString()) {
                etRepeatPassword.requestFocus()
                registerScrollView.smoothScrollToTop()
                CommonUtil.setShakeAnimation(etRepeatPassword)
                CommonUtil.showToast(this, R.string.error_diff_password)
            } else {
                presenter.doRegister(etUsername.text.toString(), etPassword.text.toString())
            }
        }
    }

    private fun transparentStatusBar() {
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    private fun ShowEnterAnimation() {
        val transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition)
        window.sharedElementEnterTransition = transition

        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(objects: Transition) {
                cvAdd!!.visibility = View.GONE
            }

            override fun onTransitionEnd(objects: Transition) {
                transition.removeListener(this)
                animateRevealShow()
            }

            override fun onTransitionCancel(objects: Transition) {

            }

            override fun onTransitionPause(objects: Transition) {

            }

            override fun onTransitionResume(objects: Transition) {

            }

        })
    }

    fun animateRevealShow() {
        val mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd!!.width / 2, 0, (fab!!.width / 2).toFloat(), cvAdd!!.height.toFloat())
        mAnimator.duration = Constants.ANIM_300.toLong()
        mAnimator.interpolator = AccelerateInterpolator()
        mAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }

            override fun onAnimationStart(animation: Animator) {
                cvAdd!!.visibility = View.VISIBLE
                super.onAnimationStart(animation)
            }
        })
        mAnimator.start()
    }

    fun animateRevealClose() {
        val mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd!!.width / 2, 0, cvAdd!!.height.toFloat(), (fab!!.width / 2).toFloat())
        mAnimator.duration = Constants.ANIM_300.toLong()
        mAnimator.interpolator = AccelerateInterpolator()
        mAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                cvAdd!!.visibility = View.INVISIBLE
                super.onAnimationEnd(animation)
                fab!!.setImageResource(R.mipmap.plus)
                if (mState) {
                    val data = Intent()
                    if (!TextUtils.isEmpty(etUsername.text.toString())) {
                        data.putExtra(Constants.LOGIN_UNAME, etUsername.text.toString())
                    }
                    if (!TextUtils.isEmpty(etPassword.text.toString())) {
                        data.putExtra(Constants.LOGIN_UPASS, etPassword.text.toString())
                    }

                    setResult(Activity.RESULT_OK, data)
                    finish()
                } else {
                    super@RegisterActivity.onBackPressed()
                }
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
            }
        })
        mAnimator.start()
    }

    override fun onBackPressed() {
        animateRevealClose()
    }

    fun initToast() {
        mLoadToast = LoadToast(this)
        mLoadToast!!.setBackgroundColor(resources.getColor(R.color.dialog_bg))
        mLoadToast!!.setProgressColor(resources.getColor(R.color.black_19))
    }

    fun showLoadingView() {
        mLoadToast!!.setText(resources.getString(R.string.tip_register_ing)).setTranslationY(100).show()
    }

    fun success() {
        mState = true
        mLoadToast!!.success()
    }

    fun error() {
        mState = false
        mLoadToast!!.error()
    }
}
