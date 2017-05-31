package com.storn.freechat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.common.Constants;
import com.common.util.TimeUtil;
import com.storn.freechat.R;
import com.storn.freechat.vo.ChatMessageEntityVo;

import java.util.List;

import am.widget.shapeimageview.ShapeImageView;

/**
 * 聊天消息适配器
 * Created by tianshutong on 2017/1/9.
 */

public class ChatMessageAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChatMessageEntityVo> mChatList;

    public ChatMessageAdapter(Context mContext, List<ChatMessageEntityVo> mChatList) {
        this.mContext = mContext;
        this.mChatList = mChatList;
    }

    /**
     * 加入单条数据
     * @param chatMessageEntity
     */
    public void addChatData(ChatMessageEntityVo chatMessageEntity) {
        if (chatMessageEntity != null) {
            mChatList.add(chatMessageEntity);
            notifyDataSetChanged();
        }
    }

    /**
     * 加载更多
     * @param mChatList
     */
    public void addChatList(List<ChatMessageEntityVo> mChatList) {
        if (mChatList != null && mChatList.size() > 0) {
            this.mChatList.addAll(mChatList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mChatList == null ? 0 : mChatList.size();
    }

    @Override
    public Object getItem(int i) {
        return mChatList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return Constants.CHAT_MESSAGE_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mChatList.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        int type = getItemViewType(position);
        FromMessageHolder fromMessageHolder = null;
        ToMessageHolder toMessageHolder = null;
        if (convertView == null) {
            switch (type) {
                case Constants.CHAT_MESSAGE_TYPE_FROM: {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_room_left_layout, viewGroup, false);
                    fromMessageHolder = new FromMessageHolder(convertView);
                    ChatMessageEntityVo chatMessageEntity = mChatList.get(position);
                    String content = chatMessageEntity.content;
                    fromMessageHolder.tvContent.setText(content);
                    convertView.setTag(fromMessageHolder);

                }
                break;
                case Constants.CHAT_MESSAGE_TYPE_TO: {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_room_right_layout, viewGroup, false);
                    toMessageHolder = new ToMessageHolder(convertView);
                    ChatMessageEntityVo chatMessageEntity = mChatList.get(position);
                    String content = chatMessageEntity.content;
                    toMessageHolder.tvContent.setText(content);
                    convertView.setTag(toMessageHolder);
                }
                break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case Constants.CHAT_MESSAGE_TYPE_FROM: {
                    fromMessageHolder = (FromMessageHolder) convertView.getTag();
                    ChatMessageEntityVo chatMessageEntity = mChatList.get(position);
                    String content = chatMessageEntity.content;
                    fromMessageHolder.tvContent.setText(content);
                }
                break;
                case Constants.CHAT_MESSAGE_TYPE_TO: {
                    toMessageHolder = (ToMessageHolder) convertView.getTag();
                    ChatMessageEntityVo chatMessageEntity = mChatList.get(position);
                    String content = chatMessageEntity.content;
                    toMessageHolder.tvContent.setText(content);
                }
                break;
                default:
                    break;
            }
        }
        ChatMessageEntityVo chatMessageEntity = mChatList.get(position);
        long currentTime = chatMessageEntity.time;
        if (position == 0) {
            switch (type) {
                case Constants.CHAT_MESSAGE_TYPE_FROM: {
                    fromMessageHolder.dateLayout.setVisibility(View.VISIBLE);
                    String showTime = TimeUtil.formatChatDate(currentTime);
                    if (TextUtils.isEmpty(showTime)) {
                        fromMessageHolder.tvDate.setText("");
                    } else {
                        fromMessageHolder.tvDate.setText(showTime);
                    }
                }
                break;

                case Constants.CHAT_MESSAGE_TYPE_TO: {
                    toMessageHolder.dateLayout.setVisibility(View.VISIBLE);
                    String showTime = TimeUtil.formatChatDate(currentTime);
                    if (TextUtils.isEmpty(showTime)) {
                        toMessageHolder.tvDate.setText("");
                    } else {
                        toMessageHolder.tvDate.setText(showTime);
                    }
                }
                break;
            }
        } else {
            ChatMessageEntityVo preChatEntity = mChatList.get(position - 1);
            long preTime = preChatEntity.time;
            if (haveTimeGap(preTime, currentTime)) {
                switch (type) {
                    case Constants.CHAT_MESSAGE_TYPE_FROM: {
                        fromMessageHolder.dateLayout.setVisibility(View.VISIBLE);
                        String showTime = TimeUtil.formatChatDate(currentTime);
                        if (TextUtils.isEmpty(showTime)) {
                            fromMessageHolder.tvDate.setText("");
                        } else {
                            fromMessageHolder.tvDate.setText(showTime);
                        }
                    }
                    break;
                    case Constants.CHAT_MESSAGE_TYPE_TO: {
                        toMessageHolder.dateLayout.setVisibility(View.VISIBLE);
                        String showTime = TimeUtil.formatChatDate(currentTime);
                        if (TextUtils.isEmpty(showTime)) {
                            toMessageHolder.tvDate.setText("");
                        } else {
                            toMessageHolder.tvDate.setText(showTime);
                        }
                    }
                    break;
                }
            } else {
                switch (type) {
                    case Constants.CHAT_MESSAGE_TYPE_FROM: {
                        fromMessageHolder.dateLayout.setVisibility(View.GONE);
                    }
                    break;
                    case Constants.CHAT_MESSAGE_TYPE_TO: {
                        toMessageHolder.dateLayout.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }
        return convertView;
    }

    /**
     * 是否显示时间
     *
     * @param lastTime
     * @param time
     * @return
     */
    private boolean haveTimeGap(long lastTime, long time) {
        int gap = 1000 * 60 * 2;
        return time - lastTime > gap;
    }

    private static class FromMessageHolder {

        private TextView tvDate;
        private TextView tvContent;
        private LinearLayout dateLayout;
        private ShapeImageView shapeImageView;

        private FromMessageHolder(View itemView) {
            tvDate = (TextView) itemView.findViewById(R.id.chat_room_tv_date);
            tvContent = (TextView) itemView.findViewById(R.id.chat_room_tv_content);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_time_layout);
            shapeImageView = (ShapeImageView) itemView.findViewById(R.id.chat_room_shape_view);
        }
    }

    private static class ToMessageHolder {

        private TextView tvDate;
        private TextView tvContent;
        private LinearLayout dateLayout;
        private ShapeImageView shapeImageView;

        private ToMessageHolder(View itemView) {
            tvDate = (TextView) itemView.findViewById(R.id.chat_room_tv_date);
            tvContent = (TextView) itemView.findViewById(R.id.chat_room_tv_content);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_time_layout);
            shapeImageView = (ShapeImageView) itemView.findViewById(R.id.chat_room_shape_view);
        }
    }
}
