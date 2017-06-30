package com.storn.freechat.adapter

import am.widget.shapeimageview.ShapeImageView
import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.*
import android.view.animation.Animation
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.DensityUtil
import com.common.util.TimeUtil
import com.common.widget.CopyPopWindow
import com.common.widget.SurroundImageView
import com.storn.freechat.R
import com.storn.freechat.chat.ChatRoomAct
import com.storn.freechat.me.ui.ProfileDetailAct
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.ChatMessageEntityVo

/**
 * 聊天消息适配器
 * Created by tianshutong on 2017/1/9.
 */

class ChatMessageAdapter(private val mContext: Context, private val mChatList: MutableList<ChatMessageEntityVo>) : BaseAdapter() {
    private var mLocationX: Int = 0

    /**
     * 加入单条数据

     * @param chatMessageEntity
     */
    fun addChatData(chatMessageEntity: ChatMessageEntityVo?) {
        if (chatMessageEntity != null) {
            mChatList.add(chatMessageEntity)
            notifyDataSetChanged()
        }
    }

    /**
     * 加载更多

     * @param mChatList
     */
    fun addChatList(mChatList: List<ChatMessageEntityVo>) {
        if (mChatList.isNotEmpty()) {
            this.mChatList.addAll(0, mChatList)
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return mChatList.size
    }

    override fun getItem(i: Int): Any {
        return mChatList[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return Constants.CHAT_MESSAGE_TYPE_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        return mChatList[position].type
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View? {
        var convertView = view

        val type = getItemViewType(position)
        var fromMessageHolder: FromMessageHolder? = null
        var toMessageHolder: ToMessageHolder? = null
        if (convertView == null) {
            when (type) {
                Constants.CHAT_MESSAGE_TYPE_FROM -> {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_room_left_layout, viewGroup, false)
                    fromMessageHolder = FromMessageHolder(convertView)
                    convertView!!.tag = fromMessageHolder
                }
                Constants.CHAT_MESSAGE_TYPE_TO -> {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_room_right_layout, viewGroup, false)
                    toMessageHolder = ToMessageHolder(convertView)
                    convertView!!.tag = toMessageHolder
                }
                else -> {
                }
            }
        } else {
            when (type) {
                Constants.CHAT_MESSAGE_TYPE_FROM -> {
                    fromMessageHolder = convertView.tag as FromMessageHolder
                }
                Constants.CHAT_MESSAGE_TYPE_TO -> {
                    toMessageHolder = convertView.tag as ToMessageHolder
                }
                else -> {
                }
            }
        }

        val chatMessageEntity = mChatList[position]
        val currentTime = chatMessageEntity.time
        if (position == 0) {
            when (type) {
                Constants.CHAT_MESSAGE_TYPE_FROM -> {
                    fillFromMessage(fromMessageHolder, chatMessageEntity, position, chatMessageEntity.cId)
                }

                Constants.CHAT_MESSAGE_TYPE_TO -> {
                    fillToMessage(toMessageHolder, chatMessageEntity, position, chatMessageEntity.cId)
                }
            }
        } else {
            val preChatEntity = mChatList[position - 1]
            val preTime = preChatEntity.time
            if (haveTimeGap(preTime, currentTime)) {
                when (type) {
                    Constants.CHAT_MESSAGE_TYPE_FROM -> {
                        fromMessageHolder!!.dateLayout.visibility = View.VISIBLE
                        fillFromMessage(fromMessageHolder, chatMessageEntity, position, chatMessageEntity.cId)
                    }
                    Constants.CHAT_MESSAGE_TYPE_TO -> {
                        toMessageHolder!!.dateLayout.visibility = View.VISIBLE
                        fillToMessage(toMessageHolder, chatMessageEntity, position, chatMessageEntity.cId)
                    }
                }
            } else {
                when (type) {
                    Constants.CHAT_MESSAGE_TYPE_FROM -> {
                        fromMessageHolder!!.dateLayout.visibility = View.GONE
                        fillFromMessage(fromMessageHolder, chatMessageEntity, position, chatMessageEntity.cId)
                    }
                    Constants.CHAT_MESSAGE_TYPE_TO -> {
                        toMessageHolder!!.dateLayout.visibility = View.GONE
                        fillToMessage(toMessageHolder, chatMessageEntity, position, chatMessageEntity.cId)
                    }
                }
            }
        }

        return convertView
    }

    private fun fillFromMessage(fromMessageHolder: FromMessageHolder?,
                                messageEntityVo: ChatMessageEntityVo, position: Int, cId: Int) {
        val currentTime = messageEntityVo.time
        val showTime = TimeUtil.formatChatDate(currentTime)
        if (TextUtils.isEmpty(showTime)) {
            fromMessageHolder!!.tvDate.text = ""
        } else {
            fromMessageHolder!!.tvDate.text = showTime
        }
        val content = messageEntityVo.content
        fromMessageHolder.tvContent.text = content

//        fromMessageHolder.headView.setOnClickListener {
//            val intent = Intent(mContext, ProfileDetailAct::class.java)
//            val context = mContext as ChatRoomAct
//            val options = ActivityOptions.makeSceneTransitionAnimation(context, fromMessageHolder.headView, mContext.resources.getString(R.string.profile_head))
//            mContext.startActivity(intent, options.toBundle())
//        }

        fromMessageHolder.contentLayout.setOnTouchListener(onTouchListener)
        fromMessageHolder.contentLayout.setOnLongClickListener { v: View ->
            showCopyPop(v, 0, content, position, cId, fromMessageHolder.rootLayout)
            false
        }
    }

    private fun fillToMessage(toMessageHolder: ToMessageHolder?,
                              messageEntityVo: ChatMessageEntityVo, position: Int, cId: Int) {
        val currentTime = messageEntityVo.time
        val showTime = TimeUtil.formatChatDate(currentTime)
        if (TextUtils.isEmpty(showTime)) {
            toMessageHolder!!.tvDate.text = ""
        } else {
            toMessageHolder!!.tvDate.text = showTime
        }
        val content = messageEntityVo.content
        toMessageHolder.tvContent.text = content

        toMessageHolder.headView.setOnClickListener {
            val intent = Intent(mContext, ProfileDetailAct::class.java)
            val context = mContext as ChatRoomAct
            val options = ActivityOptions.makeSceneTransitionAnimation(context, toMessageHolder.headView, mContext.resources.getString(R.string.profile_head))
            mContext.startActivity(intent, options.toBundle())
        }

        toMessageHolder.contentLayout.setOnTouchListener(onTouchListener)
        toMessageHolder.contentLayout.setOnLongClickListener { v: View ->
            showCopyPop(v, 1, content, position, cId, toMessageHolder.rootLayout)
            false
        }
    }

    private val onTouchListener = View.OnTouchListener { v, event ->
        v.performClick()
        if (event.action == MotionEvent.ACTION_DOWN) {
            mLocationX = event.x.toInt()
        }
        false
    }

    private fun showCopyPop(v: View, type: Int, content: String, position: Int, cId: Int, rootView: View) {
        val builder = CopyPopWindow.Builder(mContext)
        builder.setOnWhichClickListener { _: View, pos: Int ->
            when (pos) {
                0 -> copy(mContext, content)
                1 -> Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show()
                2 -> deleteItem(rootView, position, cId)
            }
        }
        builder.setType(type)
        val location = IntArray(2)
        v.getLocationInWindow(location)
        val copyPopWindow = builder.create()
        if (!copyPopWindow.isShowing) {
            location[0] = v.left + mLocationX - DensityUtil.dip2px(mContext, 200f) / 2
            copyPopWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - v.measuredHeight)
        }
    }

    /**
     * 是否显示时间

     * @param lastTime
     * *
     * @param time
     * *
     * @return
     */
    private fun haveTimeGap(lastTime: Long, time: Long): Boolean {
        val gap = 1000 * 60 * 2
        return time - lastTime > gap
    }

    /**
     * 复制文本

     * @param context
     * *
     * @param content
     */
    private fun copy(context: Context, content: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("copy", content)
        clipboardManager.primaryClip = clipData
    }

    /**
     * 删除单挑聊天记录

     * @param view
     * *
     * @param position
     * *
     * @param cId
     */
    private fun deleteItem(view: View, position: Int, cId: Int) {
        val listener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                DBHelper.getInstance().deleteChatMessage(mContext, cId)
                mChatList.removeAt(position)
                notifyDataSetChanged()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        }
        AnimationUtil.collapse(view, listener)
    }

    class FromMessageHolder constructor(itemView: View) {
        val tvDate = itemView.findViewById(R.id.chat_room_tv_date) as TextView
        val tvContent = itemView.findViewById(R.id.chat_room_tv_content) as TextView
        val dateLayout = itemView.findViewById(R.id.chat_room_time_layout) as LinearLayout
        val contentLayout = itemView.findViewById(R.id.chat_room_content_layout) as LinearLayout
        val rootLayout = itemView.findViewById(R.id.chat_room_root_layout) as LinearLayout
        val headView = itemView.findViewById(R.id.chat_room_head_view) as SurroundImageView
        val shapeImageView = itemView.findViewById(R.id.chat_room_shape_view) as ShapeImageView
    }

    class ToMessageHolder constructor(itemView: View) {
        val tvDate = itemView.findViewById(R.id.chat_room_tv_date) as TextView
        val tvContent = itemView.findViewById(R.id.chat_room_tv_content) as TextView
        val dateLayout = itemView.findViewById(R.id.chat_room_time_layout) as LinearLayout
        val contentLayout = itemView.findViewById(R.id.chat_room_content_layout) as LinearLayout
        val rootLayout = itemView.findViewById(R.id.chat_room_root_layout) as LinearLayout
        val headView = itemView.findViewById(R.id.chat_room_head_view) as SurroundImageView
        val shapeImageView = itemView.findViewById(R.id.chat_room_shape_view) as ShapeImageView
    }
}
