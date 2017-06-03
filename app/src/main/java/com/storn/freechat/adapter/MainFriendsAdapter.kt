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

class MainFriendsAdapter(private val mContext: Context, private val groupList: ArrayList<FriendsGroupVo>?, private val childList: ArrayList<List<FriendsEntityVo>>?) : BaseExpandableListAdapter() {

    fun refreshData(groupList: ArrayList<FriendsGroupVo>?, childList: ArrayList<List<FriendsEntityVo>>?) {
        if (groupList != null && groupList.size != 0) {
            this.groupList!!.clear()
            this.groupList.addAll(groupList)
        }
        if (childList != null && childList.size != 0) {
            this.childList?.clear()
            this.childList?.addAll(childList)
        }
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int {
        if (groupList != null) {
            return groupList.size
        }
        return 0
    }

    override fun getChildrenCount(i: Int): Int {
        if (childList != null) {
            return childList[i].size
        }
        return 0
    }

    override fun getGroup(i: Int): Any {
        return if (groupList == null) 0 else groupList[i]
    }

    override fun getChild(i: Int, i1: Int): Any {
        if (childList != null) {
            return childList[i][i1]
        }
        return Any()
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
        val mainGroup = groupList!![i]
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
            AnimationUtil.runEnterAnimation(view, DensityUtil.getScreenHeight(mContext), childList)
        } else {
            childHolder = convertView.tag as ChildHolder
        }

        val mainChild = childList?.get(i)?.get(i1) as FriendsEntityVo
        childHolder.tvChildName.text = mainChild.name
        childHolder.tvChildPresence.text = mainChild.presence
        val color = (Math.random() * Constants.COLORS.size).toInt()
        childHolder.headView.setImageResource(Constants.COLORS[color])
        val length = mainChild.name.length
        val headName: String
        if (length > 0 && length <= 2) {
            headName = mainChild.name
        } else {
            headName = mainChild.name.substring(mainChild.name.length - 2)
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
