package com.storn.freechat.chat

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.common.common.Constants
import com.common.util.PreferenceTool
import com.common.util.TimeUtil
import com.common.widget.TopAutoRefreshListView
import com.jaeger.library.StatusBarUtil
import com.storn.freechat.HomeActivity
import com.storn.freechat.R
import com.storn.freechat.adapter.ChatMessageAdapter
import com.storn.freechat.base.BaseActivity
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.ChatMessageEntityVo
import com.storn.freechat.vo.FriendsEntityVo
import com.storn.freechat.vo.MessageEntityVo
import kotlinx.android.synthetic.main.activity_chat_room.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.chat.ChatManager

/**
 * 聊天室
 * Created by tianshutong on 2016/12/9.
 */

class ChatRoomAct : BaseActivity(), TextWatcher, View.OnClickListener, TopAutoRefreshListView.onTopRefreshListener {

    private var mOffset = 0
    private val mLimit = 10
    private var friendInfo = FriendsEntityVo()
    private var mMessageVo = MessageEntityVo()
    private var chatMessageAdapter: ChatMessageAdapter? = null

    //聊天监听
    private var mChatManager: ChatManager? = null

    companion object {
        var chatHandler: ChatHandler? = null
    }

    init {
        chatHandler = ChatHandler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_y_e2), 0)
        initToolbar()
        initData()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        chatHandler = null
    }

    private fun initToolbar() {
        mainToolBar!!.setNavigationIcon(R.mipmap.white_arrow_left)
    }

    private fun initData() {

        val data = this.intent
        mMessageVo = data.getSerializableExtra(Constants.MESSAGEVO) as MessageEntityVo
        friendInfo.jid = mMessageVo.fromJid
        friendInfo.name = mMessageVo.name
        setToolbarTitle(mMessageVo.name)
        getChatMessage()
        clearMsgTip()
    }

    private fun initListener() {

        mainToolBar!!.setNavigationOnClickListener { _ -> finish() }
        mainToolBarRight!!.setOnClickListener { _ ->

        }
        chatRoomSend!!.setOnClickListener(this)
        chatRoomEdit!!.addTextChangedListener(this)

        refreshableView!!.setAutoRefreshEnabled(true)
        refreshableView!!.setOnTopRefreshListener(this)

    }

    private fun initChat() {
        val connection = XMPPConnectionManager.getInstance().connection
        mChatManager = ChatManager.getInstanceFor(connection)
    }

    /**
     * 设置标题名

     * @param name
     */
    private fun setToolbarTitle(name: String) {
        if (!TextUtils.isEmpty(name)) {
            mainToolBarTitle!!.text = name
        }
    }

    private fun clearMsgTip() {
        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message()
            homeMessage.what = Constants.CLEAR_MESSAGE_TIP
            homeMessage.obj = mMessageVo
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
    }

    private fun refreshMsg() {
        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message()
            homeMessage.what = Constants.REFRESH_MESSAGE
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
    }

    private fun getChatMessage() {
        var myJid = PreferenceTool.getString(Constants.LOGIN_JID)
        if (TextUtils.isEmpty(myJid)) {
            myJid = ChatApplication.getUserVo().jid
        }
        if (TextUtils.isEmpty(myJid)) return
        queryChatMessage(friendInfo.jid, myJid, mOffset, mLimit)
    }

    /**
     * 查询聊天信息

     * @param fromJid
     * *
     * @param myJid
     */
    private fun queryChatMessage(fromJid: String, myJid: String, offset: Int, limit: Int) {
        val oldMessageList = DBHelper.getInstance()
                .queryChatMessageByJid(this, myJid, fromJid, offset, limit)
        setChatAdapter(oldMessageList)
    }

    private fun setChatAdapter(messageList: MutableList<ChatMessageEntityVo>) {
        if (chatMessageAdapter == null) {
            chatMessageAdapter = ChatMessageAdapter(this, messageList)
            refreshableView!!.adapter = chatMessageAdapter
            refreshableView!!.setSelection(chatMessageAdapter!!.count)
        } else {
            refreshableView!!.postDelayed({
                refreshableView!!.onRefreshComplete()
                if (messageList.size < mLimit) {
                    refreshableView!!.onLoadFinish()
                }
                chatMessageAdapter!!.addChatList(messageList)
                refreshableView!!.setSelection(messageList.size + 1)

            }, Constants.DELAY_1000.toLong())
        }
    }

    /**
     * 发送消息

     * @param msg
     */
    private fun sendMessage(msg: String) {
        initChat()

        Thread {
            val newChat = mChatManager!!.createChat(friendInfo.jid)
            try {
                newChat.sendMessage(msg)
                saveAndRefreshMessage(Constants.CHAT_MESSAGE_TYPE_TO, msg)
            } catch (e: SmackException.NotConnectedException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun saveAndRefreshMessage(type: Int, content: String) {
        val mUserVo = ChatApplication.getUserVo()

        //save to chat list
        val currentTime = TimeUtil.getCurrentTime()
        val chatMessageEntity = ChatMessageEntityVo()
        chatMessageEntity.cId = currentTime.toInt()
        chatMessageEntity.fromJid = friendInfo.jid
        chatMessageEntity.myJid = mUserVo.jid
        chatMessageEntity.type = type
        chatMessageEntity.content = content
        chatMessageEntity.time = currentTime
        val dbHelper = DBHelper.getInstance()
        dbHelper.insertOrUpdateChatMessage(this, chatMessageEntity)

        //save to message list
        mMessageVo.mId = currentTime.toInt()
        mMessageVo.fromJid = friendInfo.jid
        mMessageVo.myJid = mUserVo.jid
        mMessageVo.name = friendInfo.name
        mMessageVo.content = content
        mMessageVo.time = currentTime
        mMessageVo.msgCount = 0
        dbHelper.insertOrUpdateMessage(this, mMessageVo)

        val addMessage = Message.obtain()
        addMessage.what = Constants.ADD_CHAT_MESSAGE
        addMessage.obj = chatMessageEntity
        chatHandler?.sendMessage(addMessage)

        refreshMsg()
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun afterTextChanged(editable: Editable) {
        val content = chatRoomEdit!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(content)) {
            chatRoomSend!!.visibility = View.GONE
            chatRoomImgPlus!!.visibility = View.VISIBLE
        } else {
            chatRoomSend!!.visibility = View.VISIBLE
            chatRoomImgPlus!!.visibility = View.GONE
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.chatRoomImgVoice -> {
            }
            R.id.chatRoomImgPlus -> {
            }
            R.id.chatRoomSend -> {
                val content = chatRoomEdit!!.text.toString().trim { it <= ' ' }
                sendMessage(content)
            }
            else -> {
            }
        }
    }

    override fun onRefresh() {
        mOffset += mLimit
        getChatMessage()
    }

    /**
     * 处理消息
     */
    inner class ChatHandler : Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                Constants.ADD_CHAT_MESSAGE -> {
                    val chatMessageEntity = msg.obj as ChatMessageEntityVo
                    chatMessageAdapter!!.addChatData(chatMessageEntity)
                    refreshableView!!.setSelection(chatMessageAdapter!!.count)
                    chatRoomEdit!!.setText("")
                }
            }
        }
    }

}
