package com.storn.freechat.chat.adapter

import android.app.ActivityOptions
import android.content.*
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.text.TextUtils
import android.view.*
import android.view.animation.Animation
import android.widget.*
import cn.iwgang.countdownview.CountdownView
import com.common.common.Constants
import com.common.util.*
import com.common.widget.ConfirmDialog
import com.common.widget.CopyPopWindow
import com.common.widget.SurroundImageView
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView
import com.storn.freechat.R
import com.storn.freechat.chat.ui.ChatRoomAct
import com.storn.freechat.manager.UserManager
import com.storn.freechat.me.ui.ProfileDetailAct
import com.storn.freechat.util.DBHelper
import com.storn.freechat.vo.ChatMessageEntityVo
import java.io.IOException

/**
 * 聊天消息适配器
 * Created by tianshutong on 2017/1/9.
 */

class ChatMessageAdapter(val mContext: Context, var mChatList: MutableList<ChatMessageEntityVo>,
                         val mListView: ListView) : BaseAdapter() {
    private var mLocationX: Int = 0
    private var mPlayer = MediaPlayer()
    private val MIN_AUDIO_LINE_WIDTH = 60

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

    /**
     * 更新单条消息
     */
    fun updateItem(id: Int) {
        val first = mListView.firstVisiblePosition
        val last = mListView.lastVisiblePosition
        for (i in first..last) {
            if (id == this.mChatList[i].cId) {
                val itemView = mListView.getChildAt(i + 1 - first)
                getView(i, itemView, mListView)
                break
            }
        }
    }

    override fun getCount(): Int {
        return mChatList.size
    }

    override fun getItem(i: Int): Any {
        return mChatList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
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

        fillData(fromMessageHolder, toMessageHolder, type, position)

        return convertView
    }

    /**
     * 填充数据
     */
    private fun fillData(fromMessageHolder: FromMessageHolder?,
                         toMessageHolder: ToMessageHolder?, type: Int, position: Int) {
        val chatMessageEntity = mChatList[position]

        val currentTime = chatMessageEntity.time
        if (position == 0) {
            when (type) {
                Constants.CHAT_MESSAGE_TYPE_FROM -> {
                    fromMessageHolder!!.dateLayout.visibility = View.VISIBLE
                    fillFromMessage(fromMessageHolder, position, chatMessageEntity.cId)
                }

                Constants.CHAT_MESSAGE_TYPE_TO -> {
                    toMessageHolder!!.dateLayout.visibility = View.VISIBLE
                    fillToMessage(toMessageHolder, position, chatMessageEntity.cId)
                }
            }
        } else {
            val preChatEntity = mChatList[position - 1]
            val preTime = preChatEntity.time
            if (haveTimeGap(preTime, currentTime)) {
                when (type) {
                    Constants.CHAT_MESSAGE_TYPE_FROM -> {
                        fromMessageHolder!!.dateLayout.visibility = View.VISIBLE
                        fillFromMessage(fromMessageHolder, position, chatMessageEntity.cId)
                    }
                    Constants.CHAT_MESSAGE_TYPE_TO -> {
                        toMessageHolder!!.dateLayout.visibility = View.VISIBLE
                        fillToMessage(toMessageHolder, position, chatMessageEntity.cId)
                    }
                }
            } else {
                when (type) {
                    Constants.CHAT_MESSAGE_TYPE_FROM -> {
                        fromMessageHolder!!.dateLayout.visibility = View.GONE
                        fillFromMessage(fromMessageHolder, position, chatMessageEntity.cId)
                    }
                    Constants.CHAT_MESSAGE_TYPE_TO -> {
                        toMessageHolder!!.dateLayout.visibility = View.GONE
                        fillToMessage(toMessageHolder, position, chatMessageEntity.cId)
                    }
                }
            }
        }
    }

    /**
     * 填充好友消息
     */
    private fun fillFromMessage(fromMessageHolder: FromMessageHolder?, position: Int, cId: Int) {

        fillDate(position, fromMessageHolder!!.tvDate)

        fillFromMessageType(position, fromMessageHolder)

//        fromMessageHolder.headView.setOnClickListener {
//            val intent = Intent(mContext, ProfileDetailAct::class.java)
//            val context = mContext as ChatRoomAct
//            val options = ActivityOptions.makeSceneTransitionAnimation(context, fromMessageHolder.headView, mContext.resources.getString(R.string.profile_head))
//            mContext.startActivity(intent, options.toBundle())
//        }

        audioClick(position, fromMessageHolder.audioLayout, fromMessageHolder.audioImg,
                fromMessageHolder.audioTime)

        fromMessageHolder.audioTime.setOnCountdownEndListener {
            fromMessageHolder.audioImg.setImageResource(R.mipmap.ic_audio_play_blue)
            fromMessageHolder.audioTime.stop()
            fromMessageHolder.audioTime.updateShow(mChatList[position].audioTime)
            stopAudio()
        }

        fromMessageHolder.contentLayout.setOnTouchListener(onTouchListener)
        fromMessageHolder.contentLayout.setOnLongClickListener { v: View ->
            if (mChatList[position].messageType == Constants.CHAT_MESSAGE_TXT) {
                showCopyPop(v, 0, mChatList[position].content, position, cId, fromMessageHolder.rootLayout)
            }
            false
        }
    }

    /**
     * 填充我的消息
     */
    private fun fillToMessage(toMessageHolder: ToMessageHolder?, position: Int, cId: Int) {
        fillProgressView(position, toMessageHolder)

        fillDate(position, toMessageHolder!!.tvDate)

        fillMyHeadView(toMessageHolder)

        fillToMessageType(position, toMessageHolder)

        audioClick(position, toMessageHolder.audioLayout, toMessageHolder.audioImg,
                toMessageHolder.audioTime)

        toMessageHolder.headView.setOnClickListener {
            val intent = Intent(mContext, ProfileDetailAct::class.java)
            val context = mContext as ChatRoomAct
            val options = ActivityOptions.makeSceneTransitionAnimation(context, toMessageHolder.headView, mContext.resources.getString(R.string.profile_head))
            mContext.startActivity(intent, options.toBundle())
        }

        //重发
        toMessageHolder.progressView.setOnClickListener {
            val messageEntityVo = mChatList[position]
            when (messageEntityVo.messageType) {
                Constants.CHAT_MESSAGE_TXT -> {
                    showReSendDialog(position, messageEntityVo.content)
                }
                Constants.CHAT_MESSAGE_AUDIO -> {

                }
                Constants.CHAT_MESSAGE_PIC -> {

                }
            }
        }

        toMessageHolder.contentLayout.setOnTouchListener(onTouchListener)
        toMessageHolder.contentLayout.setOnLongClickListener { v: View ->
            val messageEntityVo = mChatList[position]
            if (messageEntityVo.messageType == Constants.CHAT_MESSAGE_TXT) {
                showCopyPop(v, 1, messageEntityVo.content, position, cId, toMessageHolder.rootLayout)
            }
            false
        }
    }

    //记录上次点击位置
    var oldPosition = -1
    //播放语音
    var isPlaying = false
    //是否第一次播放
    var isFirstPlay = true

    /**
     * 语音点击事件
     */
    private fun audioClick(position: Int, audioLayout: LinearLayout,
                           audioImg: ImageView, audioTime: CountdownView) {

        audioLayout.setOnClickListener {
            val messageEntityVo = mChatList[position]

            if (oldPosition != -1 && oldPosition != position) {
                stopAudio()
                updateItem(mChatList[oldPosition].cId)
            }

            if (isPlaying) {
                pauseAudio()
                when (messageEntityVo.type) {
                    Constants.CHAT_MESSAGE_TYPE_FROM -> {
                        audioImg.setImageResource(R.mipmap.ic_audio_play_blue)
                    }
                    Constants.CHAT_MESSAGE_TYPE_TO -> {
                        audioImg.setImageResource(R.mipmap.ic_audio_play_white)
                    }
                }
                audioTime.pause()
            } else {
                if (isFirstPlay) {
                    try {
                        mPlayer.setDataSource(messageEntityVo.audioPath)
                        mPlayer.prepare()
                        playAudio()
                        when (messageEntityVo.type) {
                            Constants.CHAT_MESSAGE_TYPE_FROM -> {
                                audioImg.setImageResource(R.mipmap.ic_audio_pause_blue)
                            }
                            Constants.CHAT_MESSAGE_TYPE_TO -> {
                                audioImg.setImageResource(R.mipmap.ic_audio_pause_white)
                            }
                        }
                        audioTime.start(messageEntityVo.audioTime)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    playAudio()
                    audioImg.setImageResource(R.mipmap.ic_audio_pause_white)
                    audioTime.restart()
                }
            }

            oldPosition = position

        }

        audioTime.setOnCountdownEndListener {
            val messageEntityVo = mChatList[position]

            when (messageEntityVo.type) {
                Constants.CHAT_MESSAGE_TYPE_FROM -> {
                    audioImg.setImageResource(R.mipmap.ic_audio_play_blue)
                }
                Constants.CHAT_MESSAGE_TYPE_TO -> {
                    audioImg.setImageResource(R.mipmap.ic_audio_play_white)
                }
            }
            audioTime.stop()
            audioTime.updateShow(messageEntityVo.audioTime)
            stopAudio()
        }
    }

    private fun playAudio() {
        mPlayer.start()
        isFirstPlay = false
        isPlaying = true
    }

    fun pauseAudio() {
        if (isPlaying) {
            mPlayer.pause()
            isPlaying = false
        }
    }

    fun stopAudio() {
        mPlayer.stop()
        mPlayer.reset()
        isPlaying = false
        isFirstPlay = true
    }

    /**
     * 释放player
     */
    fun releasePlayer() {
        mPlayer.stop()
        mPlayer.release()
        isPlaying = false
        isFirstPlay = true
    }

    /**
     * 消息发送的状态
     */
    private fun fillProgressView(position: Int, toMessageHolder: ToMessageHolder?) {
        when (mChatList[position].status) {
            Constants.CHAT_SEND_ING -> {
                toMessageHolder?.progressView!!.visibility = View.VISIBLE
                toMessageHolder.errorView.visibility = View.GONE
            }
            Constants.CHAT_SEND_SUCCESS -> {
                toMessageHolder?.progressView!!.visibility = View.GONE
                toMessageHolder.errorView.visibility = View.GONE
            }
            Constants.CHAT_SEND_FAIL -> {
                toMessageHolder?.progressView!!.clearAnimation()
                toMessageHolder.progressView.visibility = View.GONE
                toMessageHolder.errorView.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 发送时间
     */
    private fun fillDate(position: Int, tvDate: TextView) {
        val currentTime = mChatList[position].time
        val showTime = TimeUtil.formatChatDate(currentTime)
        if (TextUtils.isEmpty(showTime)) {
            tvDate.text = ""
        } else {
            tvDate.text = showTime
        }
    }

    /**
     * 我的头像
     */
    private fun fillMyHeadView(toMessageHolder: ToMessageHolder) {
        val userVo = DBHelper.getInstance().queryProfileInfo(mContext, PreferenceTool.getString(Constants.LOGIN_JID))
        if (userVo != null) {
            if (userVo.img != null && userVo.img.isNotEmpty()) {
                GlideHelper.showHeadViewWithNoAnim(mContext, userVo.img, toMessageHolder.headView)
            } else {
                val inputStream = UserManager.getInstance().getUserHead(userVo.jid)
                if (inputStream != null) {
                    toMessageHolder.headView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
                } else {
                    toMessageHolder.headView.setImageResource(R.mipmap.default_head_2)
                }
            }
        } else {
            toMessageHolder.headView.setImageResource(R.mipmap.default_head_2)
        }
    }

    /**
     * 填充好友的消息类型
     * 文本
     * 语音
     * 图片
     * @param position
     * @param fromMessageHolder
     */
    private fun fillFromMessageType(position: Int, fromMessageHolder: FromMessageHolder?) {
        val messageEntityVo = mChatList[position]
        when (messageEntityVo.messageType) {
            Constants.CHAT_MESSAGE_TXT -> {
                fromMessageHolder!!.tvContent.text = messageEntityVo.content
                fromMessageHolder.shapeImageView.visibility = View.GONE
                fromMessageHolder.audioLayout.visibility = View.GONE
                fromMessageHolder.tvContent.visibility = View.VISIBLE
                fromMessageHolder.contentLayout.visibility = View.VISIBLE
            }
            Constants.CHAT_MESSAGE_AUDIO -> {
                if (oldPosition != -1 && !isFirstPlay && isPlaying && oldPosition == position) {
                    fromMessageHolder!!.audioImg.setImageResource(R.mipmap.ic_audio_pause_blue)
                } else {
                    fromMessageHolder!!.audioImg.setImageResource(R.mipmap.ic_audio_play_blue)
                }
                //此处可能会导致卡顿
                if (oldPosition != -1 && oldPosition == position && !isFirstPlay && !isPlaying) {
                    fromMessageHolder.audioTime.pause()
                } else if (oldPosition != -1 && oldPosition == position && isPlaying) {
                    fromMessageHolder.audioTime.updateShow(messageEntityVo.audioTime - mPlayer.currentPosition.toLong())
                    fromMessageHolder.audioTime.start(messageEntityVo.audioTime - mPlayer.currentPosition.toLong())
                } else {
                    fromMessageHolder.audioTime.updateShow(messageEntityVo.audioTime)
                    fromMessageHolder.audioTime.stop()
                }

                val lp = fromMessageHolder.audioLine.layoutParams as LinearLayout.LayoutParams
                lp.width = MIN_AUDIO_LINE_WIDTH +
                        TimeUtil.convertToSec(messageEntityVo.audioTime).toInt() * 3
                fromMessageHolder.audioLine.layoutParams = lp

                fromMessageHolder.tvContent.visibility = View.GONE
                fromMessageHolder.shapeImageView.visibility = View.GONE
                fromMessageHolder.audioLayout.visibility = View.VISIBLE
                fromMessageHolder.contentLayout.visibility = View.VISIBLE
            }
            Constants.CHAT_MESSAGE_PIC -> {
                fromMessageHolder!!.contentLayout.visibility = View.GONE
                fromMessageHolder.shapeImageView.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 填充我的消息类型
     * 文本
     * 语音
     * 图片
     * @param position
     * @param toMessageHolder
     */
    private fun fillToMessageType(position: Int, toMessageHolder: ToMessageHolder?) {
        val messageEntityVo = mChatList[position]

        when (messageEntityVo.messageType) {
            Constants.CHAT_MESSAGE_TXT -> {
                toMessageHolder!!.tvContent.text = messageEntityVo.content
                toMessageHolder.shapeImageView.visibility = View.GONE
                toMessageHolder.audioLayout.visibility = View.GONE
                toMessageHolder.tvContent.visibility = View.VISIBLE
                toMessageHolder.contentLayout.visibility = View.VISIBLE
            }
            Constants.CHAT_MESSAGE_AUDIO -> {
                if (oldPosition != -1 && !isFirstPlay && isPlaying && oldPosition == position) {
                    toMessageHolder!!.audioImg.setImageResource(R.mipmap.ic_audio_pause_white)
                } else {
                    toMessageHolder!!.audioImg.setImageResource(R.mipmap.ic_audio_play_white)
                }

                //此处会导致卡顿
                if (oldPosition != -1 && oldPosition == position && !isFirstPlay && !isPlaying) {
                    toMessageHolder.audioTime.pause()
                } else if (oldPosition != -1 && oldPosition == position && isPlaying) {
                    toMessageHolder.audioTime.updateShow(messageEntityVo.audioTime - mPlayer.currentPosition.toLong())
                    toMessageHolder.audioTime.start(messageEntityVo.audioTime - mPlayer.currentPosition.toLong())
                } else {
                    toMessageHolder.audioTime.updateShow(messageEntityVo.audioTime)
                    toMessageHolder.audioTime.stop()
                }

                val lp = toMessageHolder.audioLine.layoutParams as LinearLayout.LayoutParams
                lp.width = MIN_AUDIO_LINE_WIDTH +
                        TimeUtil.convertToSec(messageEntityVo.audioTime).toInt() * 3
                toMessageHolder.audioLine.layoutParams = lp

                toMessageHolder.tvContent.visibility = View.GONE
                toMessageHolder.shapeImageView.visibility = View.GONE
                toMessageHolder.audioLayout.visibility = View.VISIBLE
                toMessageHolder.contentLayout.visibility = View.VISIBLE
            }
            Constants.CHAT_MESSAGE_PIC -> {
                toMessageHolder!!.contentLayout.visibility = View.GONE
                toMessageHolder.shapeImageView.visibility = View.VISIBLE
            }
        }
    }

    private val onTouchListener = View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            mLocationX = event.x.toInt()
        }
        false
    }

    /**
     * 显示重发提示框
     */
    private fun showReSendDialog(position: Int, content: String) {
        var dialog: ConfirmDialog? = null
        val builder = ConfirmDialog.Builder(mContext)
        builder.setTitle(R.string.setting_login_out_title)
        builder.setMessage(R.string.resend_message)
        builder.setAutoDismiss(true)
        builder.setContentPanelHeight(mContext.resources.getDimension(R.dimen.content_panel_min_height).toInt())
        builder.setPositiveButton(mContext.getString(R.string.confirm)) { _: DialogInterface, _: Int ->
            if (!NetworkUtil.isNetworkConnected(mContext))
                CommonUtil.showToast(mContext, R.string.error_network)
            else
                if (ChatRoomAct.chatHandler != null) {
                    val message = android.os.Message.obtain()
                    message.what = Constants.RESEND_CHAT_MESSAGE
                    message.obj = content
                    message.arg1 = position
                    ChatRoomAct.chatHandler?.sendMessage(message)
                }
        }
        builder.setNegativeButton(mContext.getString(R.string.cancel)
        ) { _: DialogInterface, _: Int ->
        }
        if (dialog == null) {
            dialog = builder.create()
        }
        dialog?.setCanceledOnTouchOutside(true)
        if (!dialog!!.isShowing) {
            dialog.show()
        }
    }

    /**
     * 显示复制
     */
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
     * 删除单条聊天记录

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

        val shapeImageView = itemView.findViewById(R.id.chat_room_shape_view) as PorterShapeImageView

        val audioLayout = itemView.findViewById(R.id.chat_room_audio_layout) as LinearLayout
        val audioImg = itemView.findViewById(R.id.chat_audio_img) as ImageView
        val audioTime = itemView.findViewById(R.id.chat_audio_time) as CountdownView
        val audioLine = itemView.findViewById(R.id.chat_audio_line) as ImageView
    }

    class ToMessageHolder constructor(itemView: View) {
        val tvDate = itemView.findViewById(R.id.chat_room_tv_date) as TextView
        val tvContent = itemView.findViewById(R.id.chat_room_tv_content) as TextView
        val dateLayout = itemView.findViewById(R.id.chat_room_time_layout) as LinearLayout
        val contentLayout = itemView.findViewById(R.id.chat_room_content_layout) as LinearLayout
        val rootLayout = itemView.findViewById(R.id.chat_room_root_layout) as LinearLayout
        val headView = itemView.findViewById(R.id.chat_room_head_view) as SurroundImageView
        val progressView = itemView.findViewById(R.id.chat_progress) as ProgressBar
        val errorView = itemView.findViewById(R.id.chat_error_view) as ImageView

        val shapeImageView = itemView.findViewById(R.id.chat_room_shape_view) as PorterShapeImageView

        val audioLayout = itemView.findViewById(R.id.chat_room_audio_layout) as LinearLayout
        val audioImg = itemView.findViewById(R.id.chat_audio_img) as ImageView
        val audioTime = itemView.findViewById(R.id.chat_audio_time) as CountdownView
        val audioLine = itemView.findViewById(R.id.chat_audio_line) as ImageView
    }
}
