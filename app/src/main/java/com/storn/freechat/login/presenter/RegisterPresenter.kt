package com.storn.freechat.login.presenter

import com.common.common.Constants
import com.common.util.CommonUtil
import com.jude.beam.expansion.BeamBasePresenter
import com.storn.freechat.R
import com.storn.freechat.login.ui.RegisterActivity
import com.storn.freechat.manager.XMPPConnectionManager

/**
 * 注册Presenter
 * Created by tianshutong on 2017/7/1.
 */
class RegisterPresenter : BeamBasePresenter<RegisterActivity>(), RegisterContract.Presenter,
        LoginContract.ILoginListener {

    override fun onCreateView(view: RegisterActivity) {
        super.onCreateView(view)
    }

    override fun doRegister(uName: String, pwd: String) {
        XMPPConnectionManager.getInstance().register(uName, pwd, this)
    }

    override fun start() {
        view.showLoadingView()
    }

    override fun success() {
        view.mHandler.postDelayed({
            view.success()
        }, Constants.DELAY_1000.toLong())
        view.mHandler.postDelayed({
            view.onBackPressed()
        }, Constants.DELAY_2000.toLong())
    }

    override fun fail(error: String) {
        view.runOnUiThread {
            view.error()
            if (error == "XMPPError: conflict - cancel") {
                CommonUtil.showToast(view, R.string.error_register_conflict)
            } else if (error == "No response received within reply timeout. Timeout was 10000ms (~10s). Used filter: No filter used or filter was 'null'.") {
                CommonUtil.showToast(view, R.string.error_timeout)
            }
        }
    }
}