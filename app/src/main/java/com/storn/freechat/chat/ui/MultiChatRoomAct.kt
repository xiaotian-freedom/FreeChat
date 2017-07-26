package com.storn.freechat.chat.ui

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.common.common.Constants
import com.common.util.PreferenceTool
import com.jaeger.library.StatusBarUtil
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.chat.adapter.MultiChatMessageAdapter
import com.storn.freechat.chat.presenter.ChatContract
import com.storn.freechat.chat.presenter.MultiChatPresenter
import com.storn.freechat.vo.MultiChatEntityVo
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_chat_room_bottom_edit_layout.*
import kotlinx.android.synthetic.main.activity_chat_room_bottom_layout.*
import kotlinx.android.synthetic.main.common_yellow_tool_bar_layout.*

/**
 * 多人聊天室
 * Created by tianshutong on 2016/12/9.
 */
@RequiresPresenter(MultiChatPresenter::class)
class MultiChatRoomAct : BeamBaseActivity<MultiChatPresenter>(), ChatContract.ChatView {

    private var chatMessageAdapter: MultiChatMessageAdapter? = null

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
    }

    override fun onDestroy() {
        super.onDestroy()
        chatHandler = null
    }

    override fun initToolbar() {
        mainToolBar.setNavigationIcon(R.mipmap.white_arrow_left)
    }

    /**
     * 设置标题名

     * @param name
     */
    override fun setToolbarTitle(name: String) {
        if (!TextUtils.isEmpty(name)) {
            mainToolBarTitle!!.text = name
        }
    }

    override fun changeChatType(type: Int) {
        when (type) {
            Constants.CHAT_EDIT_TYPE -> {
                bottomEditLayout.visibility = View.VISIBLE
                bottomVoiceLayout.visibility = View.GONE
            }
            Constants.CHAT_RECORD_TYPE -> {
                bottomEditLayout.visibility = View.GONE
                bottomVoiceLayout.visibility = View.VISIBLE
            }
            else -> {
            }
        }
    }

    /**
     * 更换背景
     */
    override fun changeChatBg() {
        when (PreferenceTool.getInt(Constants.CHAT_BACKGROUND, 0)) {
            0 -> scrollLayout.setBackgroundResource(R.mipmap.bg_chat_list_blue)
            1 -> scrollLayout.setBackgroundResource(R.mipmap.bg_chat_list_pink)
            2 -> scrollLayout.setBackgroundResource(R.mipmap.bg)
            3 -> scrollLayout.setBackgroundResource(R.mipmap.bg_welcome)
            4 -> scrollLayout.setBackgroundResource(R.mipmap.bg_shapeimageview)
            else -> {
                scrollLayout.setBackgroundResource(R.mipmap.bg_chat_list_blue)
            }
        }
    }

    fun setChatAdapter(messageList: MutableList<MultiChatEntityVo>) {
        if (chatMessageAdapter == null) {
            chatMessageAdapter = MultiChatMessageAdapter(this, messageList)
            refreshableView!!.adapter = chatMessageAdapter
            refreshableView!!.setSelection(chatMessageAdapter!!.count)
        } else {
            refreshableView!!.postDelayed({
                presenter.mRefreshing = false
                refreshableView!!.onRefreshComplete()
                if (messageList.size < presenter.mLimit) {
                    refreshableView!!.onLoadFinish()
                }
                chatMessageAdapter!!.addChatList(messageList)
                refreshableView!!.setSelection(messageList.size + 1)
            }, Constants.DELAY_1000.toLong())
        }
    }

    /**
     * 显示或隐藏加号
     */
    override fun changePlus(content: String) {
        if (TextUtils.isEmpty(content)) {
            chatRoomSend!!.visibility = View.GONE
            chatRoomImgPlus!!.visibility = View.VISIBLE
        } else {
            chatRoomSend!!.visibility = View.VISIBLE
            chatRoomImgPlus!!.visibility = View.GONE
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
                Constants.CHANGE_CHAT_BG -> {
                    changeChatBg()
                }
            }
        }
    }

}
