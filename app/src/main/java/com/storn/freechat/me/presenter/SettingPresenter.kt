package com.storn.freechat.me.presenter

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.AppCompatEditText
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.common.common.Constants
import com.common.util.*
import com.common.widget.ConfirmDialog
import com.jude.beam.expansion.BeamBasePresenter
import com.storn.freechat.R
import com.storn.freechat.login.presenter.LoginContract
import com.storn.freechat.login.ui.LoginActivity
import com.storn.freechat.manager.UserManager
import com.storn.freechat.me.ui.SettingAct
import kotlinx.android.synthetic.main.activity_home.*

/**
 * 设置
 * Created by tianshutong on 2017/7/13.
 */
class SettingPresenter : BeamBasePresenter<SettingAct>(), SettingContract.Presenter {

    //change pwd
    var pwdView: View? = null
    var pwdDialog: ConfirmDialog? = null
    var pwdOldEditText: AppCompatEditText? = null
    var pwdNewEditText: AppCompatEditText? = null
    var pwdConfirmEdit: AppCompatEditText? = null

    //login out
    var outDialog: ConfirmDialog? = null

    var showChatBg: Boolean = false

    val REQUEST_READ_STORAGE = 100

    override fun onCreateView(view: SettingAct) {
        super.onCreateView(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        pwdDialog = null
        outDialog = null
        pwdView = null
        pwdOldEditText = null
        pwdNewEditText = null
        pwdConfirmEdit = null
    }

    override fun changeChatBg() {
        if (showChatBg) {
            showChatBg = false
            view.hideChatBg()
        } else {
            showChatBg = true
            view.showChatBg()
        }
    }

    override fun changePwd() {
        showChangePwdDialog()
    }

    override fun clearCache() {
        if (!PermissionUtil.isReadStorage(view)) {
            ActivityCompat.requestPermissions(view, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_STORAGE)
            return
        }
        val cacheSize = FileUtil.getCacheFileSize(view)
        if (cacheSize == 0.0) {
            CommonUtil.showToast(view, R.string.clear_no_cache)
        } else {
            view.showLoadToast(view.resources.getString(R.string.clear_cache_ing))
            if (FileUtil.clearAllCache(view)) {
                view.mHandler!!.postDelayed({
                    view.toastSuccess()
                    CommonUtil.showToast(view, String.format(view.resources.getString(R.string.clear_yes_cache), FileUtil.getFormatSize(cacheSize)))
                }, Constants.DELAY_1000.toLong())
            } else {
                view.toastError()
            }
        }
    }

    override fun loginOut() {
        showLoginOutDialog()
    }

    fun showLoginOutDialog() {
        val builder = ConfirmDialog.Builder(view)
        builder.setTitle(R.string.setting_login_out_title)
        builder.setMessage(R.string.setting_login_out_message)
        builder.setAutoDismiss(true)
        builder.setContentPanelHeight(view.resources.getDimension(R.dimen.content_panel_height).toInt())
        builder.setPositiveButton(view.getString(R.string.confirm)) { _: DialogInterface, _: Int ->
            confirmLoginOut()
        }
        builder.setNegativeButton(view.getString(R.string.cancel)
        ) { _: DialogInterface, _: Int ->
        }
        if (outDialog == null) {
            outDialog = builder.create()
        }
        outDialog?.setCanceledOnTouchOutside(true)
        if (!outDialog!!.isShowing) {
            outDialog?.show()
        }
    }

    fun confirmLoginOut() {
        PreferenceTool.putBoolean(Constants.LOGIN_STATUS, false)
        PreferenceTool.commit()
        startActivity(Intent(view, LoginActivity::class.java))
        view.finish()
        view.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out)
    }

    fun showChangePwdDialog() {
        if (pwdView == null) {
            pwdView = LayoutInflater.from(view).inflate(R.layout.change_password_dialog_layout, view.rootView, false)
            pwdView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            pwdOldEditText = pwdView!!.findViewById(R.id.old_password) as AppCompatEditText
            pwdNewEditText = pwdView!!.findViewById(R.id.new_password) as AppCompatEditText
            pwdConfirmEdit = pwdView!!.findViewById(R.id.new_password_confirm) as AppCompatEditText
        }
        val builder = ConfirmDialog.Builder(view)
        builder.setTitle(R.string.setting_change_pwd)
        builder.setCustomView(pwdView)
        builder.setAutoDismiss(false)
        builder.setContentPanelHeight(view.resources.getDimension(R.dimen.content_panel_max_height).toInt())
        builder.setPositiveButton(view.getString(R.string.confirm)) { _: DialogInterface, _: Int ->

            val oldPwd = pwdOldEditText!!.text.toString().trim { it <= ' ' }
            val newPwd = pwdNewEditText!!.text.toString().trim { it <= ' ' }
            val confirmNewPwd = pwdConfirmEdit!!.text.toString().trim { it <= ' ' }
            confirmPwd(oldPwd, newPwd, confirmNewPwd)
        }
        builder.setNegativeButton(view.getString(R.string.cancel)
        ) { _: DialogInterface, _: Int ->
            SoftKeyBoardUtil.hideSoftKeyboard(view)
        }
        if (pwdDialog == null) {
            pwdDialog = builder.create()
        }
        pwdDialog?.setCanceledOnTouchOutside(true)
        if (!pwdDialog!!.isShowing) {
            pwdDialog?.show()
        }
    }

    fun confirmPwd(oldPwd: String, newPwd: String, newPwdConfirm: String) {
        if (TextUtils.isEmpty(oldPwd)) {
            CommonUtil.setShakeAnimation(pwdOldEditText)
        } else if (oldPwd != PreferenceTool.getString(Constants.LOGIN_UPASS)) {
            CommonUtil.showToast(view, view.getString(R.string.setting_old_pwd_error))
        } else {
            if (TextUtils.isEmpty(newPwd)) {
                CommonUtil.setShakeAnimation(pwdNewEditText)
            } else {
                if (newPwd.length < 4) {
                    CommonUtil.showToast(view, view.getString(R.string.setting_pwd_min_tip))
                } else {
                    if (newPwdConfirm != newPwd) {
                        CommonUtil.showToast(view, view.getString(R.string.setting_pwd_confirm_error))
                    } else {
                        UserManager.getInstance().changePassword(view, newPwd, ChangePwdListener())
                    }
                }
            }
        }
    }

    inner class ChangePwdListener : LoginContract.IChangePwdListener {
        override fun start() {
            view.showLoadToast(view.resources.getString(R.string.setting_change_pwd_ing))
        }

        override fun success() {
            if (pwdDialog != null && pwdDialog!!.isShowing) {
                pwdDialog?.dismiss()
            }
            view.mHandler!!.postDelayed({ view.toastSuccess() }, Constants.DELAY_500.toLong())
            view.mHandler!!.postDelayed({ loginOut() }, Constants.DELAY_1000.toLong())
        }

        override fun fail(error: String) {
            view.runOnUiThread { view.toastError() }
        }
    }
}