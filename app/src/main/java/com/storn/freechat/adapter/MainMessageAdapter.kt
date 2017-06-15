package com.storn.freechat.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.common.common.Constants
import com.common.util.AnimationUtil
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

class MainMessageAdapter(private val mContext: Context, private var mList: List<MessageEntityVo>) : SwipeMenuAdapter<MainMessageAdapter.MainMessageViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    fun setRefreshData(list: List<MessageEntityVo>) {
        if (list.isNotEmpty()) {
            this.mList = list
            notifyDataSetChanged()
        }
    }

    override fun onCreateContentView(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(mContext).inflate(R.layout.main_message_list_item, parent, false)
    }

    override fun onCompatCreateViewHolder(realContentView: View, viewType: Int): MainMessageViewHolder {
        return MainMessageViewHolder(realContentView)
    }

    override fun onBindViewHolder(holder: MainMessageViewHolder, position: Int) {
        //        AnimationUtil.runEnterAnimation(holder.itemView, DensityUtil.getScreenHeight(mContext), mList);
        val messageEntity = mList[position]
        holder.tvName.text = messageEntity.name
        val color = (Math.random() * Constants.COLORS.size).toInt()
        holder.headView.setImageResource(Constants.COLORS[color])
        val length = messageEntity.name.length
        val headName: String
        if (length in 0..2) {
            headName = messageEntity.name
        } else {
            headName = messageEntity.name.substring(messageEntity.name.length - 2)
        }
        holder.tvHeadName.text = headName

        val time = messageEntity.time
        val showTime = TimeUtil.formatChatDate(time)
        if (TextUtils.isEmpty(showTime)) {
            holder.tvTime.text = ""
        } else {
            holder.tvTime.text = showTime
        }
        holder.tvContent.text = messageEntity.content

        if (messageEntity.msgCount > 0) {
            holder.tvTip.text = messageEntity.msgCount.toString()
            holder.tvTip.visibility = View.VISIBLE
        } else {
            holder.tvTip.visibility = View.INVISIBLE
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
        var mOnItemClickListener: OnItemClickListener? = null

        init {
            itemView.setOnClickListener(this)
            tvHeadName = itemView.findViewById(R.id.message_list_item_head_name) as TextView
            tvName = itemView.findViewById(R.id.message_list_item_tv_name) as TextView
            tvTime = itemView.findViewById(R.id.message_list_item_tv_time) as TextView
            tvContent = itemView.findViewById(R.id.message_list_item_tv_content) as TextView
            headView = itemView.findViewById(R.id.message_list_item_head_view) as CircleImageView
            tvTip = itemView.findViewById(R.id.message_tip) as DragIndicatorView
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
            mOnItemClickListener = onItemClickListener
        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                AnimationUtil.startScaleAnim(this@MainMessageViewHolder.itemView)
                mOnItemClickListener!!.onItemClick(adapterPosition)
            }
        }
    }
}
