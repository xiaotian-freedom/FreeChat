package com.storn.freechat

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.transition.Slide
import android.util.Log
import android.view.*
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.DensityUtil
import com.common.util.SoftKeyBoardUtil
import com.common.util.ToastUtil
import com.common.widget.ConfirmDialog
import com.common.widget.PinnedHeaderExpandableListView
import com.gitonway.lee.niftynotification.lib.Effects
import com.storn.freechat.adapter.MainFriendsAdapter
import com.storn.freechat.adapter.MainGroupAdapter
import com.storn.freechat.adapter.MainMessageAdapter
import com.storn.freechat.base.BaseActivity
import com.storn.freechat.chat.ChatRoomAct
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.common.DBHelper
import com.storn.freechat.login.presenter.LoginContract
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.util.ActivityManagerUtil
import com.storn.freechat.vo.FriendsEntityVo
import com.storn.freechat.vo.FriendsGroupVo
import com.storn.freechat.vo.GroupEntityVo
import com.storn.freechat.vo.MessageEntityVo
import com.yalantis.phoenix.PullToRefreshView
import com.yanzhenjie.recyclerview.swipe.Closeable
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_drawer_layout.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.tool_bar_layout.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smackx.muc.MultiUserChatManager
import java.util.*

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
        TextWatcher, LoginContract.ILoginListener, ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener, PinnedHeaderExpandableListView.OnHeaderUpdateListener,
        OnSwipeMenuItemClickListener, PullToRefreshView.OnRefreshListener {

    val MESSAGE: Int = 0
    val CHATROOM: Int = 1
    val FRIENDS: Int = 2
    val SERVICE: Int = 3
    val SETTINGS: Int = 4

    var currentSelect: Int = MESSAGE
    var customView: View? = null
    var dialog: ConfirmDialog? = null
    var createEditText: AppCompatEditText? = null

    var messageAdapter: MainMessageAdapter? = null
    var friendsAdapter: MainFriendsAdapter? = null
    var groupAdapter: MainGroupAdapter? = null

    //消息列表
    var mMessageList: MutableList<MessageEntityVo>? = null
    //群列表
    var mGroupList: MutableList<GroupEntityVo>? = null
    //好友列表
    var groupList: ArrayList<FriendsGroupVo>? = null
    var childList: ArrayList<List<FriendsEntityVo>>? = null

    var isExit: Boolean = false
    var isFriendsRefreshing: Boolean = false
    var isFirstShowFriendList: Boolean = true
    var isFirstShowChatRoomList: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupWindowTransition()
        initXmmConn()
        initToolbar()
        initListener()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit2Click()
        }
        return false
    }

    private fun setupWindowTransition() {
        val slideIn = Slide(Gravity.END)
        slideIn.duration = 500
        val slideOut = Slide(Gravity.START)
        slideOut.duration = 500
        window.enterTransition = slideIn
        window.exitTransition = slideOut
    }

    fun initToolbar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        setToolbarTitle("小田一郎君")

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initListener() {
        fab.setOnClickListener { _ ->
            when (currentSelect) {
                CHATROOM -> showChatRoomDialog()
                FRIENDS -> Toast.makeText(this, "添加好友功能暂未上线，敬请期待~~~", Toast.LENGTH_SHORT).show()
                else -> {
                }
            }
        }
        navigationView.setNavigationItemSelectedListener(this)
        mainExpandableListView.setOnChildClickListener(this)
        mainExpandableListView.setOnGroupClickListener(this)
        mainExpandableListView.setOnHeaderUpdateListener(this)
        mainMessageRecyclerView.setSwipeMenuItemClickListener(this)
        swipeMessageRefreshLayout.setOnRefreshListener(this)
        swipeExpandableRefreshLayout.setOnRefreshListener(mExpandableListener)
    }

    private fun initXmmConn() {
        if (!XMPPConnectionManager.getInstance().isConnected) {
            XMPPConnectionManager.getInstance().login(this)
        } else {
            initMessageRecyclerView()
        }
    }

    /**
     * 初始化消息列表
     */
    private fun initMessageRecyclerView() {
        mainMessageRecyclerView.setHasFixedSize(true)
        mainMessageRecyclerView.setSwipeMenuCreator(
                { _, swipeRightMenu, _ ->
                    val width = resources.getDimensionPixelSize(R.dimen.item_menu_size)
                    val height = ViewGroup.LayoutParams.MATCH_PARENT
                    val deleteItem = SwipeMenuItem(this)
                            .setBackgroundDrawable(R.drawable.item_menu_select_red)
                            .setImage(R.mipmap.ic_action_delete)
                            .setWidth(width)
                            .setHeight(height)
                            .setTop(DensityUtil.dip2px(this, 11f))
                            .setRight(DensityUtil.dip2px(this, 11f))
                            .setBottom(DensityUtil.dip2px(this, 11f))
                    swipeRightMenu.addMenuItem(deleteItem)// 添加一个按钮到右侧侧菜单。
                })
        mainMessageRecyclerView.itemAnimator = DefaultItemAnimator()
        mainMessageRecyclerView.layoutManager = LinearLayoutManager(this)
        queryMessageList()
    }

    /**
     * 查询聊天消息记录
     */
    private fun queryMessageList() {
        mMessageList = DBHelper.getInstance().queryMessageByJid(this,
                ChatApplication.getUserVo().jid)
        setMessageAdapter()
    }

    /**
     * 设置消息适配器
     */
    private fun setMessageAdapter() {
        if (messageAdapter == null) {
            messageAdapter = MainMessageAdapter(this, mMessageList)
            messageAdapter?.setOnItemClickListener(mOnMessageItemClickListener)
            mainMessageRecyclerView!!.adapter = messageAdapter
        } else {
            messageAdapter?.setRefreshData(mMessageList)
        }
    }

    /**
     * 设置群适配器
     */
    private fun setGroupAdapter(groupList: MutableList<GroupEntityVo>?) {
        if (groupAdapter == null) {
            groupAdapter = MainGroupAdapter(this, groupList)
            groupAdapter?.setOnItemClickListener(mOnGroupItemClickListener)
            mainMessageRecyclerView.adapter = groupAdapter
        } else {
            groupAdapter?.setRefreshData(groupList)
        }
    }

    /**
     * 设置好友适配器
     */
    private fun setFriendsAdapter() {
        if (friendsAdapter == null) {
            friendsAdapter = MainFriendsAdapter(this, groupList, childList)
            mainExpandableListView.setAdapter(friendsAdapter)
        } else {
            friendsAdapter?.refreshData(groupList, childList)
        }
    }

    /**
     * 设置导航栏标题

     * @param name
     */
    private fun setToolbarTitle(name: String) {
        if (!TextUtils.isEmpty(name)) {
            mainToolBarTitle!!.text = name
        }
    }

    /**
     * 设置导航栏右侧图标

     * @param resId
     */
    private fun setToolbarRight(resId: Int) {
        if (resId != 0) {
            mainToolBarRight!!.setImageResource(resId)
        }
    }

    /**
     * 退出应用
     */
    private fun exit2Click() {
        val mTimer: Timer
        if (isExit) {
            val manager = XMPPConnectionManager.getInstance()
            if (manager.isConnected) {
                manager.disconnect()
            }
            ActivityManagerUtil.exitApp()
        } else {
            isExit = true
            Toast.makeText(this, resources.getString(R.string.click_one_more_exit), Toast.LENGTH_SHORT).show()
            mTimer = Timer()
            mTimer.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false
                }
            }, Constants.DELAY_1000.toLong())
        }
    }

    /**
     * 显示message recyclerview的刷新控件
     */
    private fun showMessageRecyclerRefreshLayout() {
        if (swipeMessageRefreshLayout.visibility == View.GONE) {
            mainExpandableListView.visibility = View.GONE
            swipeMessageRefreshLayout.setRefreshStyle(PullToRefreshView.STYLE_SUN)
            AnimationUtil.startAlphaAnim(swipeMessageRefreshLayout, swipeExpandableRefreshLayout)
        }
    }

    /**
     * 显示expandable的刷新控件
     */
    private fun showExpandableRefreshLayout() {
        if (mainExpandableListView.visibility == View.GONE) {
            mainExpandableListView.visibility = View.VISIBLE
            swipeExpandableRefreshLayout.setRefreshStyle(PullToRefreshView.STYLE_SUN)
            AnimationUtil.startAlphaAnim(swipeExpandableRefreshLayout, swipeMessageRefreshLayout)
        }
    }

    /**
     * 显示创建聊天室对话框
     */
    private fun showChatRoomDialog() {
        AnimationUtil.rotationAnim(fab)
        if (customView == null) {
            customView = LayoutInflater.from(this).inflate(R.layout.create_chat_room_layout, rootView, false)
            customView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    resources.getDimension(R.dimen.space_i_10).toInt())
            createEditText = customView!!.findViewById(R.id.create_chat_room_edit) as AppCompatEditText
            createEditText!!.addTextChangedListener(this)
        }
        val builder = ConfirmDialog.Builder(this)
        builder.setTitle(R.string.create_room)
        builder.setCustomView(customView)
        builder.setContentPanelHeight(resources.getDimension(R.dimen.content_panel_height).toInt())
        builder.setAutoDismiss(true)
        builder.setPositiveButton(getString(R.string.confirm)) { _: DialogInterface, _: Int ->
            val chatRoom = createEditText!!.text.toString().trim({ it <= ' ' })
            if (TextUtils.isEmpty(chatRoom)) {
                Toast.makeText(this, getString(R.string.create_room_empty), Toast.LENGTH_SHORT).show()
            } else {
                if (chatRoom.length < 2) {
                    Toast.makeText(this, getString(R.string.create_room_tip_min), Toast.LENGTH_SHORT).show()
                } else {
                    val isCreated = createChatRooms(chatRoom)
                    if (isCreated) {
                        Toast.makeText(this, getString(R.string.create_room_success), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, getString(R.string.create_room_fail), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.setNegativeButton(getString(R.string.cancel)
        ) { _: DialogInterface, _: Int ->
            SoftKeyBoardUtil.hideSoftKeyboard(this@HomeActivity)
        }
        if (dialog == null) {
            dialog = builder.create()
        }
        dialog?.setCanceledOnTouchOutside(true)
        if (!dialog!!.isShowing) {
            dialog?.show()
        }
        dialog?.setOnDismissListener { AnimationUtil.reverseRotation(fab) }
    }

    /**
     * 创建聊天室
     */
    private fun createChatRooms(roomName: String): Boolean {
        val connection = XMPPConnectionManager.getInstance().connection
        val mMultiUserChatManager = MultiUserChatManager.getInstanceFor(connection)
        val mUserVo = ChatApplication.getUserVo()
        if (TextUtils.isEmpty(mUserVo.jid)) {
            return false
        }
        try {
            val SERVICE_NAME = mMultiUserChatManager.serviceNames
            val userChat = mMultiUserChatManager.getMultiUserChat(roomName + "@" + SERVICE_NAME[0])
            userChat.create(mUserVo.name)
            return XMPPConnectionManager.getInstance().configChatRoom(userChat)
        } catch (e: XMPPException.XMPPErrorException) {
            e.printStackTrace()
        } catch (e: SmackException) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 获取好友列表
     */
    private fun getFriendsData() {
        groupList?.clear()
        childList?.clear()
        val connection = XMPPConnectionManager.getInstance().connection
        val roster = Roster.getInstanceFor(connection)
        if (!roster.isLoaded) {
            try {
                roster.reloadAndWait()
            } catch (e: SmackException.NotLoggedInException) {
                e.printStackTrace()
            } catch (e: SmackException.NotConnectedException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        roster.addRosterListener(object : RosterListener {
            override fun entriesAdded(addresses: Collection<String>) {

            }

            override fun entriesUpdated(addresses: Collection<String>) {

            }

            override fun entriesDeleted(addresses: Collection<String>) {

            }

            override fun presenceChanged(presence: Presence) {

            }
        })

        val rosterEntries = roster.groups

        for (rosterGroup in rosterEntries) {
            val groupName = rosterGroup.name
            val count = rosterGroup.entryCount
            val friendsGroupVo = FriendsGroupVo()
            friendsGroupVo.name = groupName
            friendsGroupVo.count = count
            groupList?.add(friendsGroupVo)

            val rosterEntryList = rosterGroup.entries
            val tempChildList = java.util.ArrayList<FriendsEntityVo>()
            for (i in rosterEntryList.indices) {
                val rosterEntry = rosterEntryList[i]
                val friendsEntityVo = FriendsEntityVo()
                val presence = roster.getPresence(rosterEntry.user)
                Log.e("状态", "presence=" + presence)
                Log.e("状态", "presence status=" + presence.status)
                Log.e("状态", "presence mode=" + presence.mode)

                if (presence.isAvailable) {
                    friendsEntityVo.presence = "[在线]"
                } else {
                    friendsEntityVo.presence = "[离线]"
                }
                friendsEntityVo.name = rosterEntry.name
                friendsEntityVo.jid = rosterEntry.user
                tempChildList.add(friendsEntityVo)
            }
            childList?.add(tempChildList)
        }
        setFriendsAdapter()

        val chatManager = ChatManager.getInstanceFor(connection)
        chatManager.addChatListener(chatManagerListener)
        swipeExpandableRefreshLayout.postDelayed({
            if (isFriendsRefreshing) {
                isFriendsRefreshing = false
                swipeExpandableRefreshLayout.setRefreshing(false)
                swipeExpandableRefreshLayout.postDelayed({
                    ToastUtil.showToast(this, "暂无新朋友", R.id.content_root_layout, Effects.standard)
                }, Constants.DELAY_500.toLong())
            }
        }, Constants.DELAY_1000.toLong())
    }

    /**
     * 获取已加入的聊天室
     */
    private fun getChatRooms() {
        val connection = XMPPConnectionManager.getInstance().connection
        val mMultiUserChatManager = MultiUserChatManager.getInstanceFor(connection)
        val mUserVo = ChatApplication.getUserVo()
        try {
            if (TextUtils.isEmpty(mUserVo.jid)) {
                return
            }
            val tempRoomList = mMultiUserChatManager.getJoinedRooms(mUserVo.jid)
            for (roomId in tempRoomList) {
                val roomInfo = mMultiUserChatManager.getRoomInfo(roomId)
                val groupVo = GroupEntityVo()
                groupVo.roomJid = roomInfo.room
                groupVo.roomName = roomInfo.name
                mGroupList?.plus(groupVo)
            }
            setGroupAdapter(mGroupList)
        } catch (e: SmackException.NoResponseException) {
            e.printStackTrace()
        } catch (e: XMPPException.XMPPErrorException) {
            e.printStackTrace()
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        }

    }

    /**
     * 左侧item点击事件
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_message -> {
                fab.visibility = View.GONE
                showMessageRecyclerRefreshLayout()
            }
            R.id.nav_group -> {
                currentSelect = CHATROOM
                fab.visibility = View.VISIBLE
                if (isFirstShowChatRoomList) {
                    getChatRooms()
                    isFirstShowChatRoomList = false
                }
                showMessageRecyclerRefreshLayout()
            }
            R.id.nav_contact -> {
                currentSelect = FRIENDS
                fab.visibility = View.VISIBLE
                if (isFirstShowFriendList) {
                    getFriendsData()
                    isFirstShowFriendList = false
                }
                showExpandableRefreshLayout()
            }
            R.id.nav_service -> {
                fab.visibility = View.GONE
            }
            R.id.nav_settings -> {
                fab.visibility = View.GONE
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val chatRoom = createEditText!!.text.toString().trim()
        if (!TextUtils.isEmpty(chatRoom) && chatRoom.length >= 10) {
            Toast.makeText(this@HomeActivity, getString(R.string.create_room_tip_max), Toast.LENGTH_SHORT).show()
        }
    }

    override fun start() {
        progressView.visibility = View.VISIBLE
        setToolbarTitle("连接中...")
    }

    override fun success() {
        runOnUiThread {
            progressView.visibility = View.GONE
            setToolbarTitle(ChatApplication.getUserVo().name)
            initMessageRecyclerView()
        }
    }

    override fun fail() {
        runOnUiThread { progressView.visibility = View.GONE }
    }

    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        val mainChild = childList?.get(groupPosition)?.get(childPosition)
        val intent = Intent()
        intent.setClass(this, ChatRoomAct::class.java)
        intent.putExtra(Constants.FRIEND_JID, mainChild?.jid)
        intent.putExtra(Constants.FRIEND_NAME, mainChild?.name)
        startActivity(intent)
        return false
    }

    override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean {
        return false
    }

    override fun getPinnedHeader(): View {
        val view = this.layoutInflater
                .inflate(R.layout.main_expandable_group, mainExpandableListView, false)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return view
    }

    override fun updatePinnedHeader(headerView: View?, firstVisibleGroupPos: Int) {
        if (friendsAdapter == null || friendsAdapter?.groupCount == 0) {
            return
        }
        val mainGroup = friendsAdapter?.getGroup(firstVisibleGroupPos) as FriendsGroupVo
        val tvGroupName = headerView?.findViewById(R.id.main_group_name) as TextView
        val tvCount = headerView.findViewById(R.id.main_group_count) as TextView
        tvGroupName.text = mainGroup.name
        tvCount.text = mainGroup.count.toString()
    }

    override fun onItemClick(closeable: Closeable?, adapterPosition: Int, menuPosition: Int, direction: Int) {
        closeable?.smoothCloseRightMenu()
        mMessageList?.removeAt(adapterPosition)
        messageAdapter?.notifyItemRemoved(adapterPosition)
    }

    /**
     * 消息列表点击监听
     */
    private val mOnMessageItemClickListener = { position: Int ->
        val messageEntity = mMessageList?.get(position)
        val intent = Intent()
        intent.setClass(this, ChatRoomAct::class.java)
        intent.putExtra(Constants.FRIEND_JID, messageEntity?.fromJid)
        intent.putExtra(Constants.FRIEND_NAME, messageEntity?.name)
        startActivity(intent)
    }

    private val mOnGroupItemClickListener = { position: Int ->
        Toast.makeText(this, "这是点击的第" + position + "个群", Toast.LENGTH_SHORT).show()
    }

    /**
     * 注册消息监听器
     */
    private val chatManagerListener = { chat: Chat, _: Boolean ->
        chat.addMessageListener { _: Chat, message: org.jivesoftware.smack.packet.Message ->
            Log.e("消息", message.body)
        }
    }

    /**
     * 消息列表刷新
     */
    override fun onRefresh() {
        swipeMessageRefreshLayout.postDelayed({
            swipeMessageRefreshLayout.setRefreshing(false)
            val mUserVo = ChatApplication.getUserVo()
            val messageEntityList = DBHelper.getInstance().queryMessageByJid(this, mUserVo.jid)
            if (messageEntityList.size == mMessageList?.size) {
                swipeMessageRefreshLayout.postDelayed({
                    ToastUtil.showToast(this, "暂无新消息", R.id.content_root_layout, Effects.standard)
                }, Constants.DELAY_500.toLong())
            } else {
                messageAdapter?.setRefreshData(messageEntityList)
            }
        }, Constants.DELAY_1000.toLong())
    }

    /**
     * 好友列表刷新
     */
    private val mExpandableListener = {
        isFriendsRefreshing = true
        getFriendsData()
    }
}
