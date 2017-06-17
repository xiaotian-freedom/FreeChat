package com.storn.freechat.service

import android.content.Context
import android.text.TextUtils
import com.common.common.Constants
import com.common.util.TimeUtil
import com.storn.freechat.HomeActivity
import com.storn.freechat.chat.ChatRoomAct
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.ChatMessageEntityVo
import com.storn.freechat.vo.MessageEntityVo
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManagerListener
import org.jivesoftware.smack.packet.Message

/**
 * 聊天信息监听类
 * Created by tianshutong on 2017/6/10.
 */
class MySingleChatListener(val context: Context) : ChatManagerListener {

    override fun chatCreated(chat: Chat, createdLocally: Boolean) {
        chat.addMessageListener({ _: Chat, message: Message ->
            if (!TextUtils.isEmpty(message.body)) {
                saveAndRefreshMessage(Constants.CHAT_MESSAGE_TYPE_FROM, message)
            }
        })
    }

    private fun saveAndRefreshMessage(type: Int, message: Message) {
        val content: String = message.body
        val fromJid: String = message.from
        val toJid: String = message.to
        val fromName: String = fromJid.split("@")[0]

        //save to chat list
        val currentTime = TimeUtil.getCurrentTime()
        val chatMessageEntity = ChatMessageEntityVo()
        chatMessageEntity.cId = currentTime.toInt()
        chatMessageEntity.fromJid = fromJid
        chatMessageEntity.myJid = toJid
        chatMessageEntity.type = type
        chatMessageEntity.content = content
        chatMessageEntity.time = currentTime
        val dbHelper = DBHelper.getInstance()
        dbHelper.insertOrUpdateChatMessage(context, chatMessageEntity)

        //save to message list
        var mCount: Int = 0
        var messageEntity = DBHelper.getInstance().querySingleMessageByJid(context, fromJid)
        if (messageEntity == null) {
            messageEntity = MessageEntityVo()
        } else {
            mCount = ++messageEntity.msgCount
        }
        if (ChatRoomAct.chatHandler != null) {
            mCount = 0
        }
        messageEntity.mId = currentTime.toInt()
        messageEntity.fromJid = fromJid
        messageEntity.myJid = toJid
        messageEntity.name = fromName
        messageEntity.content = content
        messageEntity.time = currentTime
        messageEntity.msgCount = mCount
        dbHelper.insertOrUpdateMessage(context, messageEntity)

        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message.obtain()
            homeMessage.what = Constants.REFRESH_MESSAGE
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
        if (ChatRoomAct.chatHandler != null) {
            val addMessage = android.os.Message.obtain()
            addMessage.what = Constants.ADD_CHAT_MESSAGE
            addMessage.obj = chatMessageEntity
            ChatRoomAct.chatHandler?.sendMessage(addMessage)
        }
    }

}