package com.storn.freechat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.DensityUtil
import com.storn.freechat.R
import com.storn.freechat.vo.FriendsEntityVo
import com.storn.freechat.vo.FriendsGroupVo
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

/**
 * 好友适配器
 * Created by tianshutong on 2016/12/22.
 */

class MainFriendsAdapter(var mContext: Context, var groupList: List<FriendsGroupVo>,
                         var childList: ArrayList<List<FriendsEntityVo>>,
                         var isFirstInGroup: Boolean, var isFirstInChild: Boolean) : BaseExpandableListAdapter() {

    fun refreshData(groupList: List<FriendsGroupVo>, childList: ArrayList<List<FriendsEntityVo>>,
                    isFirstInGroup: Boolean, isFirstInChild: Boolean) {
        if (groupList.isNotEmpty()) {
            this.groupList = groupList
        }
        if (childList.size != 0) {
            this.childList = childList
        }
        this.isFirstInGroup = isFirstInGroup
        this.isFirstInChild = isFirstInChild
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int {
        return groupList.size
    }

    override fun getChildrenCount(i: Int): Int {
        return childList[i].size
    }

    override fun getGroup(i: Int): Any {
        return groupList[i]
    }

    override fun getChild(i: Int, i1: Int): Any {
        return childList[i][i1]
    }

    override fun getGroupId(i: Int): Long {
        return i.toLong()
    }

    override fun getChildId(i: Int, i1: Int): Long {
        return i1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(i: Int, b: Boolean, view: View?, viewGroup: ViewGroup): View {
        var convertView = view
        val groupHolder: GroupHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_expandable_group, viewGroup, false)
            groupHolder = GroupHolder(convertView)
            convertView!!.tag = groupHolder
        } else {
            groupHolder = convertView.tag as GroupHolder
        }
        if (isFirstInGroup) {
            AnimationUtil.runEnterAnimation(convertView, DensityUtil.getScreenWidth(mContext), groupList)
        }
        val mainGroup = groupList[i]
        groupHolder.tvGroupName.text = mainGroup.name
        groupHolder.tvChildCount.text = mainGroup.count.toString()
        return convertView
    }

    override fun getChildView(i: Int, i1: Int, b: Boolean, view: View?, viewGroup: ViewGroup): View {
        var convertView = view
        val childHolder: ChildHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_expandable_child, viewGroup, false)
            childHolder = ChildHolder(convertView)
            convertView!!.tag = childHolder
        } else {
            childHolder = convertView.tag as ChildHolder
        }
        if (isFirstInChild) {
            AnimationUtil.runEnterAnimation(convertView, DensityUtil.getScreenHeight(mContext), childList)
        }

        val mainChild = childList[i][i1]
        childHolder.tvChildName.text = mainChild.nickName
        childHolder.tvChildPresence.text = mainChild.presence
        val color = (Math.random() * Constants.COLORS.size).toInt()
        childHolder.headView.setImageResource(Constants.COLORS[color])
        val length = mainChild.nickName.length
        val headName: String
        if (length in 0..2) {
            headName = mainChild.nickName
        } else {
            headName = mainChild.nickName.substring(mainChild.nickName.length - 2)
        }
        childHolder.tvHeadName.text = headName
        return convertView
    }

    override fun isChildSelectable(i: Int, i1: Int): Boolean {
        return true
    }

    private class GroupHolder constructor(itemView: View) {

        val tvGroupName: TextView = itemView.findViewById(R.id.main_group_name) as TextView
        val tvChildCount: TextView = itemView.findViewById(R.id.main_group_count) as TextView

    }

    private class ChildHolder constructor(itemView: View) {

        val tvChildName: TextView = itemView.findViewById(R.id.main_expandable_child_name) as TextView
        val tvChildPresence: TextView = itemView.findViewById(R.id.main_expandable_child_presence) as TextView
        val headView: CircleImageView = itemView.findViewById(R.id.main_expandable_child_head_view) as CircleImageView
        val tvHeadName: TextView = itemView.findViewById(R.id.main_expandable_child_head_name) as TextView

    }
}
