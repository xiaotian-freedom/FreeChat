package com.storn.freechat.service

import android.content.Context
import android.text.TextUtils
import com.common.common.Constants
import com.common.util.TimeUtil
import com.storn.freechat.HomeActivity
import com.storn.freechat.chat.MultiChatRoomAct
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.MessageEntityVo
import com.storn.freechat.vo.MultiChatEntityVo
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message

/**
 * 多人聊天监听类
 * Created by tianshutong on 2017/6/19.
 */
class MultiChatListener(val context: Context, val roomJid: String) : MessageListener {

    override fun processMessage(message: Message?) {
        if (TextUtils.isEmpty(message!!.body)) return
        if (message.from.split("/")[1] == message.to.split("@")[0]) return

        //save to multi chat list
        val currentTime = TimeUtil.getCurrentTime()
        val multiChatEntityVo = MultiChatEntityVo()
        multiChatEntityVo.cId = currentTime.toInt()
        multiChatEntityVo.myJid = message.to
        multiChatEntityVo.roomJid = roomJid
        multiChatEntityVo.roomName = message.from.split("@")[0]
        multiChatEntityVo.fromName = message.from.split("/")[1]
        multiChatEntityVo.content = message.body
        multiChatEntityVo.time = currentTime
        multiChatEntityVo.type = Constants.CHAT_MESSAGE_TYPE_FROM
        DBHelper.getInstance().insertOrUpdateMultiChatMessage(context, multiChatEntityVo)

        //save to message list
        var mCount: Int = 0
        var messageEntity = DBHelper.getInstance().querySingleMessageByJid(context, roomJid)
        if (messageEntity == null) {
            messageEntity = MessageEntityVo()
        } else {
            mCount = ++messageEntity.msgCount
        }
        if (MultiChatRoomAct.chatHandler != null) {
            mCount = 0
        }
        messageEntity.mId = currentTime.toInt()
        messageEntity.jid = roomJid
        messageEntity.myJid = message.to
        messageEntity.roomName = message.from.split("@")[0]
        messageEntity.fromName = message.from.split("/")[1]
        messageEntity.content = message.body
        messageEntity.time = currentTime
        messageEntity.msgCount = mCount
        messageEntity.type = 1
        DBHelper.getInstance().insertOrUpdateMessage(context, messageEntity)

        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message.obtain()
            homeMessage.what = Constants.REFRESH_MESSAGE
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
        if (MultiChatRoomAct.chatHandler != null) {
            val addMessage = android.os.Message.obtain()
            addMessage.what = Constants.ADD_CHAT_MESSAGE
            addMessage.obj = multiChatEntityVo
            MultiChatRoomAct.chatHandler?.sendMessage(addMessage)
        }
    }

}