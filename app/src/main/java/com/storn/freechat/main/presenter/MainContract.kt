package com.storn.freechat.main.presenter

import android.graphics.Bitmap

/**
 * Created by tianshutong on 2017/6/30.
 */
interface MainContract {

    interface View {
        fun showProgress()
        fun hideProgress()
        fun setToolbarTitle(text: String)
        fun setBmpHeadView(bitmap: Bitmap)
        fun setResHeadView(resId: Int)
        fun updateHeadView(url: String)
        fun setNickName(text: String)
        fun closeDrawer()
        fun slideToMessage()
        fun slideToGroup()
        fun slideToFriends()
        fun slideToService()
        fun slideToSetting()
    }

    interface Presenter {
        fun initListener()
        fun exit2Click()
        fun queryMessageList()
        fun goProfileAct()
        fun goToChat(position: Int)
        fun deleteMessage(position: Int)
        fun goToGroup()
        fun goToFriends()
        fun goToSetting()
    }
}