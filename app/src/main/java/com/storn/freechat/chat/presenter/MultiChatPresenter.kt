package com.storn.freechat.chat.presenter

import android.graphics.Rect
import android.os.Message
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
import com.jude.beam.expansion.BeamBasePresenter
import com.storn.freechat.R
import com.storn.freechat.chat.ui.MultiChatRoomAct
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.service.MultiChatListener
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.MessageEntityVo
import com.storn.freechat.vo.MultiChatEntityVo
import com.yanzhenjie.recyclerview.swipe.overscroll.OnOverScrollListener
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_chat_room_bottom_edit_layout.*
import kotlinx.android.synthetic.main.activity_chat_room_bottom_layout.*
import kotlinx.android.synthetic.main.common_yellow_tool_bar_layout.*
import org.jivesoftware.smack.SmackConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smackx.muc.DiscussionHistory
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.ParticipantStatusListener

/**
 * Created by tianshutong on 2017/7/7.
 */
class MultiChatPresenter : BeamBasePresenter<MultiChatRoomAct>(), ChatContract.IChatPresenter,
        TextWatcher, View.OnClickListener,
        TopAutoRefreshListView.onTopRefreshListener, OnOverScrollListener,
        ViewTreeObserver.OnGlobalLayoutListener, ParticipantStatusListener {

    var mOffset = 0
    val mLimit = 10
    var mRefreshing = false
    var ShowKeyboard = false

    private var mMessageVo = MessageEntityVo()
    //聊天监听
    private var mMultiChat: MultiUserChat? = null

    override fun onCreateView(view: MultiChatRoomAct) {
        super.onCreateView(view)
        view.initToolbar()
        view.changeChatBg()
        initData()
        initListener()
        initChat()
    }

    override fun initData() {
        val data = view.intent
        mMessageVo = data.getSerializableExtra(Constants.MESSAGEVO) as MessageEntityVo
        view.setToolbarTitle(mMessageVo.roomName)
        getChatMessage()
    }

    override fun initChat() {
        val connection = XMPPConnectionManager.getInstance().connection
        if (!connection.isConnected || !connection.isAuthenticated) return
        mMultiChat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(mMessageVo.jid)
        val mMultiChatListener = MultiChatListener(view, mMessageVo.jid)
        mMultiChat!!.addMessageListener(mMultiChatListener)

        val history = DiscussionHistory()
        history.maxStanzas = 0
        mMultiChat!!.join(PreferenceTool.getString(Constants.LOGIN_UNAME),
                PreferenceTool.getString(Constants.LOGIN_UPASS), history,
                SmackConfiguration.getDefaultPacketReplyTimeout().toLong())
        mMultiChat!!.addParticipantStatusListener(this)
    }

    override fun initListener() {
        view.mainToolBar!!.setNavigationOnClickListener { view.onBackPressed() }
        view.mainToolBarRight!!.setOnClickListener {

        }
        view.chatRoomSend!!.setOnClickListener(this)
        view.chatRoomEdit!!.addTextChangedListener(this)

        view.refreshableView.bindActivity(view)
        view.refreshableView.setAutoRefreshEnabled(true)
        view.refreshableView.setOnTopRefreshListener(this)

        view.scrollLayout.onOverScrollListener = this

        view.chatRoomRootLayout.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun clearMsgTip() {
        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message()
            homeMessage.what = Constants.CLEAR_MESSAGE_TIP
            homeMessage.obj = mMessageVo
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
    }

    fun refreshMsg() {
        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message()
            homeMessage.what = Constants.REFRESH_MESSAGE
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
    }

    override fun getChatMessage() {
        var myJid = PreferenceTool.getString(Constants.LOGIN_JID)
        if (TextUtils.isEmpty(myJid)) {
            myJid = ChatApplication.getUserVo().jid
        }
        if (TextUtils.isEmpty(myJid)) return
        queryChatMessage(mMessageVo.jid, myJid, mOffset, mLimit)
    }

    override fun queryChatMessage(jid: String, myJid: String, offset: Int, limit: Int) {
        val oldMessageList = DBHelper.getInstance()
                .queryMultiChatMessageByJid(view, myJid, jid, offset, limit)
        view.setChatAdapter(oldMessageList)
    }

    override fun sendMessage(position: Int, msg: String) {
        Thread {
            try {
                mMultiChat!!.sendMessage(msg)
                saveAndRefreshMessage(Constants.CHAT_MESSAGE_TYPE_TO, msg)
            } catch (e: SmackException.NotConnectedException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun saveAndRefreshMessage(type: Int, content: String) {
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
        dbHelper.insertOrUpdateMultiChatMessage(view, multiChatMessageEntity)

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
        dbHelper.insertOrUpdateMessage(view, mMessageVo)

        val addMessage = Message.obtain()
        addMessage.what = Constants.ADD_CHAT_MESSAGE
        addMessage.obj = multiChatMessageEntity
        MultiChatRoomAct.chatHandler?.sendMessage(addMessage)

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
        val content = view.chatRoomEdit!!.text.toString().trim { it <= ' ' }
        view.changePlus(content)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.chatRoomImgVoice -> {
            }
            R.id.chatRoomImgPlus -> {
            }
            R.id.chatRoomSend -> {
                val content = view.chatRoomEdit!!.text.toString().trim { it <= ' ' }
//                sendMessage(content)
            }
            else -> {
            }
        }
    }

    override fun onRefresh() {
        if (!mRefreshing) {
            mRefreshing = true
            mOffset += mLimit
            getChatMessage()
        }
    }

    override fun onTopOverScroll() {
        if (!mRefreshing) {
            view.refreshableView.showHeader()
            onRefresh()
        }
    }

    override fun onBottomOverScroll() {
        if (!ShowKeyboard) {
            view.chatRoomEdit.requestFocus()
            SoftKeyBoardUtil.showSoftKeyboard(view)
            ShowKeyboard = true
        }
    }

    override fun onLeftOverScroll() {
    }

    override fun onRightOverScroll() {
    }

    override fun onGlobalLayout() {
        val r = Rect()
        view.chatRoomRootLayout.getWindowVisibleDisplayFrame(r)
        // 获取状态栏高度
        val statusBarHeight = DensityUtil.getStatusBarHeight(view.chatRoomRootLayout)
        // 屏幕高度,不含虚拟按键的高度
        val screenHeight = DensityUtil.getScreenHeight(view)
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
}