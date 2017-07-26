package com.storn.freechat.chat.utils

import android.content.Context
import android.text.TextUtils
import com.common.common.Constants
import com.common.util.TimeUtil
import com.storn.freechat.chat.ui.ChatRoomAct
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.ChatMessageEntityVo
import com.storn.freechat.vo.MessageEntityVo

/**
 * 保存消息类
 * Created by tianshutong on 2017/7/8.
 */
class SaveMsgUtil(val mContext: Context, val fromJid: String, val myJid: String,
                  val fromName: String, val mMessageVo: MessageEntityVo,
                  val mType: Int, val messageType: Int, val status: Int, val msgCount: Int,
                  val mContent: String?, val audioPath: String?, val audioTime: Long,
                  val mImgPath: String?) {

    fun save() {
        val currentTime = TimeUtil.getCurrentTime()
        saveChatMessage(currentTime)
        saveMessage(currentTime)
    }

    fun saveChatMessage(currentTime: Long) {
        val chatMessageEntity = ChatMessageEntityVo()
        chatMessageEntity.cId = currentTime.toInt()
        chatMessageEntity.fromJid = fromJid
        chatMessageEntity.myJid = myJid
        chatMessageEntity.type = mType
        if (!TextUtils.isEmpty(mContent)) {
            chatMessageEntity.content = mContent
        }
        if (!TextUtils.isEmpty(audioPath)) {
            chatMessageEntity.audioPath = audioPath
            chatMessageEntity.audioTime = audioTime
        }
        if (!TextUtils.isEmpty(mImgPath)) {
            chatMessageEntity.imgPath = mImgPath
        }
        chatMessageEntity.time = currentTime
        chatMessageEntity.messageType = messageType
        chatMessageEntity.status = status
        DBHelper.getInstance().insertOrUpdateChatMessage(mContext, chatMessageEntity)

        if (ChatRoomAct.chatHandler != null) {
            val addMessage = android.os.Message.obtain()
            addMessage.what = Constants.ADD_CHAT_MESSAGE
            addMessage.obj = chatMessageEntity
            ChatRoomAct.chatHandler?.sendMessage(addMessage)
        }
    }

    fun saveMessage(currentTime: Long) {
        mMessageVo.mId = currentTime.toInt()
        mMessageVo.jid = fromJid
        mMessageVo.myJid = myJid
        mMessageVo.fromName = fromName
        if (!TextUtils.isEmpty(mContent)) {
            mMessageVo.content = mContent
        }
        mMessageVo.time = currentTime
        mMessageVo.msgCount = msgCount
        mMessageVo.type = 0
        mMessageVo.messageType = messageType
        mMessageVo.status = status
        DBHelper.getInstance().insertOrUpdateMessage(mContext, mMessageVo)

        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message.obtain()
            homeMessage.what = Constants.REFRESH_MESSAGE
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
    }

}