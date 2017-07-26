package com.storn.freechat.main.presenter

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.widget.RadioGroup
import com.common.common.Constants
import com.common.util.CommonUtil
import com.common.util.PreferenceTool
import com.jude.beam.expansion.BeamBasePresenter
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.header.GoogleDotView
import com.storn.freechat.R
import com.storn.freechat.chat.ui.ChatRoomAct
import com.storn.freechat.chat.ui.MultiChatRoomAct
import com.storn.freechat.contact.ui.FriendsAct
import com.storn.freechat.group.GroupAct
import com.storn.freechat.login.presenter.LoginContract
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.manager.UserManager
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.me.ui.ProfileDetailAct
import com.storn.freechat.me.ui.SettingAct
import com.storn.freechat.util.ActivityManagerUtil
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.MessageEntityVo
import com.yanzhenjie.recyclerview.swipe.Closeable
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener
import kotlinx.android.synthetic.main.activity_left_panel.*
import kotlinx.android.synthetic.main.content_home.*
import org.jivesoftware.smack.chat.ChatManager
import java.util.*

/**
 * 首页控制器
 * Created by tianshutong on 2017/6/30.
 */
class MainPresenter : BeamBasePresenter<HomeActivity>(), LoginContract.ILoginListener,
        MainContract.Presenter, RadioGroup.OnCheckedChangeListener, OnSwipeMenuItemClickListener {

    val MESSAGE: Int = 0
    val GROUP: Int = 1
    val FRIENDS: Int = 2
    val SERVICE: Int = 3
    val SETTINGS: Int = 4
    var currentSelect: Int = MESSAGE

    override fun onCreateView(view: HomeActivity) {
        super.onCreateView(view)
        initXmmConn()
        initListener()
    }

    fun initXmmConn() {
        if (!XMPPConnectionManager.getInstance().isConnected) {
            XMPPConnectionManager.getInstance().login(this)
        } else {
            success()
        }
    }

    override fun initListener() {
        view.headView!!.setOnClickListener { goProfileAct() }
        view.slideMenuGroup.setOnCheckedChangeListener(this)
        view.mainMessageRecyclerView.setSwipeMenuItemClickListener(this)
        view.messageRefreshLayout.setHeaderView(GoogleDotView(view))
        view.messageRefreshLayout.setOnRefreshListener(mMessageListListener)


        view.slideMenuLeftRbGroup.setOnClickListener {
            if (currentSelect == GROUP) {
                goToGroup()
            } else {
                view.mHandler.postDelayed({
                    goToGroup()
                }, Constants.DELAY_300.toLong())
            }
        }

        view.slideMenuLeftRbContact.setOnClickListener {
            if (currentSelect == FRIENDS) {
                goToFriends()
            } else {
                view.mHandler.postDelayed({
                    goToFriends()
                }, Constants.DELAY_300.toLong())
            }
        }

        view.slideMenuLeftRbSettings.setOnClickListener {
            if (currentSelect == SETTINGS) {
                goToSetting()
            } else {
                view.mHandler.postDelayed({
                    goToSetting()
                }, Constants.DELAY_300.toLong())
            }
        }
    }

    override fun start() {
        view.runOnUiThread {
            view.showProgress()
            view.setToolbarTitle("连接中...")
        }
    }

    override fun success() {
        view.runOnUiThread { view.hideProgress() }

        val name: String
        val userVo = DBHelper.getInstance().queryProfileInfo(view, PreferenceTool.getString(Constants.LOGIN_JID))
        if (userVo != null) {
            if (userVo.nickName.isNotEmpty()) {
                name = userVo.nickName
            } else {
                name = userVo.name
            }
            view.runOnUiThread {
                view.setToolbarTitle(name)
                view.setNickName(name)
            }

            if (userVo.img != null && userVo.img.isNotEmpty()) {
                view.runOnUiThread { view.updateHeadView(userVo.img) }
            } else {
                val inputStream = UserManager.getInstance().getUserHead(userVo.jid)
                if (inputStream != null) {
                    view.runOnUiThread { view.setBmpHeadView(BitmapFactory.decodeStream(inputStream)) }
                } else {
                    view.runOnUiThread { view.setResHeadView(R.mipmap.default_head_2) }
                }
            }
        }
        val chatManager = ChatManager.getInstanceFor(XMPPConnectionManager.getInstance().connection)
        chatManager.addChatListener(view.mChatListener)
    }

    override fun fail(error: String) {
        view.runOnUiThread {
            view.hideProgress()
            view.setToolbarTitle("网络异常")
        }
    }

    /**
     * 查询聊天消息记录
     */
    override fun queryMessageList() {
        var messageList = mutableListOf<MessageEntityVo>()
        val jid = PreferenceTool.getString(Constants.LOGIN_JID)
        if (!TextUtils.isEmpty(jid)) {
            messageList = DBHelper.getInstance().queryMessageByJid(view, jid)
        }
        view.setMessageAdapter(messageList)
    }

    override fun goProfileAct() {
        val intent = Intent(view, ProfileDetailAct::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(view, view.headView, view.resources.getString(R.string.profile_head))
        view.startActivity(intent, options.toBundle())
    }

    override fun goToChat(position: Int) {
        if (view.messageAdapter!!.mList.isNotEmpty()) {
            val messageEntity = view.messageAdapter!!.mList[position]
            val intent = Intent()
            if (messageEntity.type == 0) {
                intent.setClass(view, ChatRoomAct::class.java)
            } else if (messageEntity.type == 1) {
                intent.setClass(view, MultiChatRoomAct::class.java)
            }
            intent.putExtra(Constants.MESSAGEVO, messageEntity)
            startActivity(intent)
        }
    }

    override fun deleteMessage(position: Int) {
        val mId = view.messageAdapter!!.mList[position].mId
        view.messageAdapter!!.deleteData(view.messageAdapter!!.mList, position)
        DBHelper.getInstance().deleteMessage(view, mId)
    }

    /**
     * 跳转到群列表
     */
    override fun goToGroup() {
        view.startActivity(Intent(view, GroupAct::class.java))
    }

    /**
     * 跳转到好友列表
     */
    override fun goToFriends() {
        view.startActivity(Intent(view, FriendsAct::class.java))
    }

    /**
     * 跳转到设置界面
     */
    override fun goToSetting() {
        view.startActivity(Intent(view, SettingAct::class.java))
    }

    var isExit: Boolean = false


    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.slideMenuLeftRbMessage -> {
                view.slideToMessage()
                view.mHandler.postDelayed({
                    currentSelect = MESSAGE
                }, Constants.DELAY_300.toLong())
                view.closeDrawer()
            }
            R.id.slideMenuLeftRbGroup -> {
                view.slideToGroup()
                view.mHandler.postDelayed({
                    currentSelect = GROUP
                }, Constants.DELAY_300.toLong())
            }
            R.id.slideMenuLeftRbContact -> {
                view.slideToFriends()
                view.mHandler.postDelayed({
                    currentSelect = FRIENDS
                }, Constants.DELAY_300.toLong())
            }
            R.id.slideMenuLeftRbService -> {
                view.slideToService()
                view.mHandler.postDelayed({
                    currentSelect = SERVICE
                }, Constants.DELAY_300.toLong())
                view.closeDrawer()
            }
            R.id.slideMenuLeftRbSettings -> {
                view.slideToSetting()
                view.mHandler.postDelayed({
                    currentSelect = SETTINGS
                }, Constants.DELAY_300.toLong())
            }
        }
    }

    override fun onItemClick(closeable: Closeable?, adapterPosition: Int, menuPosition: Int, direction: Int) {
        closeable?.smoothCloseRightMenu()
        deleteMessage(adapterPosition)
    }

    private val mMessageListListener = object : RefreshListenerAdapter() {
        override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
            view.mHandler.postDelayed({ queryMessageList() }, Constants.DELAY_1000.toLong())
        }
    }

    /**
     * 退出应用
     */
    override fun exit2Click() {
        val mTimer: Timer
        if (isExit) {
            val manager = XMPPConnectionManager.getInstance()
            if (manager.isConnected) {
                manager.disconnect()
            }
            ActivityManagerUtil.exitApp()
        } else {
            isExit = true
            CommonUtil.showToast(view, view.getString(R.string.click_one_more_exit))
            mTimer = Timer()
            mTimer.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false
                }
            }, Constants.DELAY_1000.toLong())
        }
    }
}