package com.storn.freechat

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import com.common.common.Constants
import com.common.util.*
import com.common.widget.CircleImageView
import com.common.widget.ConfirmDialog
import com.common.widget.PinnedHeaderExpandableListView
import com.jaeger.library.StatusBarUtil
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.header.GoogleDotView
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.storn.freechat.adapter.MainFriendsAdapter
import com.storn.freechat.adapter.MainGroupAdapter
import com.storn.freechat.adapter.MainMessageAdapter
import com.storn.freechat.base.BaseActivity
import com.storn.freechat.chat.ChatRoomAct
import com.storn.freechat.chat.MultiChatRoomAct
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.interfac.OnItemClickListener
import com.storn.freechat.jni.FreeChatCommon
import com.storn.freechat.login.presenter.LoginContract
import com.storn.freechat.manager.UserManager
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.me.ui.ProfileDetailAct
import com.storn.freechat.service.MySingleChatListener
import com.storn.freechat.service.NetWorkStateReceiver
import com.storn.freechat.service.TaxiConnectionListener
import com.storn.freechat.util.ActivityManagerUtil
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.FriendsEntityVo
import com.storn.freechat.vo.FriendsGroupVo
import com.storn.freechat.vo.GroupEntityVo
import com.storn.freechat.vo.MessageEntityVo
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
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.RoomInfo
import java.util.*

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
        TextWatcher, LoginContract.ILoginListener, ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener, PinnedHeaderExpandableListView.OnHeaderUpdateListener,
        OnSwipeMenuItemClickListener {

    val MESSAGE: Int = 0
    val CHATROOM: Int = 1
    val FRIENDS: Int = 2
    val SERVICE: Int = 3
    val SETTINGS: Int = 4

    var currentSelect: Int = MESSAGE
    var customView: View? = null
    var headView: CircleImageView? = null
    var dialog: ConfirmDialog? = null
    var createEditText: AppCompatEditText? = null

    var messageAdapter: MainMessageAdapter? = null
    var friendsAdapter: MainFriendsAdapter? = null
    var groupAdapter: MainGroupAdapter? = null

    //消息列表
    var mMessageList = mutableListOf<MessageEntityVo>()
    //群列表
    var mGroupList = mutableListOf<GroupEntityVo>()
    //好友列表
    var groupList = mutableListOf<FriendsGroupVo>()
    var childList = arrayListOf<List<FriendsEntityVo>>()

    var isExit: Boolean = false
    var isFirstShowFriendList: Boolean = true
    var isFirstShowChatRoomList: Boolean = true

    var mHandler = Handler(Looper.getMainLooper())

    val netWorkStateReceiver = NetWorkStateReceiver()

    val mChatListener = MySingleChatListener(this)

    val connectionListener = TaxiConnectionListener(this)

    /**
     * kotlin没有static
     * 需使用companion object代替
     * 用于外部调用内部成员变量
     */
    companion object {
        var homeHandler: HomeHandler? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initToolbar()
        initXmmConn()
        initMessageRecyclerView()
        initGroupRecyclerView()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(netWorkStateReceiver, filter)
        XMPPConnectionManager.getInstance().connection.addConnectionListener(connectionListener)
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(netWorkStateReceiver)
        if (XMPPConnectionManager.getInstance().connection != null) {
            XMPPConnectionManager.getInstance().connection.removeConnectionListener(connectionListener)
            ChatManager.getInstanceFor(XMPPConnectionManager.getInstance().connection).removeChatListener(mChatListener)
            XMPPConnectionManager.getInstance().disconnect()
        }
    }

    fun initToolbar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        mainToolBarRight.visibility = View.GONE
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawer, ContextCompat.getColor(this, R.color.color_y_e2))

        homeHandler = HomeHandler()
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
        headView = navigationView.getHeaderView(0).findViewById(R.id.nav_head_view) as CircleImageView
        headView!!.setOnClickListener({
            val intent = Intent(this, ProfileDetailAct::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this, headView, resources.getString(R.string.profile_head))
            startActivity(intent, options.toBundle())
        })
        navigationView.setNavigationItemSelectedListener(this)
        mainExpandableListView.setOnChildClickListener(this)
        mainExpandableListView.setOnGroupClickListener(this, true)
        mainMessageRecyclerView.setSwipeMenuItemClickListener(this)

        messageRefreshLayout.setHeaderView(GoogleDotView(this))
        messageRefreshLayout.setOnRefreshListener(mMessageListListener)
        groupRefreshLayout.setHeaderView(BezierLayout(this))
        groupRefreshLayout.setOnRefreshListener(mGroupListListener)
        expandableRefreshLayout.setHeaderView(ProgressLayout(this))
        expandableRefreshLayout.setOnRefreshListener(mExpandableListener)
    }

    private fun initXmmConn() {
        if (!XMPPConnectionManager.getInstance().isConnected) {
            XMPPConnectionManager.getInstance().login(this)
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
     * 初始化群列表
     */
    private fun initGroupRecyclerView() {
        mainGroupRecyclerView.setHasFixedSize(true)
        mainGroupRecyclerView.itemAnimator = DefaultItemAnimator()
        mainGroupRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * 查询聊天消息记录
     */
    private fun queryMessageList() {
        val jid = PreferenceTool.getString(Constants.LOGIN_JID)
        if (TextUtils.isEmpty(jid)) return
        mMessageList = DBHelper.getInstance().queryMessageByJid(this, jid)
        setMessageAdapter()
    }

    /**
     * 设置消息适配器
     */
    private fun setMessageAdapter() {
        if (messageAdapter == null) {
            messageAdapter = MainMessageAdapter(this, mMessageList, true)
            messageAdapter?.setOnItemClickListener(mOnMessageItemClickListener)
            mainMessageRecyclerView.adapter = messageAdapter
        } else {
            messageAdapter?.setRefreshData(mMessageList, false)
        }
        messageRefreshLayout.finishRefreshing()
    }

    /**
     * 设置群适配器
     */
    private fun setGroupAdapter() {
        if (groupAdapter == null) {
            groupAdapter = MainGroupAdapter(this, mGroupList, true)
            groupAdapter?.setOnItemClickListener(mOnGroupItemClickListener)
            mainGroupRecyclerView.adapter = groupAdapter
        } else {
            groupAdapter?.setRefreshData(mGroupList, false)
        }
    }

    /**
     * 设置好友适配器
     */
    private fun setFriendsAdapter() {
        if (friendsAdapter == null) {
            friendsAdapter = MainFriendsAdapter(this, groupList, childList, true, true)
            mainExpandableListView.setAdapter(friendsAdapter)
            mainExpandableListView.setOnHeaderUpdateListener(this)
//            mainExpandableListView.requestRefreshHeader()
        } else {
            friendsAdapter?.refreshData(groupList, childList, false, false)
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
     * 设置头像
     */
    private fun setHeadView(bitmap: Bitmap) {
        headView!!.setImageBitmap(bitmap)
    }

    /**
     * 更新头像
     */
    private fun updateHeadView(url: String) {
        GlideHelper.showHeadViewWithNoAnim(this, url, headView)
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
     * 显示message 的刷新控件
     */
    private fun showMessageRecyclerRefreshLayout() {
        if (messageRefreshLayout.visibility == View.GONE) {
            AnimationUtil.startAlphaAnim(messageRefreshLayout, groupRefreshLayout, expandableRefreshLayout)
        }
    }

    /**
     * 显示群聊刷新控件
     */
    private fun showGroupRecyclerRefreshLayout() {
        if (groupRefreshLayout.visibility == View.GONE) {
            AnimationUtil.startAlphaAnim(groupRefreshLayout, messageRefreshLayout, expandableRefreshLayout)
        }
    }

    /**
     * 显示expandable的刷新控件
     */
    private fun showExpandableRefreshLayout() {
        if (expandableRefreshLayout.visibility == View.GONE) {
            AnimationUtil.startAlphaAnim(expandableRefreshLayout, messageRefreshLayout, groupRefreshLayout)
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
                    createChatRooms(chatRoom)
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
    private fun createChatRooms(roomName: String): Unit {
        mProgressView.visibility = View.VISIBLE
        val connection = XMPPConnectionManager.getInstance().connection
        val mMultiUserChatManager = MultiUserChatManager.getInstanceFor(connection)
        val mUserVo = ChatApplication.getUserVo()
        var created: Boolean

        mHandler.postDelayed({
            try {
                val SERVICE_NAME = mMultiUserChatManager.serviceNames
                val userChat = mMultiUserChatManager.getMultiUserChat(roomName + "@" + SERVICE_NAME[0])
                userChat.create(mUserVo.name)
//                joinChatRoom(mUserVo, mMultiUserChatManager)
                created = XMPPConnectionManager.getInstance().configChatRoom(userChat)

            } catch (e: XMPPException.XMPPErrorException) {
                e.printStackTrace()
                created = false
            } catch (e: SmackException) {
                e.printStackTrace()
                created = false
            }

            mProgressView.visibility = View.GONE
            if (created) {
                getChatRooms()
                Toast.makeText(this, getString(R.string.create_room_success), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.create_room_fail), Toast.LENGTH_SHORT).show()
            }
        }, Constants.DELAY_3000.toLong())
    }

    /*private fun joinChatRoom(userVo: UserVo, mucChatManager: MultiUserChatManager) {
        val muc = mucChatManager.getMultiUserChat(userVo.jid)
        val history: DiscussionHistory = DiscussionHistory()
        history.maxStanzas = 0
        muc.join(userVo.name, userVo.password, history, SmackConfiguration.getDefaultPacketReplyTimeout().toLong())
    }*/

    /**
     * 查询本地好友列表
     */
    private fun queryLocalFriends() {
        val userVo = ChatApplication.userVo
        if (userVo == null || userVo.jid.isEmpty()) return
        groupList = DBHelper.getInstance().queryFriendsGroupList(this, userVo.jid)
        val tempList = DBHelper.getInstance().queryFriendsList(this, userVo.jid)
        if (tempList.isNotEmpty()) childList.add(tempList)
        if (groupList.isNotEmpty()) setFriendsAdapter()
    }

    /**
     * 获取好友列表
     */
    private fun getFriendsData() {
        val connection = XMPPConnectionManager.getInstance().connection
        if (!connection.isConnected || !connection.isAuthenticated) return
        val roster = Roster.getInstanceFor(connection)
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
        if (rosterEntries.isNotEmpty()) {
            groupList.clear()
            childList.clear()
        }

        for (rosterGroup in rosterEntries) {
            val groupName = rosterGroup.name
            val count = rosterGroup.entryCount
            val friendsGroupVo = FriendsGroupVo()
            friendsGroupVo.name = groupName
            friendsGroupVo.count = count
            friendsGroupVo.myJid = ChatApplication.getUserVo().jid
            groupList.add(friendsGroupVo)
            DBHelper.getInstance().insertOrUpdateFriendsGroup(this, friendsGroupVo)

            val rosterEntryList = rosterGroup.entries
            val tempChildList = java.util.ArrayList<FriendsEntityVo>()
            for (i in rosterEntryList.indices) {
                val rosterEntry = rosterEntryList[i]
                val friendsEntityVo = FriendsEntityVo()
                val presence = roster.getPresence(rosterEntry.user)

                val type = presence.type
                if (type == Presence.Type.available) {
                    friendsEntityVo.presence = "[在线]"
                } else {
                    friendsEntityVo.presence = "[离线]"
                }
                friendsEntityVo.jid = rosterEntry.user + Constants.JID_POST
                friendsEntityVo.name = rosterEntry.user
                friendsEntityVo.nickName = rosterEntry.name
                friendsEntityVo.myJid = ChatApplication.getUserVo().jid
                tempChildList.add(friendsEntityVo)
                DBHelper.getInstance().insertOrUpdateFriends(this, friendsEntityVo)
            }
            childList.add(tempChildList)
        }
        if (groupList.isNotEmpty()) {
            setFriendsAdapter()
        }
        expandableRefreshLayout.finishRefreshing()
    }

    /**
     * 获取本地聊天室
     */
    private fun queryLocalChatRooms() {
        mGroupList.clear()
        mGroupList = DBHelper.getInstance().queryGroupList(this)
        if (mGroupList.isNotEmpty()) setGroupAdapter()
    }

    /**
     * 获取服务器所有的聊天室
     */
    private fun getChatRooms() {
        val connection = XMPPConnectionManager.getInstance().connection
        if (!XMPPConnectionManager.getInstance().isLogin) return
        mGroupList.clear()
        val mMultiUserChatManager = MultiUserChatManager.getInstanceFor(connection)
        try {
            val tempRoomList = mMultiUserChatManager.getHostedRooms("conference." + FreeChatCommon.getXMPPServerName())
            if (tempRoomList.isNotEmpty()) {
                for (hostRoom in tempRoomList) {

                    val roomInfo: RoomInfo = mMultiUserChatManager.getRoomInfo(hostRoom.jid)

                    val groupVo = GroupEntityVo()
                    groupVo.roomJid = hostRoom.jid
                    groupVo.roomName = hostRoom.name
                    groupVo.description = roomInfo.description
                    groupVo.subject = roomInfo.subject
                    groupVo.occupantsCount = roomInfo.occupantsCount
                    mGroupList.add(groupVo)
                    DBHelper.getInstance().insertOrUpdateGroup(this, groupVo)
                }
            }
            if (mGroupList.isNotEmpty()) {
                setGroupAdapter()
            }
            groupRefreshLayout.finishRefreshing()

        } catch (e: SmackException.NoResponseException) {
            e.printStackTrace()
        } catch (e: XMPPException.XMPPErrorException) {
            e.printStackTrace()
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        }
    }

    /**
     * 移除消息提示
     */
    private fun clearMsgTip(messageEntity: MessageEntityVo) {
        messageEntity.msgCount = 0
        DBHelper.getInstance().insertOrUpdateMessage(this, messageEntity)
    }

    /**************************************************************************************/
    /********************************* listener *******************************************/
    /**************************************************************************************/

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
                    queryLocalChatRooms()
                    mHandler.postDelayed({
                        getChatRooms()
                    }, Constants.DELAY_1000.toLong())
                    isFirstShowChatRoomList = false
                }
                showGroupRecyclerRefreshLayout()
            }
            R.id.nav_contact -> {
                currentSelect = FRIENDS
                fab.visibility = View.VISIBLE
                if (isFirstShowFriendList) {
                    queryLocalFriends()
                    mHandler.postDelayed({
                        getFriendsData()
                    }, Constants.DELAY_1000.toLong())
                    isFirstShowFriendList = false
                }
                showExpandableRefreshLayout()
            }
            R.id.nav_service -> {
                currentSelect = SERVICE
                fab.visibility = View.GONE
            }
            R.id.nav_settings -> {
                currentSelect = SETTINGS
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
        runOnUiThread({
            progressView.visibility = View.VISIBLE
            setToolbarTitle("连接中...")
        })
    }

    override fun success() {
        runOnUiThread {
            progressView.visibility = View.GONE
            val name: String
            val userVo = DBHelper.getInstance().queryProfileInfo(this, PreferenceTool.getString(Constants.LOGIN_JID))
            if (userVo != null) {
                if (userVo.nickName.isNotEmpty()) {
                    name = userVo.nickName
                } else {
                    name = userVo.name
                }
                setToolbarTitle(name)
                if (userVo.img != null && userVo.img.isNotEmpty()) {
                    updateHeadView(userVo.img)
                } else {
                    val inputStream = UserManager.getInstance().getUserHead(PreferenceTool.getString(Constants.LOGIN_JID))
                    if (inputStream != null) {
                        setHeadView(BitmapFactory.decodeStream(inputStream))
                    } else {
                        headView!!.setImageResource(R.mipmap.default_head_2)
                    }
                }
            }

            val chatManager = ChatManager.getInstanceFor(XMPPConnectionManager.getInstance().connection)
            chatManager.addChatListener(mChatListener)
        }
    }

    override fun fail() {
        runOnUiThread {
            progressView.visibility = View.GONE;setToolbarTitle("网络异常")
        }
    }

    /**
     * 好友列表点击
     */
    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        val mainChild = childList[groupPosition][childPosition]
        val intent = Intent(this, ChatRoomAct::class.java)
        val messageEntity = MessageEntityVo()
        messageEntity.jid = mainChild.jid
        messageEntity.fromName = mainChild.name
        intent.putExtra(Constants.MESSAGEVO, messageEntity)
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
        if (friendsAdapter == null) return
        if (groupList.isEmpty()) return
        val mainGroup = groupList[firstVisibleGroupPos]
        val tvGroupName = headerView?.findViewById(R.id.main_group_name) as TextView
        val tvCount = headerView.findViewById(R.id.main_group_count) as TextView
        tvGroupName.text = mainGroup.name
        tvCount.text = mainGroup.count.toString()
    }

    /**
     * 删除消息列表
     */
    override fun onItemClick(closeable: Closeable?, adapterPosition: Int, menuPosition: Int, direction: Int) {
        closeable?.smoothCloseRightMenu()
        val mId = mMessageList[adapterPosition].mId
        mMessageList.removeAt(adapterPosition)
        messageAdapter!!.deleteData(mMessageList, adapterPosition)
        DBHelper.getInstance().deleteMessage(this, mId)
    }

    /**
     * 消息列表点击事件
     */
    private val mOnMessageItemClickListener = OnItemClickListener { position: Int ->
        if (mMessageList.isNotEmpty()) {
            val messageEntity = mMessageList[position]
            val intent = Intent()

            if (messageEntity.type == 0) {
                intent.setClass(this, ChatRoomAct::class.java)
            } else if (messageEntity.type == 1) {
                intent.setClass(this, MultiChatRoomAct::class.java)
            }
            intent.putExtra(Constants.MESSAGEVO, messageEntity)
            startActivity(intent)
        }
    }

    /**
     * 群列表点击事件
     */
    private val mOnGroupItemClickListener = OnItemClickListener { position: Int ->
        if (mGroupList.isNotEmpty()) {
            val groupEntity = mGroupList[position]
            if (groupEntity.roomJid == "my_server@conference.freechat.storn.com") {
                Toast.makeText(this, "拒绝加入", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MultiChatRoomAct::class.java)
                val messageEntity = MessageEntityVo()
                messageEntity.jid = groupEntity.roomJid
                messageEntity.roomName = groupEntity.roomName
                intent.putExtra(Constants.MESSAGEVO, messageEntity)
                startActivity(intent)
            }
        }
    }


    /**
     * 消息列表刷新
     */
    private val mMessageListListener = object : RefreshListenerAdapter() {
        override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
            messageRefreshLayout.postDelayed({ queryMessageList() }, Constants.DELAY_1000.toLong())
        }
    }


    /**
     * 群列表刷新
     */
    private val mGroupListListener = object : RefreshListenerAdapter() {
        override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
            groupRefreshLayout.postDelayed({ getChatRooms() }, Constants.DELAY_2000.toLong())
        }
    }


    /**
     * 好友列表刷新
     */
    private val mExpandableListener = object : RefreshListenerAdapter() {
        override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
            expandableRefreshLayout.postDelayed({ getFriendsData() }, Constants.DELAY_1000.toLong())
        }
    }

    /**
     * 消息处理
     */
    inner class HomeHandler : Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                Constants.REFRESH_MESSAGE -> queryMessageList()
                Constants.CLEAR_MESSAGE_TIP -> {
                    val messageEntity = msg.obj as MessageEntityVo
                    clearMsgTip(messageEntity)
                    queryMessageList()
                }
                Constants.CONNECT_SERVICE -> {
                    mHandler.postDelayed({ initXmmConn() }, Constants.DELAY_1000.toLong())
                }
                Constants.UPDATE_HEADVIEW -> {
                    val url = msg.obj as String
                    updateHeadView(url)
                }
                else -> {
                }
            }
        }
    }

}
