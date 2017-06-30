package com.storn.freechat.me.presenter

/**
 * Created by tianshutong on 2017/6/28.
 */
interface ModifyContract {

    interface View {

        fun initToolBar()
        fun fillData()
        fun updateCount(text: String)
    }

    interface Presenter {
        fun initListener()
        fun getTitle(): String
        fun getInfo(): String
        fun saveInfo()
    }
}