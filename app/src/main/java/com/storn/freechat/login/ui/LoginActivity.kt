package com.storn.freechat.login.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import com.common.common.Constants
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
class LoginActivity : BeamBaseActivity<LoginPresenter>(), LoginContract.View, View.OnClickListener, TextView.OnEditorActionListener {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_PHONE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                ToastUtil.showToast(this, "授权失败,无法记录密码", R.id.login_form, Effects.standard)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == presenter.REQUEST_REGISTER && resultCode == Activity.RESULT_OK && data != null) {
            mAccountView.setText(data.getStringExtra(Constants.LOGIN_UNAME))
            mPasswordView.setText(data.getStringExtra(Constants.LOGIN_UPASS))
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
        mPasswordView.setOnEditorActionListener(this)
    }

    override fun fillLastAccount(userName: String, password: String) {
        if (!TextUtils.isEmpty(userName)) {
            mAccountView.setText(userName)
        }
        if (!TextUtils.isEmpty(password)) {
            mPasswordView.setText(password)
        }
    }

    override fun addAccountsToAutoComplete(list: List<String>) {
        val accountList = list
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, accountList)
        mAccountView.setAdapter(adapter)
    }

    override fun showProgress(active: Boolean) {
        if (active) {
            AnimationUtil.startAlphaAnim(Constants.ANIM_300, mProgressView, mLoginFormView)
        } else {
            AnimationUtil.startAlphaAnim(Constants.ANIM_300, mLoginFormView, mProgressView)
        }
        mProgressView.postDelayed({
            if (mProgressView.visibility == View.VISIBLE) {
                mProgressView.startAnim()
            }
        }, Constants.ANIM_500.toLong())
    }

    fun resetLoad() {
        SoftKeyBoardUtil.hideSoftKeyboard(this@LoginActivity)
        mProgressView.reset()
    }

    override fun loadSuccess() {
        SoftKeyBoardUtil.hideSoftKeyboard(this@LoginActivity)
        mProgressView.success()
    }

    override fun loadFailed() {
        SoftKeyBoardUtil.hideSoftKeyboard(this@LoginActivity)
        mProgressView.fail()
        mPasswordView.requestFocus()
        CommonUtil.showToast(this, R.string.error_incorrect_password)
    }

    override fun checkFocusView() {
        val userName = mAccountView.text.toString()
        val password = mPasswordView.text.toString()

        if (TextUtils.isEmpty(userName)) {
            mAccountView.requestFocus()
            CommonUtil.setShakeAnimation(mAccountView)
            return
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.requestFocus()
            CommonUtil.setShakeAnimation(mPasswordView)
            return
        }

        SoftKeyBoardUtil.hideSoftKeyboard(this)
        presenter.attemptLogin(userName, password)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.mLoginFormView -> checkFocusView()
            R.id.loginFab -> presenter.goToRegAct(loginFab, loginFab!!.transitionName)
            else -> {
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkFocusView()
            return true
        }
        return false
    }

    companion object {

        private val READ_PHONE = 100
    }

}

