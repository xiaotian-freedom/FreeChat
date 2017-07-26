package com.storn.freechat.group

/**
 * Created by tianshutong on 2017/7/14.
 */
interface GroupContract {
    interface View {
        fun initToolbar()
        fun initToast()
        fun showLoadToast(text: String)
        fun toastSuccess()
        fun toastError()
        fun finishGroupRefresh()
        fun startRotation()
        fun endRotation()
    }

    interface Presenter {
        fun initListener()
        fun queryLocalChatRooms()
        fun getChatRooms()
        fun showChatRoomDialog()
        fun createChatRooms(roomName: String)
        fun goMultiChat(position: Int)
        fun fabClickListener()
    }
}