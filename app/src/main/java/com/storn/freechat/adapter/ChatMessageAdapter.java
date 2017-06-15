package com.storn.freechat.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.common.Constants;
import com.common.util.AnimationUtil;
import com.common.util.DensityUtil;
import com.common.util.TimeUtil;
import com.common.widget.CopyPopWindow;
import com.common.widget.TopAutoRefreshListView;
import com.storn.freechat.R;
import com.storn.freechat.util.DBHelper;
import com.storn.freechat.vo.ChatMessageEntityVo;

import java.util.List;

import am.widget.shapeimageview.ShapeImageView;

/**
 * 聊天消息适配器
 * Created by tianshutong on 2017/1/9.
 */

public class ChatMessageAdapter extends BaseAdapter {

    private Context mContext;
    private int mLocationX;
    private TopAutoRefreshListView mListView;
    private List<ChatMessageEntityVo> mChatList;

    public ChatMessageAdapter(Context mContext, List<ChatMessageEntityVo> mChatList,
                              TopAutoRefreshListView listView) {
        this.mContext = mContext;
        this.mChatList = mChatList;
        this.mListView = listView;
    }

    /**
     * 加入单条数据
     *
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
     *
     * @param mChatList
     */
    public void addChatList(List<ChatMessageEntityVo> mChatList) {
        if (mChatList != null && mChatList.size() > 0) {
            this.mChatList.addAll(0, mChatList);
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
                    convertView.setTag(fromMessageHolder);
                }
                break;
                case Constants.CHAT_MESSAGE_TYPE_TO: {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_room_right_layout, viewGroup, false);
                    toMessageHolder = new ToMessageHolder(convertView);
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
                }
                break;
                case Constants.CHAT_MESSAGE_TYPE_TO: {
                    toMessageHolder = (ToMessageHolder) convertView.getTag();
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
                    fillFromMessage(fromMessageHolder, chatMessageEntity, position, chatMessageEntity.cId);
                }
                break;

                case Constants.CHAT_MESSAGE_TYPE_TO: {
                    fillToMessage(toMessageHolder, chatMessageEntity, position, chatMessageEntity.cId);
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
                        fillFromMessage(fromMessageHolder, chatMessageEntity, position, chatMessageEntity.cId);
                    }
                    break;
                    case Constants.CHAT_MESSAGE_TYPE_TO: {
                        toMessageHolder.dateLayout.setVisibility(View.VISIBLE);
                        fillToMessage(toMessageHolder, chatMessageEntity, position, chatMessageEntity.cId);
                    }
                    break;
                }
            } else {
                switch (type) {
                    case Constants.CHAT_MESSAGE_TYPE_FROM: {
                        fromMessageHolder.dateLayout.setVisibility(View.GONE);
                        fillFromMessage(fromMessageHolder, chatMessageEntity, position, chatMessageEntity.cId);
                    }
                    break;
                    case Constants.CHAT_MESSAGE_TYPE_TO: {
                        toMessageHolder.dateLayout.setVisibility(View.GONE);
                        fillToMessage(toMessageHolder, chatMessageEntity, position, chatMessageEntity.cId);
                    }
                    break;
                }
            }
        }

        return convertView;
    }

    private void fillFromMessage(FromMessageHolder fromMessageHolder,
                                 ChatMessageEntityVo messageEntityVo, int position, int cId) {
        long currentTime = messageEntityVo.time;
        String showTime = TimeUtil.formatChatDate(currentTime);
        if (TextUtils.isEmpty(showTime)) {
            fromMessageHolder.tvDate.setText("");
        } else {
            fromMessageHolder.tvDate.setText(showTime);
        }
        String content = messageEntityVo.content;
        fromMessageHolder.tvContent.setText(content);

        fromMessageHolder.contentLayout.setOnTouchListener(onTouchListener);
        fromMessageHolder.contentLayout.setOnLongClickListener((View v) -> {
            showCopyPop(v, 0, content, position, cId, fromMessageHolder.rootLayout);
            return false;
        });
    }

    private void fillToMessage(ToMessageHolder toMessageHolder,
                               ChatMessageEntityVo messageEntityVo, int position, int cId) {
        long currentTime = messageEntityVo.time;
        String showTime = TimeUtil.formatChatDate(currentTime);
        if (TextUtils.isEmpty(showTime)) {
            toMessageHolder.tvDate.setText("");
        } else {
            toMessageHolder.tvDate.setText(showTime);
        }
        String content = messageEntityVo.content;
        toMessageHolder.tvContent.setText(content);

        toMessageHolder.contentLayout.setOnTouchListener(onTouchListener);
        toMessageHolder.contentLayout.setOnLongClickListener((View v) -> {
            showCopyPop(v, 1, content, position, cId, toMessageHolder.rootLayout);
            return false;
        });
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mLocationX = (int) event.getX();
            }
            return false;
        }
    };

    private void showCopyPop(View v, int type, String content, int position, int cId, View rootView) {
        CopyPopWindow.Builder builder = new CopyPopWindow.Builder(mContext);
        builder.setOnWhichClickListener((View view, int pos) -> {
            switch (pos) {
                case 0:
                    copy(mContext, content);
                    break;
                case 1:
                    Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();

                    break;
                case 2:
                    deleteItem(rootView, position, cId);
                    break;
            }
        });
        builder.setType(type);
        int[] location = new int[2];
        v.getLocationInWindow(location);
        CopyPopWindow copyPopWindow = builder.create();
        if (!copyPopWindow.isShowing()) {
            location[0] = v.getLeft() + mLocationX - DensityUtil.dip2px(mContext, 200) / 2;
            copyPopWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - v.getMeasuredHeight());
        }
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

    /**
     * 复制文本
     *
     * @param context
     * @param content
     */
    private void copy(Context context, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy", content);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 删除单挑聊天记录
     *
     * @param view
     * @param position
     * @param cId
     */
    private void deleteItem(View view, int position, int cId) {
        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                DBHelper.getInstance().deleteChatMessage(mContext, cId);
                mChatList.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        AnimationUtil.collapse(view, listener);
    }

    private static class FromMessageHolder {

        private TextView tvDate;
        private TextView tvContent;
        private LinearLayout dateLayout;
        private LinearLayout contentLayout;
        private LinearLayout rootLayout;
        private ShapeImageView shapeImageView;

        private FromMessageHolder(View itemView) {
            tvDate = (TextView) itemView.findViewById(R.id.chat_room_tv_date);
            tvContent = (TextView) itemView.findViewById(R.id.chat_room_tv_content);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_time_layout);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_content_layout);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_root_layout);
            shapeImageView = (ShapeImageView) itemView.findViewById(R.id.chat_room_shape_view);
        }
    }

    private static class ToMessageHolder {

        private TextView tvDate;
        private TextView tvContent;
        private LinearLayout dateLayout;
        private LinearLayout contentLayout;
        private LinearLayout rootLayout;
        private ShapeImageView shapeImageView;

        private ToMessageHolder(View itemView) {
            tvDate = (TextView) itemView.findViewById(R.id.chat_room_tv_date);
            tvContent = (TextView) itemView.findViewById(R.id.chat_room_tv_content);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_time_layout);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_content_layout);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.chat_room_root_layout);
            shapeImageView = (ShapeImageView) itemView.findViewById(R.id.chat_room_shape_view);
        }
    }
}
