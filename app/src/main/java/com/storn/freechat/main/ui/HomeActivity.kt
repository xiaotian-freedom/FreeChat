package com.storn.freechat.main.ui

import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.DensityUtil
import com.common.util.GlideHelper
import com.jaeger.library.StatusBarUtil
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.adapter.MainMessageAdapter
import com.storn.freechat.interfac.OnItemClickListener
import com.storn.freechat.main.presenter.MainContract
import com.storn.freechat.main.presenter.MainPresenter
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.service.MySingleChatListener
import com.storn.freechat.service.NetWorkStateReceiver
import com.storn.freechat.service.TaxiConnectionListener
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.MessageEntityVo
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import kotlinx.android.synthetic.main.activity_left_panel.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.tool_bar_layout.*
import org.jivesoftware.smack.chat.ChatManager

@RequiresPresenter(MainPresenter::class)
class HomeActivity : BeamBaseActivity<MainPresenter>(),
        MainContract.View {

    var messageAdapter: MainMessageAdapter? = null

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
        initMessageRecyclerView()
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
            presenter.exit2Click()
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
            XMPPConnectionManager.getInstance().removeConnection()
        }
        homeHandler = null
        messageAdapter = null
    }

    fun initToolbar() {
        homeHandler = HomeHandler()

        toolbar.title = ""
        setSupportActionBar(toolbar)
        mainToolBarRight.visibility = View.GONE
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawer, ContextCompat.getColor(this, R.color.colorPrimary))
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
        presenter.queryMessageList()
    }

    override fun showProgress() {
        progressView.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressView.visibility = View.GONE
    }

    override fun setToolbarTitle(text: String) {
        if (!TextUtils.isEmpty(text)) {
            mainToolBarTitle!!.text = text
        }
    }

    /**
     * 设置头像
     */
    override fun setBmpHeadView(bitmap: Bitmap) {
        headView!!.setImageBitmap(bitmap)
    }

    override fun setResHeadView(resId: Int) {
        headView!!.setImageResource(resId)
    }

    /**
     * 更新头像
     */
    override fun updateHeadView(url: String) {
        GlideHelper.showHeadViewWithNoAnim(this, url, headView)
    }

    /**
     * 设置用户名
     */
    override fun setNickName(text: String) {
        nickView!!.text = text
    }

    /**
     * 移除消息提示
     */
    private fun clearMsgTip(messageEntity: MessageEntityVo) {
        messageEntity.msgCount = 0
        DBHelper.getInstance().insertOrUpdateMessage(this, messageEntity)
    }

    /**
     * 设置消息适配器
     */
    fun setMessageAdapter(list: MutableList<MessageEntityVo>) {
        if (messageAdapter == null) {
            messageAdapter = MainMessageAdapter(this, list, true)
            messageAdapter?.setOnItemClickListener(mOnMessageItemClickListener)
            mainMessageRecyclerView.adapter = messageAdapter
        } else {
            messageAdapter?.setRefreshData(list, false)
        }
        messageRefreshLayout.finishRefreshing()
    }

    override fun closeDrawer() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        mHandler.postDelayed({ drawer.closeDrawer(GravityCompat.START) }, Constants.DELAY_500.toLong())
    }

    override fun slideToMessage() {
        val cursorTop = slideMenuLeftLine.top
        val messageTop = slideMenuLeftRbMessage.top
        AnimationUtil.SlideDown(slideMenuLeftLine, messageTop - cursorTop)
    }

    override fun slideToGroup() {
        val cursorTop = slideMenuLeftLine.top
        val groupTop = slideMenuLeftRbGroup.top
        AnimationUtil.SlideDown(slideMenuLeftLine, groupTop - cursorTop)
    }

    override fun slideToFriends() {
        val cursorTop = slideMenuLeftLine.top
        val contactTop = slideMenuLeftRbContact.top
        AnimationUtil.SlideDown(slideMenuLeftLine, contactTop - cursorTop)
    }

    override fun slideToService() {
        val cursorTop = slideMenuLeftLine.top
        val serviceTop = slideMenuLeftRbService.top
        AnimationUtil.SlideDown(slideMenuLeftLine, serviceTop - cursorTop)
    }

    override fun slideToSetting() {
        val cursorTop = slideMenuLeftLine.top
        val settingsTop = slideMenuLeftRbSettings.top
        AnimationUtil.SlideDown(slideMenuLeftLine, settingsTop - cursorTop)
    }

    /**
     * 消息列表点击事件
     */
    private val mOnMessageItemClickListener = OnItemClickListener { position: Int -> presenter.goToChat(position) }

    /**
     * 消息处理
     */
    inner class HomeHandler : Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                Constants.REFRESH_MESSAGE -> presenter.queryMessageList()
                Constants.CLEAR_MESSAGE_TIP -> {
                    val messageEntity = msg.obj as MessageEntityVo
                    clearMsgTip(messageEntity)
                    presenter.queryMessageList()
                }
                Constants.CONNECT_SERVICE -> {
                    mHandler.postDelayed({ presenter.initXmmConn() }, Constants.DELAY_1000.toLong())
                }
                Constants.UPDATE_HEADVIEW -> {
                    val url = msg.obj as String
                    updateHeadView(url)
                }
                Constants.UPDATE_NICKNAME -> {
                    val nick = msg.obj as String
                    setNickName(nick)
                }
                else -> {
                }
            }
        }
    }
}
