package com.storn.freechat.chat

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import com.common.common.Constants
import com.common.util.DensityUtil
import com.common.util.PreferenceTool
import com.common.util.SoftKeyBoardUtil
import com.common.util.TimeUtil
import com.common.widget.TopAutoRefreshListView
import com.jaeger.library.StatusBarUtil
import com.storn.freechat.HomeActivity
import com.storn.freechat.R
import com.storn.freechat.adapter.MultiChatMessageAdapter
import com.storn.freechat.base.BaseActivity
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.service.MultiChatListener
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.MessageEntityVo
import com.storn.freechat.vo.MultiChatEntityVo
import com.yanzhenjie.recyclerview.swipe.overscroll.OnOverScrollListener
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.common_yellow_tool_bar_layout.*
import org.jivesoftware.smack.SmackConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smackx.muc.DiscussionHistory
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.ParticipantStatusListener

/**
 * 多人聊天室
 * Created by tianshutong on 2016/12/9.
 */

class MultiChatRoomAct : BaseActivity(), TextWatcher, View.OnClickListener,
        TopAutoRefreshListView.onTopRefreshListener, OnOverScrollListener,
        ViewTreeObserver.OnGlobalLayoutListener, ParticipantStatusListener {

    private var mOffset = 0
    private val mLimit = 10
    private var mRefresing = false
    private var mMessageVo = MessageEntityVo()
    private var chatMessageAdapter: MultiChatMessageAdapter? = null

    //聊天监听
    private var mMultiChat: MultiUserChat? = null

    companion object {
        var chatHandler: MultiChatHandler? = null
    }

    init {
        chatHandler = MultiChatHandler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_y_e2), 0)
        initToolbar()
        initData()
        initChat()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        chatHandler = null
    }

    private fun initToolbar() {
        mainToolBar.setNavigationIcon(R.mipmap.white_arrow_left)
    }

    private fun initData() {
        val data = this.intent
        mMessageVo = data.getSerializableExtra(Constants.MESSAGEVO) as MessageEntityVo
        setToolbarTitle(mMessageVo.roomName)
        getChatMessage()
//        clearMsgTip()
    }

    private fun initListener() {

        mainToolBar!!.setNavigationOnClickListener { _ -> finish() }
        mainToolBarRight!!.setOnClickListener { _ ->

        }
        chatRoomSend!!.setOnClickListener(this)
        chatRoomEdit!!.addTextChangedListener(this)

        refreshableView.bindActivity(this)
        refreshableView.setAutoRefreshEnabled(true)
        refreshableView.setOnTopRefreshListener(this)

        scrollLayout.setOnOverScrollListener(this)

        chatRoomRootLayout.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    private fun initChat() {
        val connection = XMPPConnectionManager.getInstance().connection
        if (!connection.isConnected || !connection.isAuthenticated) return
        mMultiChat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(mMessageVo.jid)
        val mMultiChatListener = MultiChatListener(this, mMessageVo.jid)
        mMultiChat!!.addMessageListener(mMultiChatListener)

        val history = DiscussionHistory()
        history.maxStanzas = 0
        mMultiChat!!.join(PreferenceTool.getString(Constants.LOGIN_UNAME),
                PreferenceTool.getString(Constants.LOGIN_UPASS), history,
                SmackConfiguration.getDefaultPacketReplyTimeout().toLong())
        mMultiChat!!.addParticipantStatusListener(this)
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
        queryChatMessage(mMessageVo.jid, myJid, mOffset, mLimit)
    }

    /**
     * 查询聊天信息

     * @param roomJid
     * *
     * @param myJid
     */
    private fun queryChatMessage(roomJid: String, myJid: String, offset: Int, limit: Int) {
        val oldMessageList = DBHelper.getInstance()
                .queryMultiChatMessageByJid(this, myJid, roomJid, offset, limit)
        setChatAdapter(oldMessageList)
    }

    private fun setChatAdapter(messageList: MutableList<MultiChatEntityVo>) {
        if (chatMessageAdapter == null) {
            chatMessageAdapter = MultiChatMessageAdapter(this, messageList)
            refreshableView!!.adapter = chatMessageAdapter
            refreshableView!!.setSelection(chatMessageAdapter!!.count)
        } else {
            refreshableView!!.postDelayed({
                mRefresing = false
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
        Thread {
            try {
                mMultiChat!!.sendMessage(msg)
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
        val multiChatMessageEntity = MultiChatEntityVo()
        multiChatMessageEntity.cId = currentTime.toInt()
        multiChatMessageEntity.myJid = mUserVo.jid
        multiChatMessageEntity.roomJid = mMessageVo.jid
        multiChatMessageEntity.roomName = mMessageVo.roomName
        multiChatMessageEntity.type = type
        multiChatMessageEntity.content = content
        multiChatMessageEntity.time = currentTime
        val dbHelper = DBHelper.getInstance()
        dbHelper.insertOrUpdateMultiChatMessage(this, multiChatMessageEntity)

        //save to message list
        mMessageVo.mId = currentTime.toInt()
        mMessageVo.jid = mMessageVo.jid
        mMessageVo.myJid = mUserVo.jid
        mMessageVo.fromName = mUserVo.name
        mMessageVo.roomName = mMessageVo.roomName
        mMessageVo.content = content
        mMessageVo.time = currentTime
        mMessageVo.msgCount = 0
        mMessageVo.type = 1
        dbHelper.insertOrUpdateMessage(this, mMessageVo)

        val addMessage = Message.obtain()
        addMessage.what = Constants.ADD_CHAT_MESSAGE
        addMessage.obj = multiChatMessageEntity
        chatHandler?.sendMessage(addMessage)

        refreshMsg()
    }

    override fun adminRevoked(participant: String?) {
    }

    override fun adminGranted(participant: String?) {
    }

    override fun moderatorGranted(participant: String?) {
    }

    override fun membershipRevoked(participant: String?) {
    }

    override fun membershipGranted(participant: String?) {
    }

    override fun moderatorRevoked(participant: String?) {
    }

    override fun banned(participant: String?, actor: String?, reason: String?) {
    }

    override fun voiceRevoked(participant: String?) {
    }

    override fun nicknameChanged(participant: String?, newNickname: String?) {
    }

    override fun ownershipRevoked(participant: String?) {
    }

    override fun joined(participant: String?) {
    }

    override fun voiceGranted(participant: String?) {
    }

    override fun ownershipGranted(participant: String?) {
    }

    override fun kicked(participant: String?, actor: String?, reason: String?) {
    }

    override fun left(participant: String?) {
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
        if (!mRefresing) {
            mRefresing = true
            mOffset += mLimit
            getChatMessage()
        }
    }

    override fun onTopOverScroll() {
        if (!mRefresing) {
            refreshableView.showHeader()
            onRefresh()
        }
    }

    override fun onBottomOverScroll() {
        if (!ShowKeyboard) {
            chatRoomEdit.requestFocus()
            SoftKeyBoardUtil.showSoftKeyboard(this)
            ShowKeyboard = true
        }
    }

    override fun onLeftOverScroll() {
    }

    override fun onRightOverScroll() {
    }

    var ShowKeyboard = false

    override fun onGlobalLayout() {
        val r = Rect()
        chatRoomRootLayout.getWindowVisibleDisplayFrame(r)
        // 获取状态栏高度
        val statusBarHeight = DensityUtil.getStatusBarHeight(chatRoomRootLayout)
        // 屏幕高度,不含虚拟按键的高度
        val screenHeight = DensityUtil.getScreenHeight(this)
        // 键盘最小高度
        val minKeyboardHeight = screenHeight / 3
        // 在不显示软键盘时，height 等于状态栏的高度
        val height = screenHeight - (r.bottom - r.top)

        if (ShowKeyboard) {
            // 如果软键盘是弹出的状态，并且 height 小于等于状态栏高度，
            // 说明这时软键盘已经收起
            if (height - statusBarHeight < minKeyboardHeight) {
                ShowKeyboard = false
            }
        } else {
            // 如果软键盘是收起的状态，并且 height 大于状态栏高度，
            // 说明这时软键盘已经弹出
            if (height - statusBarHeight > minKeyboardHeight) {
                ShowKeyboard = true
            }
        }
    }

    /**
     * 处理消息
     */
    inner class MultiChatHandler : Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                Constants.ADD_CHAT_MESSAGE -> {
                    val chatMessageEntity = msg.obj as MultiChatEntityVo
                    chatMessageAdapter!!.addChatData(chatMessageEntity)
                    refreshableView!!.setSelection(chatMessageAdapter!!.count)
                    chatRoomEdit!!.setText("")
                }
            }
        }
    }

}
