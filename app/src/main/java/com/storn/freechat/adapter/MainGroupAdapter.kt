package com.storn.freechat.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.DensityUtil
import com.storn.freechat.R
import com.storn.freechat.interfac.OnItemClickListener
import com.storn.freechat.vo.GroupEntityVo
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 会议室适配器
 * Created by tianshutong on 2016/12/15.
 */

class MainGroupAdapter(val mContext: Context, var mList: MutableList<GroupEntityVo>,
                       var isFirstAnim: Boolean) : SwipeMenuAdapter<MainGroupAdapter.MainMessageViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    fun setRefreshData(list: MutableList<GroupEntityVo>, isFirstAnim: Boolean) {
        if (list.isNotEmpty()) {
            this.mList = list
            this.isFirstAnim = isFirstAnim
            notifyDataSetChanged()
        }
    }

    fun addData(groupEntityVo: GroupEntityVo) {
        this.mList.add(0, groupEntityVo)
        notifyItemInserted(0)
    }

    override fun onCreateContentView(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(mContext).inflate(R.layout.main_group_list_item, parent, false)
    }

    override fun onCompatCreateViewHolder(realContentView: View, viewType: Int): MainMessageViewHolder {
        return MainMessageViewHolder(realContentView)
    }

    override fun onBindViewHolder(holder: MainMessageViewHolder, position: Int) {
        if (isFirstAnim) {
            AnimationUtil.runEnterAnimation(holder.itemView, DensityUtil.getScreenWidth(mContext), mList)
        }
        val groupVo = mList[position]
        val name = groupVo.roomName
        holder.tvName.text = name
        val color = (Math.random() * Constants.COLORS.size).toInt()
        holder.headView.setImageResource(Constants.COLORS[color])
        val length = name.length
        val headName: String
        if (length in 0..2) {
            headName = name
        } else {
            headName = name.substring(name.length - 2)
        }
        holder.tvHeadName.text = headName

        holder.setOnItemClickListener(mOnItemClickListener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MainMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var headView: CircleImageView
        var tvHeadName: TextView
        var tvName: TextView
        var mOnItemClickListener: OnItemClickListener? = null

        init {
            itemView.setOnClickListener(this)
            tvHeadName = itemView.findViewById(R.id.group_list_item_head_name) as TextView
            tvName = itemView.findViewById(R.id.group_list_item_tv_name) as TextView
            headView = itemView.findViewById(R.id.group_list_item_head_view) as CircleImageView
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
            mOnItemClickListener = onItemClickListener
        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(adapterPosition)
            }
        }
    }
}
