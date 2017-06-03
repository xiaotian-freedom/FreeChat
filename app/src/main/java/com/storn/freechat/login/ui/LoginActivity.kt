package com.storn.freechat.login.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import com.common.util.*
import com.gitonway.lee.niftynotification.lib.Effects
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.login.presenter.LoginContract
import com.storn.freechat.login.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
@RequiresPresenter(LoginPresenter::class)
class LoginActivity : BeamBaseActivity<LoginPresenter>(), LoginContract.View, View.OnClickListener {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_PHONE) {
            if (grantResults.isNotEmpty() && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                ToastUtil.showToast(this, "授权失败,无法记录密码", R.id.login_form, Effects.standard)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        transparentStatusBar()
        blurBg()
        initData()
        initListener()
    }

    private fun blurBg() {
        val resourceBmp = BitmapFactory.decodeResource(resources, R.mipmap.bg)
        val blurBmp = FastBlur.blurBitmap(this, resourceBmp, 20f)
        blurBackground!!.setImageBitmap(blurBmp)
    }

    private fun transparentStatusBar() {
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    private fun initData() {
        if (!PermissionUtil.isReadPhone(this)) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE), 100)
        }
    }

    private fun initListener() {
        loginFab.setOnClickListener(this)
        mLoginFormView.setOnClickListener(this)
        mPasswordView!!.setOnEditorActionListener { _: TextView, id: Int, _: KeyEvent ->
            if (id == R.id.mLoginFormView || id == EditorInfo.IME_NULL) {
                checkFocusView()
                true
            } else
                false
        }
    }

    override fun fillLastAccount(userName: String, password: String) {
        if (!TextUtils.isEmpty(userName)) {
            mAccountView!!.setText(userName)
        }
        if (!TextUtils.isEmpty(password)) {
            mPasswordView!!.setText(password)
        }
    }

    override fun addAccountsToAutoComplete(list: List<String>) {
        val accountList = list
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, accountList)
        mAccountView!!.setAdapter(adapter)
    }

    override fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
        mLoginFormView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
        mProgressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
        if (mProgressView!!.visibility == View.VISIBLE) {
            mProgressView!!.postDelayed({ mProgressView!!.startAnim() }, 200)
        }
    }

    override fun loadSuccess() {
        SoftKeyBoardUtil.hideSoftKeyboard(this@LoginActivity)
        mProgressView!!.success()
    }

    override fun loadFailed() {
        SoftKeyBoardUtil.hideSoftKeyboard(this@LoginActivity)
        mProgressView!!.fail()
        mPasswordView!!.error = getString(R.string.error_incorrect_password)
        mPasswordView!!.requestFocus()
    }

    override fun checkFocusView() {
        var cancel = false
        var focusView: View? = null

        // Reset errors.
        mAccountView!!.error = null
        mPasswordView!!.error = null

        val userName = mAccountView!!.text.toString()
        val password = mPasswordView!!.text.toString()

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            focusView = mAccountView
            cancel = true
        }
        if (TextUtils.isEmpty(password)) {
            focusView = mAccountView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
            CommonUtil.setShakeAnimation(focusView)
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            SoftKeyBoardUtil.hideSoftKeyboard(this)
            presenter.attemptLogin(userName, password)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.mLoginFormView -> checkFocusView()
            R.id.loginFab -> presenter.goToRegAct(loginFab, loginFab!!.transitionName)
            else -> {
            }
        }
    }

    companion object {

        private val READ_PHONE = 100
    }

}

