package com.storn.freechat.chat.presenter

import android.Manifest
import android.graphics.Rect
import android.support.v4.app.ActivityCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.common.common.Constants
import com.common.util.*
import com.common.widget.RecordButton
import com.common.widget.TopAutoRefreshListView
import com.jude.beam.expansion.BeamBasePresenter
import com.storn.freechat.R
import com.storn.freechat.chat.ui.ChatRoomAct
import com.storn.freechat.chat.utils.SaveMsgUtil
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.ChatMessageEntityVo
import com.storn.freechat.vo.FriendsEntityVo
import com.storn.freechat.vo.MessageEntityVo
import com.yanzhenjie.recyclerview.swipe.overscroll.OnOverScrollListener
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_chat_room_bottom_edit_layout.*
import kotlinx.android.synthetic.main.activity_chat_room_bottom_layout.*
import kotlinx.android.synthetic.main.activity_chat_room_bottom_voice_layout.*
import kotlinx.android.synthetic.main.common_yellow_tool_bar_layout.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smackx.filetransfer.FileTransfer
import org.jivesoftware.smackx.filetransfer.FileTransferListener
import org.jivesoftware.smackx.filetransfer.FileTransferManager
import org.jivesoftware.smackx.filetransfer.FileTransferRequest
import java.io.File
import java.io.IOException

/**
 * Created by tianshutong on 2017/7/7.
 */
class ChatPresenter : BeamBasePresenter<ChatRoomAct>(), ChatContract.IChatPresenter,
        TopAutoRefreshListView.onTopRefreshListener, TextWatcher, View.OnClickListener,
        OnOverScrollListener, ViewTreeObserver.OnGlobalLayoutListener, RecordButton.RecordEventListener,
        FileTransferListener {

    var mOffset = 0
    val mLimit = 20
    var friendInfo = FriendsEntityVo()
    var mMessageVo = MessageEntityVo()

    var ShowKeyboard = false

    //聊天监听
    var mChatManager: ChatManager? = null

    override fun onCreateView(view: ChatRoomAct) {
        super.onCreateView(view)
        if (!PermissionUtil.isWriteStorage(view)) {
            ActivityCompat.requestPermissions(view, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), RecordButton.WRITE_FILE)
        }
        if (!PermissionUtil.isReadStorage(view)) {
            ActivityCompat.requestPermissions(view, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RecordButton.READ_FILE)
        }
        initData()
        initChat()
        initListener()
        getChatMessage()
        clearMsgTip()
    }

    override fun initData() {
        val data = view.intent
        mMessageVo = data.getSerializableExtra(Constants.MESSAGEVO) as MessageEntityVo
        friendInfo.jid = mMessageVo.jid
        if (mMessageVo.fromName.contains("@")) {
            friendInfo.name = mMessageVo.fromName.split("@")[0]
        } else {
            friendInfo.name = mMessageVo.fromName
        }
        view.setToolbarTitle(mMessageVo.fromName)
        view.changeChatBg()
        view.changeChatType(PreferenceTool.getInt(Constants.CHAT_TYPE, 0))
    }

    override fun initListener() {
        view.refreshableView.bindActivity(view)
        view.refreshableView.setAutoRefreshEnabled(true)
        view.refreshableView.setOnTopRefreshListener(this)
        view.scrollLayout.onOverScrollListener = this
        view.mainToolBar.setNavigationOnClickListener { view.onBackPressed() }

        view.chatRoomSend.setOnClickListener(this)
        view.chatRoomImgVoice.setOnClickListener(this)
        view.chatRoomImgKeyboard.setOnClickListener(this)
        view.chatRoomEdit.addTextChangedListener(this)
        view.chatRoomRecord.setSavePath(FileUtil.getRecordTmpPath())
        view.chatRoomRecord.setRecordEventListener(this)
        view.chatRoomRootLayout.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun initChat() {
        val connection = XMPPConnectionManager.getInstance().connection
        mChatManager = ChatManager.getInstanceFor(connection)

        val transfer = FileTransferManager.getInstanceFor(connection)
        transfer.addFileTransferListener(this)
    }

    override fun clearMsgTip() {
        if (HomeActivity.homeHandler != null) {
            val homeMessage = android.os.Message()
            homeMessage.what = Constants.CLEAR_MESSAGE_TIP
            homeMessage.obj = mMessageVo
            HomeActivity.homeHandler?.sendMessage(homeMessage)
        }
    }

    override fun getChatMessage() {
        val myJid = PreferenceTool.getString(Constants.LOGIN_JID)
        if (TextUtils.isEmpty(myJid)) return
//        if (myJid.contains("Smack")) {
//            myJid = myJid.split("/")[0]
//        }
        queryChatMessage(friendInfo.jid, myJid, mOffset, mLimit)
    }

    override fun queryChatMessage(jid: String, myJid: String, offset: Int, limit: Int) {
        val oldMessageList = DBHelper.getInstance()
                .queryChatMessageByJid(view, myJid, jid, offset, limit)
        view.setChatAdapter(oldMessageList)
    }

    override fun sendMessage(position: Int, msg: String) {
        Thread {
            //    if (!friendInfo.jid.contains("Smack")) {
//                friendInfo.jid = friendInfo.jid + "/Smack"
//            }
            val newChat = mChatManager!!.createChat(friendInfo.jid)
            try {
                newChat.sendMessage(msg)
                ChatRoomAct.chatHandler?.postDelayed({ updateSendSuccess(position) }, 0)
            } catch (e: SmackException.NotConnectedException) {
                e.printStackTrace()
                ChatRoomAct.chatHandler?.postDelayed({ updateSendError(position) }, Constants.DELAY_500.toLong())
            }
        }.start()
    }

    fun updateReSend(position: Int, content: String) {
        updateItem(Constants.CHAT_SEND_ING, position)
        sendMessage(position, content)
    }

    fun updateSendSuccess(position: Int) {
        updateItem(Constants.CHAT_SEND_SUCCESS, position)
    }

    fun updateSendError(position: Int) {
        updateItem(Constants.CHAT_SEND_FAIL, position)
    }

    fun updateItem(status: Int, position: Int) {
        val chatMsgVo = view.chatMessageAdapter?.getItem(position) as ChatMessageEntityVo
        chatMsgVo.status = status
        view.chatMessageAdapter?.mChatList?.set(position, chatMsgVo)
        view.chatMessageAdapter?.updateItem(view.chatMessageAdapter!!.mChatList[position].cId)
        DBHelper.getInstance().insertOrUpdateChatMessage(view, chatMsgVo)
        notifyHome(status, position, chatMsgVo.fromJid)
    }

    fun notifyHome(status: Int, position: Int, fromJid: String) {
        if (position == view.chatMessageAdapter!!.mChatList.size - 1) {
            val messageVo = DBHelper.getInstance().querySingleMessageByJid(view, fromJid)
            messageVo.status = status
            DBHelper.getInstance().insertOrUpdateMessage(view, messageVo)

            if (HomeActivity.homeHandler != null) {
                val homeMessage = android.os.Message.obtain()
                homeMessage.what = Constants.REFRESH_MESSAGE
                HomeActivity.homeHandler?.sendMessage(homeMessage)
            }
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
    }

    override fun afterTextChanged(editable: Editable) {
        val content = view.chatRoomEdit!!.text.toString().trim { it <= ' ' }
        view.changePlus(content)
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

    /**
     * 接收文件
     */
    override fun fileTransferRequest(request: FileTransferRequest?) {
        val inTransfer = request?.accept()
        try {
            val fileName = request?.fileName
            val fileLength = request?.fileSize
            val fromUser = request?.requestor!!.split("/")[0]
            val description = request.description
            Log.i("接收文件名--->>", fileName)
            Log.i("接收文件大小--->>", fileLength.toString())
            Log.i("接收文件描述--->>", description)
            Log.i("接收文件来自--->>", fromUser)

            val file = File(FileUtil.getChatFileDir(), fileName)

            Thread {
                inTransfer?.recieveFile(file)

                //开始接收文件
                while (!inTransfer!!.isDone) {

                }
                if (inTransfer.status == FileTransfer.Status.error) {
                    Log.i("接收录音文件-->>", "失败")
                } else if (inTransfer.status == FileTransfer.Status.refused) {
                    Log.i("接收录音文件-->>", "被拒绝")
                } else if (inTransfer.status == FileTransfer.Status.cancelled) {
                    Log.i("接收录音文件-->>", "取消")
                } else if (inTransfer.status == FileTransfer.Status.complete) {
                    Log.i("接收录音文件-->>", "完成")
                    if (ChatApplication.userVo != null) {

                        if (fromUser.split("@")[0] == ChatApplication.userVo.name) {//发送文件
                        } else {//接收文件
                            if (fileName!!.endsWith("amr")) {

                                val audioTime: Long
                                if (description == "Sending file") {//来自spark
                                    audioTime = 5000//暂时的
                                } else {
                                    audioTime = description.split("/")[1].toLong()
                                }
                                SaveMsgUtil(view, friendInfo.jid, ChatApplication.userVo.jid, friendInfo.name,
                                        mMessageVo, Constants.CHAT_MESSAGE_TYPE_FROM, Constants.CHAT_MESSAGE_AUDIO,
                                        Constants.CHAT_SEND_SUCCESS, 0, "", file.absolutePath,
                                        audioTime, "").save()
                            } else {
                                when (description.split("/")[0]) {
                                    "图片" -> {
                                        SaveMsgUtil(view, friendInfo.jid, ChatApplication.userVo.jid, friendInfo.name,
                                                mMessageVo, Constants.CHAT_MESSAGE_TYPE_FROM, Constants.CHAT_MESSAGE_PIC,
                                                Constants.CHAT_SEND_SUCCESS, 0, "", "",
                                                0, file.absolutePath).save()
                                    }
                                    "文件" -> {
                                    }
                                }
                            }
                        }
                    }
                }

            }.start()
        } catch (e: SmackException) {
            Log.e("接收文件错误--->>", e.message)
        } catch (e: IOException) {
            Log.e("接收文件时发生IO错误--->>", e.message)
        }
    }

    /**
     * 发送语音
     */
    override fun onFinishedRecord(audioPath: String?, secs: Long) {
        SaveMsgUtil(view, friendInfo.jid, PreferenceTool.getString(Constants.LOGIN_JID), friendInfo.name,
                mMessageVo, Constants.CHAT_MESSAGE_TYPE_TO, Constants.CHAT_MESSAGE_AUDIO, Constants.CHAT_SEND_ING,
                0, "", audioPath, secs, "").save()

        val myJid = PreferenceTool.getString(Constants.LOGIN_JID)
        if (TextUtils.isEmpty(myJid)) return
        val fileManager = FileTransferManager.getInstanceFor(XMPPConnectionManager.getInstance().connection)
        val outGoingTransfer = fileManager.createOutgoingFileTransfer(myJid)
        outGoingTransfer.sendFile(File(audioPath), "语音" + "/" + secs.toString())

        Thread {
            //开始发送语音
            while (!outGoingTransfer.isDone) {
                Log.i("正在发送文件-->", outGoingTransfer.progress.toString())
            }
            if (outGoingTransfer.status == FileTransfer.Status.error) {
                Log.i("传输录音文件-->>", "失败")
                ChatRoomAct.chatHandler?.postDelayed({ updateSendError(view.chatMessageAdapter!!.count - 1) }, Constants.DELAY_500.toLong())
            } else if (outGoingTransfer.status == FileTransfer.Status.refused) {
                Log.i("传输录音文件-->>", "被拒绝")
                ChatRoomAct.chatHandler?.postDelayed({ updateSendError(view.chatMessageAdapter!!.count - 1) }, Constants.DELAY_500.toLong())
            } else if (outGoingTransfer.status == FileTransfer.Status.cancelled) {
                Log.i("传输录音文件-->>", "取消")
                ChatRoomAct.chatHandler?.postDelayed({ updateSendError(view.chatMessageAdapter!!.count - 1) }, Constants.DELAY_500.toLong())
            } else if (outGoingTransfer.status == FileTransfer.Status.complete) {

                ChatRoomAct.chatHandler?.postDelayed({
                    updateSendSuccess(view.chatMessageAdapter!!.count - 1)
                }, 0)
            }

        }.start()
    }

    override fun onStartRecord() {
    }

    override fun checkPermission() {
        if (!PermissionUtil.isReadRecord(view)) {
            ActivityCompat.requestPermissions(view, arrayOf(Manifest.permission.RECORD_AUDIO), RecordButton.RECORD_AUDIO)
        }
    }

    override fun onTopOverScroll() {
    }

    override fun onBottomOverScroll() {
        if (!ShowKeyboard) {
            if (view.bottomEditLayout.visibility == View.VISIBLE) {
                view.chatRoomEdit.requestFocus()
                SoftKeyBoardUtil.showSoftKeyboard(view)
                ShowKeyboard = true
            }
        }
    }

    override fun onLeftOverScroll() {
    }

    override fun onRightOverScroll() {
    }

    override fun onRefresh() {
        mOffset += mLimit
        getChatMessage()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.chatRoomImgVoice -> {
                AnimationUtil.startAlphaAnim(Constants.ANIM_300, view.bottomVoiceLayout, view.bottomEditLayout)
                v.postDelayed({ SoftKeyBoardUtil.hideSoftKeyboard(view) }, Constants.ANIM_300.toLong())
                PreferenceTool.putInt(Constants.CHAT_TYPE, Constants.CHAT_RECORD_TYPE)
                PreferenceTool.commit()
            }
            R.id.chatRoomImgKeyboard -> {
                AnimationUtil.startAlphaAnim(Constants.ANIM_300, view.bottomEditLayout, view.bottomVoiceLayout)
                view.chatRoomEdit.requestFocus()
                SoftKeyBoardUtil.showSoftKeyboard(view)
                PreferenceTool.putInt(Constants.CHAT_TYPE, Constants.CHAT_EDIT_TYPE)
                PreferenceTool.commit()
            }
            R.id.chatRoomImgPlus -> {

            }
            R.id.chatRoomSend -> {
                val content = view.chatRoomEdit!!.text.toString().trim { it <= ' ' }
                SaveMsgUtil(view, friendInfo.jid, PreferenceTool.getString(Constants.LOGIN_JID), friendInfo.name,
                        mMessageVo, Constants.CHAT_MESSAGE_TYPE_TO, Constants.CHAT_MESSAGE_TXT, Constants.CHAT_SEND_ING,
                        0, content, "", 0, "").save()
            }
            else -> {
            }
        }
    }
}