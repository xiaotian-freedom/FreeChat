package com.storn.freechat.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import com.common.common.Constants
import com.common.util.TimeUtil
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
        messageEntity.mId = currentTime.toInt()
        messageEntity.fromJid = fromJid
        messageEntity.myJid = toJid
        messageEntity.name = fromName
        messageEntity.content = content
        messageEntity.time = currentTime
        messageEntity.msgCount = mCount
        dbHelper.insertOrUpdateMessage(context, messageEntity)

        /*RxBusHelper.post(RxEvent(EventCode.REFRESH_MESSAGE_LIST))
        val homeActivity = HomeActivity()
        if (homeActivity.mHandler != null) {
            val refreshMessage = android.os.Message()
            refreshMessage.what = Constants.REFRESH_MESSAGE
            homeActivity.mHandler?.sendMessage(refreshMessage)
        }*/
        val intent = Intent()
        intent.action = Constants.LOCAL_ACTION
        intent.putExtra("category", Constants.REFRESH_MESSAGE)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        if (ChatRoomAct.chatMessageHandler != null) {
            val addMessage = android.os.Message()
            addMessage.what = Constants.ADD_CHAT_MESSAGE
            addMessage.obj = chatMessageEntity
            ChatRoomAct.chatMessageHandler.sendMessage(addMessage)
        }
    }

}