package com.storn.freechat.login.presenter

/**
 * Created by tianshutong on 2017/7/1.
 */
interface RegisterContract {

    interface Presenter {
        fun doRegister(uName: String, pwd: String)
    }
}