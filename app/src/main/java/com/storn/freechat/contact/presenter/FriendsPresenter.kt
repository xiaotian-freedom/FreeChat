package com.storn.freechat.contact.presenter

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.ExpandableListView
import com.common.common.Constants
import com.jude.beam.expansion.BeamBasePresenter
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.storn.freechat.chat.ui.ChatRoomAct
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.contact.ui.FriendsAct
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.FriendsEntityVo
import com.storn.freechat.vo.FriendsGroupVo
import com.storn.freechat.vo.MessageEntityVo
import kotlinx.android.synthetic.main.activity_friends_layout.*
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import java.util.*

/**
 * 好友控制器
 * Created by tianshutong on 2017/7/14.
 */
class FriendsPresenter : BeamBasePresenter<FriendsAct>(), FriendsContract.Presenter, ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener {

    override fun onCreateView(view: FriendsAct) {
        super.onCreateView(view)
        initListener()
        queryLocalFriends()
        view.mHandler!!.postDelayed({
            getFriendsData()
        }, Constants.DELAY_1000.toLong())
    }

    fun initListener() {
        view.mainExpandableListView.setOnChildClickListener(this)
        view.mainExpandableListView.setOnGroupClickListener(this, true)
        view.expandableRefreshLayout.setHeaderView(ProgressLayout(view))
        view.expandableRefreshLayout.setOnRefreshListener(mExpandableListener)
    }

    override fun queryLocalFriends() {
        val userVo = ChatApplication.userVo
        if (userVo == null || userVo.jid.isEmpty()) return
        val groupList = DBHelper.getInstance().queryFriendsGroupList(view, userVo.jid)
        val childList = arrayListOf<List<FriendsEntityVo>>()
        val tempList = DBHelper.getInstance().queryFriendsList(view, userVo.jid)
        if (tempList.isNotEmpty()) childList.add(tempList)
        if (groupList.isNotEmpty()) view.setFriendsAdapter(groupList, childList)
    }

    override fun getFriendsData() {
        val connection = XMPPConnectionManager.getInstance().connection
        if (!connection.isConnected || !connection.isAuthenticated) return
        val roster = Roster.getInstanceFor(connection)
        val groupList = mutableListOf<FriendsGroupVo>()
        val childList = arrayListOf<List<FriendsEntityVo>>()

        val rosterEntries = roster.groups

        for (rosterGroup in rosterEntries) {
            val groupName = rosterGroup.name
            val count = rosterGroup.entryCount
            val friendsGroupVo = FriendsGroupVo()
            friendsGroupVo.name = groupName
            friendsGroupVo.count = count
            friendsGroupVo.myJid = ChatApplication.getUserVo().jid
            groupList.add(friendsGroupVo)
            DBHelper.getInstance().insertOrUpdateFriendsGroup(view, friendsGroupVo)

            val rosterEntryList = rosterGroup.entries
            val tempChildList = ArrayList<FriendsEntityVo>()
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
                friendsEntityVo.jid = rosterEntry.user
                friendsEntityVo.name = rosterEntry.name
                friendsEntityVo.nickName = rosterEntry.name
                friendsEntityVo.myJid = ChatApplication.getUserVo().jid
                tempChildList.add(friendsEntityVo)
                DBHelper.getInstance().insertOrUpdateFriends(view, friendsEntityVo)
            }
            childList.add(tempChildList)
        }
        if (groupList.isNotEmpty()) {
            view.setFriendsAdapter(groupList, childList)
        }
        view.finishFriendsRefresh()
    }

    override fun goChatRoom(groupPosition: Int, childPosition: Int) {
        val mainChild = view.friendsAdapter!!.childList[groupPosition][childPosition]
        val intent = Intent(view, ChatRoomAct::class.java)
        val messageEntity = MessageEntityVo()
        messageEntity.jid = mainChild.jid
        if (TextUtils.isEmpty(mainChild.nickName)) {
            messageEntity.fromName = mainChild.name
        } else {
            messageEntity.fromName = mainChild.nickName
        }
        intent.putExtra(Constants.MESSAGEVO, messageEntity)
        startActivity(intent)
    }

    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        goChatRoom(groupPosition, childPosition)
        return false
    }

    override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean {
        return false
    }

    private val mExpandableListener = object : RefreshListenerAdapter() {
        override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
            view.expandableRefreshLayout.postDelayed({ getFriendsData() }, Constants.DELAY_1000.toLong())
        }
    }
}