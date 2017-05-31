package com.storn.freechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.common.util.AnimationUtil;
import com.common.common.Constants;
import com.common.util.DensityUtil;
import com.storn.freechat.R;
import com.storn.freechat.vo.FriendsEntityVo;
import com.storn.freechat.vo.FriendsGroupVo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 好友适配器
 * Created by tianshutong on 2016/12/22.
 */

public class MainFriendsAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<FriendsGroupVo> groupList;
    private ArrayList<List<FriendsEntityVo>> childList;

    public MainFriendsAdapter(Context mContext, ArrayList<FriendsGroupVo> groupList, ArrayList<List<FriendsEntityVo>> childList) {
        this.mContext = mContext;
        this.groupList = groupList;
        this.childList = childList;
    }

    public void refreshData(ArrayList<FriendsGroupVo> groupList, ArrayList<List<FriendsEntityVo>> childList) {
        if (groupList != null && groupList.size() != 0) {
            this.groupList.clear();
            this.groupList.addAll(groupList);
        }
        if (childList != null && childList.size() != 0) {
            this.childList.clear();
            this.childList.addAll(childList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childList.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return groupList == null ? 0 : groupList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupHolder groupHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.main_expandable_group, viewGroup, false);
            groupHolder = new GroupHolder(view);
            view.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) view.getTag();
        }
        FriendsGroupVo mainGroup = groupList.get(i);
        groupHolder.tvGroupName.setText(mainGroup.name);
        groupHolder.tvChildCount.setText(String.valueOf(mainGroup.count));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder childHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.main_expandable_child, viewGroup, false);
            childHolder = new ChildHolder(view);
            view.setTag(childHolder);
            AnimationUtil.runEnterAnimation(view, DensityUtil.getScreenHeight(mContext), childList);
        } else {
            childHolder = (ChildHolder) view.getTag();
        }
        FriendsEntityVo mainChild = childList.get(i).get(i1);
        childHolder.tvChildName.setText(mainChild.name);
        childHolder.tvChildPresence.setText(mainChild.presence);
        int color = (int) (Math.random() * Constants.COLORS.length);
        childHolder.headView.setImageResource(Constants.COLORS[color]);
        int length = mainChild.name.length();
        String headName;
        if (length > 0 && length <= 2) {
            headName = mainChild.name;
        } else {
            headName = mainChild.name.substring(mainChild.name.length() - 2);
        }
        childHolder.tvHeadName.setText(headName);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private static class GroupHolder {

        private TextView tvGroupName;
        private TextView tvChildCount;

        private GroupHolder(View itemView) {
            tvGroupName = (TextView) itemView.findViewById(R.id.main_group_name);
            tvChildCount = (TextView) itemView.findViewById(R.id.main_group_count);
        }
    }

    private static class ChildHolder {

        private TextView tvChildName;
        private TextView tvChildPresence;
        private CircleImageView headView;
        private TextView tvHeadName;

        private ChildHolder(View itemView) {
            tvChildName = (TextView) itemView.findViewById(R.id.main_expandable_child_name);
            tvChildPresence = (TextView) itemView.findViewById(R.id.main_expandable_child_presence);
            headView = (CircleImageView) itemView.findViewById(R.id.main_expandable_child_head_view);
            tvHeadName = (TextView) itemView.findViewById(R.id.main_expandable_child_head_name);
        }
    }
}
