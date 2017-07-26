package com.storn.freechat.adapter

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.DensityUtil
import com.common.util.TimeUtil
import com.common.widget.dragindicator.DragIndicatorView
import com.storn.freechat.R
import com.storn.freechat.interfac.OnItemClickListener
import com.storn.freechat.vo.MessageEntityVo
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 我的消息适配器
 * Created by tianshutong on 2016/12/15.
 */

class MainMessageAdapter(val mContext: Context, var mList: MutableList<MessageEntityVo>,var isFirstAnim: Boolean)
    : SwipeMenuAdapter<MainMessageAdapter.MainMessageViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    fun setRefreshData(list: MutableList<MessageEntityVo>, isFirstAnim: Boolean) {
        if (list.isNotEmpty()) {
            this.mList = list
            this.isFirstAnim = isFirstAnim
            notifyDataSetChanged()
        }
    }

    fun deleteData(list: MutableList<MessageEntityVo>, position: Int) {
        if (list.isNotEmpty()) {
            this.mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateContentView(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(mContext).inflate(R.layout.main_message_list_item, parent, false)
    }

    override fun onCompatCreateViewHolder(realContentView: View, viewType: Int): MainMessageViewHolder {
        return MainMessageViewHolder(realContentView)
    }

    override fun getItemViewType(position: Int): Int {
        return mList[position].type
    }

    override fun onBindViewHolder(holder: MainMessageViewHolder, position: Int) {
        if (isFirstAnim) {
            AnimationUtil.runEnterAnimation(holder.itemView, DensityUtil.getScreenWidth(mContext), mList)
        }

        val messageEntity = mList[position]

        when (messageEntity.type) {
            0 -> {
                holder.tvName.text = messageEntity.fromName
                val color = (Math.random() * Constants.COLORS.size).toInt()
                holder.headView.setImageResource(Constants.COLORS[color])
                if (!TextUtils.isEmpty(messageEntity.fromName)) {
                    val length = messageEntity.fromName.length
                    val headName: String
                    if (length in 0..2) {
                        headName = messageEntity.fromName
                    } else {
                        headName = messageEntity.fromName.substring(messageEntity.fromName.length - 2)
                    }
                    holder.tvHeadName.text = headName
                } else {
                    holder.tvHeadName.text = ""
                }

                when(messageEntity.messageType) {
                    Constants.CHAT_MESSAGE_TXT -> {
                        holder.tvContent.text = messageEntity.content
                    }
                    Constants.CHAT_MESSAGE_AUDIO -> {
                        holder.tvContent.text = "[语音]"
                    }
                    Constants.CHAT_MESSAGE_PIC -> {
                        holder.tvContent.text = "[图片]"
                    }
                }

            }
            1 -> {
                holder.tvName.text = messageEntity.roomName
                val color = (Math.random() * Constants.COLORS.size).toInt()
                holder.headView.setImageResource(Constants.COLORS[color])
                val length = messageEntity.roomName.length
                val headName: String
                if (length in 0..2) {
                    headName = messageEntity.roomName
                } else {
                    headName = messageEntity.roomName.substring(messageEntity.roomName.length - 2)
                }
                holder.tvHeadName.text = headName

                val sb = StringBuilder()
                sb.append(messageEntity.fromName)
                sb.append(":")
                sb.append(messageEntity.content)
                holder.tvContent.text = sb.toString()
            }
            else -> {
            }
        }


        val time = messageEntity.time
        val showTime = TimeUtil.formatChatDate(time)
        if (TextUtils.isEmpty(showTime)) {
            holder.tvTime.text = ""
        } else {
            holder.tvTime.text = showTime
        }

        if (messageEntity.msgCount > 0) {
            holder.tvTip.text = messageEntity.msgCount.toString()
            holder.tvTip.visibility = View.VISIBLE
        } else {
            holder.tvTip.visibility = View.INVISIBLE
        }

        when (messageEntity.status) {
            Constants.CHAT_SEND_ING -> {
                holder.progressView.visibility = View.VISIBLE
                holder.progressView.setImageDrawable(mContext.resources.getDrawable(R.drawable.anim_loading_view))
                val animDrawable = holder.progressView.drawable as AnimationDrawable
                animDrawable.start()

                val lp = holder.progressView.layoutParams as LinearLayout.LayoutParams
                lp.width = 50
                lp.height = 50
                holder.progressView.layoutParams = lp
            }
            Constants.CHAT_SEND_SUCCESS -> {
                holder.progressView.visibility = View.GONE
            }
            Constants.CHAT_SEND_FAIL -> {
                holder.progressView.clearAnimation()
                holder.progressView.visibility = View.VISIBLE
                holder.progressView.setImageDrawable(mContext.getDrawable(R.mipmap.chat_fail_resend_normal))

                val lp = holder.progressView.layoutParams as LinearLayout.LayoutParams
                lp.width = 40
                lp.height = 40
                holder.progressView.layoutParams = lp
            }
        }

        holder.setOnItemClickListener(mOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MainMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var headView: CircleImageView
        var tvHeadName: TextView
        var tvName: TextView
        var tvTime: TextView
        var tvContent: TextView
        var tvTip: TextView
        var progressView: ImageView
        var mOnItemClickListener: OnItemClickListener? = null

        init {
            itemView.setOnClickListener(this)
            tvHeadName = itemView.findViewById(R.id.message_list_item_head_name) as TextView
            tvName = itemView.findViewById(R.id.message_list_item_tv_name) as TextView
            tvTime = itemView.findViewById(R.id.message_list_item_tv_time) as TextView
            tvContent = itemView.findViewById(R.id.message_list_item_tv_content) as TextView
            headView = itemView.findViewById(R.id.message_list_item_head_view) as CircleImageView
            tvTip = itemView.findViewById(R.id.message_tip) as DragIndicatorView
            progressView = itemView.findViewById(R.id.message_progress) as ImageView
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
            mOnItemClickListener = onItemClickListener
        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
//                AnimationUtil.startScaleAnim(this@MainMessageViewHolder.itemView)
                mOnItemClickListener!!.onItemClick(adapterPosition)
            }
        }
    }
}
