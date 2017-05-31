package com.storn.freechat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.util.AnimationUtil;
import com.common.common.Constants;
import com.common.util.DensityUtil;
import com.storn.freechat.R;
import com.storn.freechat.interfac.OnItemClickListener;
import com.storn.freechat.vo.GroupEntityVo;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 群适配器
 * Created by tianshutong on 2016/12/15.
 */

public class MainGroupAdapter extends SwipeMenuAdapter<MainGroupAdapter.MainMessageViewHolder> {

    private Context mContext;
    private List<GroupEntityVo> mList;
    private OnItemClickListener mOnItemClickListener;

    public MainGroupAdapter(Context mContext, List<GroupEntityVo> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setRefreshData(List<GroupEntityVo> mList) {
        if (mList != null && mList.size() != 0) {
            this.mList.clear();
            this.mList.addAll(mList);
            notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.main_group_list_item, parent, false);
    }

    @Override
    public MainMessageViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new MainMessageViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(MainMessageViewHolder holder, int position) {
        AnimationUtil.runEnterAnimation(holder.itemView, DensityUtil.getScreenHeight(mContext), mList);
        GroupEntityVo groupVo = mList.get(position);
        String name = groupVo.roomName;
        holder.tvName.setText(name);
        int color = (int) (Math.random() * Constants.COLORS.length);
        holder.headView.setImageResource(Constants.COLORS[color]);
        int length = name.length();
        String headName;
        if (length > 0 && length <= 2) {
            headName = name;
        } else {
            headName = name.substring(name.length() - 2);
        }
        holder.tvHeadName.setText(headName);

        holder.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class MainMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView headView;
        TextView tvHeadName;
        TextView tvName;
        OnItemClickListener mOnItemClickListener;

        public MainMessageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvHeadName = (TextView) itemView.findViewById(R.id.group_list_item_head_name);
            tvName = (TextView) itemView.findViewById(R.id.group_list_item_tv_name);
            headView = (CircleImageView) itemView.findViewById(R.id.group_list_item_head_view);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
