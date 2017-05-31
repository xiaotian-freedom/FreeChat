package com.storn.freechat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.common.Constants;
import com.common.util.DensityUtil;
import com.common.util.AnimationUtil;
import com.common.util.TimeUtil;
import com.storn.freechat.R;
import com.storn.freechat.interfac.OnItemClickListener;
import com.storn.freechat.vo.MessageEntityVo;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 我的消息适配器
 * Created by tianshutong on 2016/12/15.
 */

public class MainMessageAdapter extends SwipeMenuAdapter<MainMessageAdapter.MainMessageViewHolder> {

    private Context mContext;
    private List<MessageEntityVo> mList;
    private OnItemClickListener mOnItemClickListener;

    public MainMessageAdapter(Context mContext, List<MessageEntityVo> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setRefreshData(List<MessageEntityVo> mList) {
        if (mList != null && mList.size() != 0) {
            this.mList.clear();
            this.mList.addAll(mList);
            notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.main_message_list_item, parent, false);
    }

    @Override
    public MainMessageViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new MainMessageViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(MainMessageViewHolder holder, int position) {
        AnimationUtil.runEnterAnimation(holder.itemView, DensityUtil.getScreenHeight(mContext), mList);
        MessageEntityVo messageEntity = mList.get(position);
        holder.tvName.setText(messageEntity.name);
        int color = (int) (Math.random() * Constants.COLORS.length);
        holder.headView.setImageResource(Constants.COLORS[color]);
        int length = messageEntity.name.length();
        String headName;
        if (length > 0 && length <= 2) {
            headName = messageEntity.name;
        } else {
            headName = messageEntity.name.substring(messageEntity.name.length() - 2);
        }
        holder.tvHeadName.setText(headName);

        long time = messageEntity.time;
        String showTime = TimeUtil.formatChatDate(time);
        if (TextUtils.isEmpty(showTime)) {
            holder.tvTime.setText("");
        } else {
            holder.tvTime.setText(showTime);
        }

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
        TextView tvTime;
        TextView tvContent;
        OnItemClickListener mOnItemClickListener;

        public MainMessageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvHeadName = (TextView) itemView.findViewById(R.id.message_list_item_head_name);
            tvName = (TextView) itemView.findViewById(R.id.message_list_item_tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.message_list_item_tv_time);
            tvContent = (TextView) itemView.findViewById(R.id.message_list_item_tv_content);
            headView = (CircleImageView) itemView.findViewById(R.id.message_list_item_head_view);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                AnimationUtil.startScaleAnim(MainMessageViewHolder.this.itemView);
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
