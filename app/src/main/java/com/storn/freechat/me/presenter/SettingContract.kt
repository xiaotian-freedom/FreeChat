package com.storn.freechat.me.presenter

/**
 * Created by tianshutong on 2017/7/13.
 */
interface SettingContract {

    interface View {
        fun initToolbar()
        fun initToast()
        fun showLoadToast(text: String)
        fun toastSuccess()
        fun toastError()
        fun showChatBg()
        fun hideChatBg()
        fun setChatBgAdapter()
    }

    interface Presenter {
        fun changeChatBg()
        fun changePwd()
        fun clearCache()
        fun loginOut()
    }
}