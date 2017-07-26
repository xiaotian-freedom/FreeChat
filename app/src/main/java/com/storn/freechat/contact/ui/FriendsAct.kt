package com.storn.freechat.contact.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.common.widget.PinnedHeaderExpandableListView
import com.jaeger.library.StatusBarUtil
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.adapter.MainFriendsAdapter
import com.storn.freechat.contact.presenter.FriendsContract
import com.storn.freechat.contact.presenter.FriendsPresenter
import com.storn.freechat.vo.FriendsEntityVo
import com.storn.freechat.vo.FriendsGroupVo
import kotlinx.android.synthetic.main.activity_friends_layout.*
import kotlinx.android.synthetic.main.tool_bar_layout.*
import java.util.*

/**
 * 好友
 * Created by tianshutong on 2017/7/14.
 */
@RequiresPresenter(FriendsPresenter::class)
class FriendsAct : BeamBaseActivity<FriendsPresenter>(), FriendsContract.View, PinnedHeaderExpandableListView.OnHeaderUpdateListener {

    var friendsAdapter: MainFriendsAdapter? = null

    var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_layout)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
        initToolbar()
    }

    override fun initToolbar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.mipmap.white_arrow_left)
        mainToolBarTitle!!.text = "好友"

        mHandler = Handler(Looper.myLooper())
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun finishFriendsRefresh() {
        expandableRefreshLayout.finishRefreshing()
    }

    /**
     * 设置好友适配器
     */
    fun setFriendsAdapter(groupList: MutableList<FriendsGroupVo>, childList: ArrayList<List<FriendsEntityVo>>) {
        if (friendsAdapter == null) {
            friendsAdapter = MainFriendsAdapter(this, groupList, childList, true, true)
            mainExpandableListView.setAdapter(friendsAdapter)
            mainExpandableListView.setOnHeaderUpdateListener(this)
        } else {
            friendsAdapter?.refreshData(groupList, childList, false, false)
        }
    }

    override fun getPinnedHeader(): View {
        val view = this.layoutInflater
                .inflate(R.layout.main_expandable_group, mainExpandableListView, false)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return view
    }

    override fun updatePinnedHeader(headerView: View?, firstVisibleGroupPos: Int) {
        if (friendsAdapter == null) return
        if (friendsAdapter!!.groupList.isEmpty()) return
        val mainGroup = friendsAdapter!!.groupList[firstVisibleGroupPos]
        val tvGroupName = headerView?.findViewById(R.id.main_group_name) as TextView
        val tvCount = headerView.findViewById(R.id.main_group_count) as TextView
        tvGroupName.text = mainGroup.name
        tvCount.text = mainGroup.count.toString()    }
}