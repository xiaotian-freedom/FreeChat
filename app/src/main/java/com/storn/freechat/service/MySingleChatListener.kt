package com.storn.freechat.service

import android.content.Context
import android.text.TextUtils
import com.common.common.Constants
import com.storn.freechat.chat.ui.ChatRoomAct
import com.storn.freechat.chat.utils.SaveMsgUtil
import com.storn.freechat.util.DBHelper
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
                val content: String = message.body
                var toJid: String = message.to
                if (!toJid.contains("Smack")) {
                    toJid += "/Smack"
                }
                val fromJid: String = message.from.split("/")[0]
                val fromName: String = fromJid.split("@")[0]
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

                SaveMsgUtil(context, fromJid, toJid, fromName, messageEntity, Constants.CHAT_MESSAGE_TYPE_FROM,
                        Constants.CHAT_MESSAGE_TXT, Constants.CHAT_SEND_SUCCESS, mCount, content, "", 0, "").save()
            }
        })
    }

}