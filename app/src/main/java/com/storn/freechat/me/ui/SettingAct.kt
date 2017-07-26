package com.storn.freechat.me.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.CommonUtil
import com.common.util.PreferenceTool
import com.common.widget.toast.LoadToast
import com.jaeger.library.StatusBarUtil
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.adapter.ChatBgAdapter
import com.storn.freechat.chat.ui.ChatRoomAct
import com.storn.freechat.chat.ui.MultiChatRoomAct
import com.storn.freechat.me.presenter.SettingContract
import com.storn.freechat.me.presenter.SettingPresenter
import kotlinx.android.synthetic.main.activity_setting_layout.*
import kotlinx.android.synthetic.main.setting_change_chat_background_layout.*
import kotlinx.android.synthetic.main.tool_bar_layout.*

/**
 * 设置
 * Created by tianshutong on 2017/7/13.
 */
@RequiresPresenter(SettingPresenter::class)
class SettingAct : BeamBaseActivity<SettingPresenter>(), SettingContract.View {

    var mGridHeight: Int = 0

    var mLoadToast: LoadToast? = null

    val mBgList = mutableListOf<Int>()

    var chatBgAdapter: ChatBgAdapter? = null

    var mHandler: Handler? = null

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == presenter.REQUEST_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                presenter.clearCache()
            }
        } else {
            CommonUtil.showToast(this, R.string.storage_no_permission)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_layout)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
        initToolbar()
        initBgList()
        initToast()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler = null
        mLoadToast = null
        chatBgAdapter = null
    }

    override fun initToolbar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.mipmap.white_arrow_left)
        mainToolBarTitle!!.text = "设置"

        mHandler = Handler(Looper.myLooper())
    }

    fun initListener() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        mChangeBgView.setOnClickListener { presenter.changeChatBg() }
        mChangePwdView.setOnClickListener { presenter.changePwd() }
        mClearCacheView.setOnClickListener { presenter.clearCache() }
        mLoginOutView.setOnClickListener { presenter.loginOut() }
        alphaView.setOnClickListener { presenter.changeChatBg() }
        chatBgGridView.onItemClickListener = ChatBgItemClick()
    }

    fun initBgList() {
        mBgList.add(R.mipmap.bg_chat_list_blue)
        mBgList.add(R.mipmap.bg_chat_list_pink)
        mBgList.add(R.mipmap.bg)
        mBgList.add(R.mipmap.bg_welcome)
        mBgList.add(R.mipmap.bg_shapeimageview)
        setChatBgAdapter()
    }

    override fun initToast() {
        mLoadToast = LoadToast(this)
        mLoadToast!!.setBackgroundColor(ContextCompat.getColor(this, R.color.dialog_bg))
        mLoadToast!!.setProgressColor(ContextCompat.getColor(this, R.color.black_19))
    }

    override fun showLoadToast(text: String) {
        mLoadToast!!.setText(text).setTranslationY(100).show()
    }

    override fun toastSuccess() {
        mLoadToast!!.success()
    }

    override fun toastError() {
        mLoadToast!!.error()
    }

    override fun showChatBg() {
        AnimationUtil.startAlphaAnim(alphaView, 0.7f)
        AnimationUtil.SlideDown(chatBgTopLayout, mGridHeight)
    }

    override fun hideChatBg() {
        AnimationUtil.endAlphaAnim(alphaView, 0.7f)
        AnimationUtil.SlideUp(chatBgTopLayout, mGridHeight)
    }

    /**
     * 设置聊天背景适配器
     */
    override fun setChatBgAdapter() {
        chatBgAdapter = ChatBgAdapter(this, mBgList)
        chatBgGridView.adapter = chatBgAdapter

        val line = (Math.ceil((mBgList.size.toDouble() / 3.toDouble()))).toInt()
        mGridHeight = line * resources.getDimension(R.dimen.grid_view_height).toInt()

        val lp = emptyView.layoutParams as LinearLayout.LayoutParams
        lp.height = mGridHeight
        emptyView.layoutParams = lp
    }

    /**
     * 聊天背景点击事件
     */
    inner class ChatBgItemClick : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            PreferenceTool.putInt(Constants.CHAT_BACKGROUND, position)
            PreferenceTool.commit()
            chatBgAdapter?.changeUi()
            if (ChatRoomAct.chatHandler != null) {
                val message = android.os.Message.obtain()
                message.what = Constants.CHANGE_CHAT_BG
                ChatRoomAct.chatHandler?.sendMessage(message)
            }
            if (MultiChatRoomAct.chatHandler != null) {
                val message = android.os.Message.obtain()
                message.what = Constants.CHANGE_CHAT_BG
                MultiChatRoomAct.chatHandler?.sendMessage(message)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}