package com.storn.freechat.chat.presenter

/**
 * Created by tianshutong on 2017/7/7.
 */
interface ChatContract {

    interface ChatView {
        fun changeChatBg()
        fun initToolbar()
        fun setToolbarTitle(name: String)
        fun changePlus(content: String)
        fun changeChatType(type: Int)
    }

    interface IChatPresenter {
        fun initChat()
        fun initData()
        fun initListener()
        fun clearMsgTip()
        fun getChatMessage()
        fun queryChatMessage(jid: String, myJid: String, offset: Int, limit: Int)
        fun sendMessage(position: Int, msg: String)
    }
}