package com.storn.freechat.login.presenter

import android.content.Context

import com.storn.freechat.vo.UserVo

/**
 * 登录接口
 * Created by tianshutong on 2017/4/7.
 */

interface LoginContract {

    interface View {

        fun loadSuccess()

        fun loadFailed()

        fun checkFocusView()

        fun showProgress(active: Boolean)

        fun addAccountsToAutoComplete(list: List<String>)

        fun fillLastAccount(userName: String, password: String)

    }

    interface Presenter {

        fun goToMainAct()

        fun goToRegAct(view: android.view.View, s: String)

        fun getAccounts()

        fun saveAccount(userName: String, password: String)

        fun checkAccount(userName: String, password: String)

        fun attemptLogin(userName: String, password: String)

    }

    interface ILoginRepo {

        fun insertOrUpdateAccount(context: Context, userVo: UserVo)
    }

    interface ILoginListener {

        fun start()

        fun success()

        fun fail(error: String)

    }

    interface IChangePwdListener {
        fun start()

        fun success()

        fun fail(error: String)
    }
}
