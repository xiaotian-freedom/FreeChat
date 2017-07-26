package com.storn.freechat.group

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.AppCompatEditText
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.common.common.Constants
import com.common.util.CommonUtil
import com.common.util.SoftKeyBoardUtil
import com.common.widget.ConfirmDialog
import com.jude.beam.expansion.BeamBasePresenter
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout
import com.storn.freechat.R
import com.storn.freechat.chat.ui.MultiChatRoomAct
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.jni.FreeChatCommon
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.GroupEntityVo
import com.storn.freechat.vo.MessageEntityVo
import kotlinx.android.synthetic.main.activity_group_layout.*
import kotlinx.android.synthetic.main.activity_home.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.RoomInfo

/**
 * 聊天室控制器
 * Created by tianshutong on 2017/7/14.
 */
class GroupPresenter : BeamBasePresenter<GroupAct>(), GroupContract.Presenter {

    //create chat room
    var customView: View? = null
    var dialog: ConfirmDialog? = null
    var createEditText: AppCompatEditText? = null

    override fun onCreateView(view: GroupAct) {
        super.onCreateView(view)
        initListener()
        queryLocalChatRooms()
        view.mHandler!!.postDelayed({
            getChatRooms()
        }, Constants.DELAY_1000.toLong())
    }

    override fun initListener() {
        view.fab.setOnClickListener { fabClickListener() }
        view.toolbar.setNavigationOnClickListener { view.onBackPressed() }
        view.groupRefreshLayout.setHeaderView(BezierLayout(view))
        view.groupRefreshLayout.setOnRefreshListener(mGroupListListener)
    }

    override fun queryLocalChatRooms() {
        val mGroupList = DBHelper.getInstance().queryGroupList(view)
        if (mGroupList.isNotEmpty()) view.setGroupAdapter(mGroupList)
    }

    override fun getChatRooms() {
        val connection = XMPPConnectionManager.getInstance().connection
        if (!XMPPConnectionManager.getInstance().isLogin) return
        val mGroupList = mutableListOf<GroupEntityVo>()
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
                    DBHelper.getInstance().insertOrUpdateGroup(view, groupVo)
                }
            }
            if (mGroupList.isNotEmpty()) {
                view.setGroupAdapter(mGroupList)
            }
            view.finishGroupRefresh()
        } catch (e: SmackException.NoResponseException) {
            e.printStackTrace()
        } catch (e: XMPPException.XMPPErrorException) {
            e.printStackTrace()
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        }
    }

    override fun showChatRoomDialog() {
        view.startRotation()
        if (customView == null) {
            customView = LayoutInflater.from(view).inflate(R.layout.create_chat_room_layout, view.rootView, false)
            customView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    view.resources.getDimension(R.dimen.space_i_10).toInt())
            createEditText = customView!!.findViewById(R.id.create_chat_room_edit) as AppCompatEditText
        }
        val builder = ConfirmDialog.Builder(view)
        builder.setTitle(R.string.create_room)
        builder.setCustomView(customView)
        builder.setContentPanelHeight(view.resources.getDimension(R.dimen.content_panel_height).toInt())
        builder.setAutoDismiss(true)
        builder.setPositiveButton(view.getString(R.string.confirm)) { _: DialogInterface, _: Int ->
            val chatRoom = createEditText!!.text.toString().trim({ it <= ' ' })
            if (TextUtils.isEmpty(chatRoom)) {
                CommonUtil.showToast(view, view.getString(R.string.create_room_empty))
            } else {
                if (chatRoom.length < 2) {
                    CommonUtil.showToast(view, view.getString(R.string.create_room_tip_min))
                } else {
                    createChatRooms(chatRoom)
                }
            }
        }
        builder.setNegativeButton(view.getString(R.string.cancel)
        ) { _: DialogInterface, _: Int ->
            SoftKeyBoardUtil.hideSoftKeyboard(view)
        }
        if (dialog == null) {
            dialog = builder.create()
        }
        dialog?.setCanceledOnTouchOutside(true)
        if (!dialog!!.isShowing) {
            dialog?.show()
        }
        dialog?.setOnDismissListener { view.endRotation() }
    }

    override fun createChatRooms(roomName: String) {
        view.showLoadToast(view.resources.getString(R.string.create_room_ing))
        val connection = XMPPConnectionManager.getInstance().connection
        val mMultiUserChatManager = MultiUserChatManager.getInstanceFor(connection)
        val mUserVo = ChatApplication.getUserVo()
        var created: Boolean

        view.mHandler!!.postDelayed({
            try {
                val SERVICE_NAME = mMultiUserChatManager.serviceNames
                val userChat = mMultiUserChatManager.getMultiUserChat(roomName + "@" + SERVICE_NAME[0])
                userChat.create(mUserVo.name)
                created = XMPPConnectionManager.getInstance().configChatRoom(userChat)

            } catch (e: XMPPException.XMPPErrorException) {
                e.printStackTrace()
                created = false
            } catch (e: SmackException) {
                e.printStackTrace()
                created = false
            }

            if (created) {
                view.toastSuccess()
                view.mHandler!!.postDelayed({
                    getChatRooms()
                }, Constants.DELAY_1000.toLong())
            } else {
                view.toastError()
            }
        }, Constants.DELAY_2000.toLong())
    }

    override fun goMultiChat(position: Int) {
        if (view.groupAdapter!!.mList.isNotEmpty()) {
            val groupEntity = view.groupAdapter!!.mList[position]
            if (groupEntity.roomJid == "my_server@conference.freechat.storn.com") {
                CommonUtil.showToast(view, "拒绝加入")
            } else {
                val intent = Intent(view, MultiChatRoomAct::class.java)
                val messageEntity = MessageEntityVo()
                messageEntity.jid = groupEntity.roomJid
                messageEntity.roomName = groupEntity.roomName
                intent.putExtra(Constants.MESSAGEVO, messageEntity)
                startActivity(intent)
            }
        }
    }

    private val mGroupListListener = object : RefreshListenerAdapter() {
        override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
            view.mHandler!!.postDelayed({ getChatRooms() }, Constants.DELAY_1000.toLong())
        }
    }

    override fun fabClickListener() {
        showChatRoomDialog()
    }
}