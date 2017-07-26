package com.storn.freechat.contact.presenter

/**
 * Created by tianshutong on 2017/7/14.
 */
interface FriendsContract {

    interface View {
        fun initToolbar()
        fun finishFriendsRefresh()
    }

    interface Presenter {
        fun queryLocalFriends()
        fun getFriendsData()
        fun goChatRoom(groupPosition: Int, childPosition: Int)
    }
}