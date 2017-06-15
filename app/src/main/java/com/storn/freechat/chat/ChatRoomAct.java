package com.storn.freechat.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.common.Constants;
import com.common.util.PreferenceTool;
import com.common.util.TimeUtil;
import com.common.widget.TopAutoRefreshListView;
import com.jaeger.library.StatusBarUtil;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.storn.freechat.R;
import com.storn.freechat.adapter.ChatMessageAdapter;
import com.storn.freechat.base.BaseActivity;
import com.storn.freechat.common.ChatApplication;
import com.storn.freechat.manager.XMPPConnectionManager;
import com.storn.freechat.util.DBHelper;
import com.storn.freechat.vo.ChatMessageEntityVo;
import com.storn.freechat.vo.FriendsEntityVo;
import com.storn.freechat.vo.MessageEntityVo;
import com.storn.freechat.vo.UserVo;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聊天室
 * Created by tianshutong on 2016/12/9.
 */

public class ChatRoomAct extends BaseActivity implements TextWatcher,
        View.OnClickListener, TopAutoRefreshListView.onTopRefreshListener {

    @BindView(R.id.main_tool_bar)
    Toolbar mainToolBar;
    @BindView(R.id.refreshableView)
    TopAutoRefreshListView refreshableView;
    @BindView(R.id.over_scroll_layout)
    TwinklingRefreshLayout scrollLayout;
    @BindView(R.id.chat_room_img_voice)
    ImageView chatRoomImgVoice;
    @BindView(R.id.chat_room_img_plus)
    ImageView chatRoomImgPlus;
    @BindView(R.id.chat_bottom_layout)
    LinearLayout chatBottomLayout;
    @BindView(R.id.chat_room_edit)
    AppCompatEditText chatRoomEdit;
    @BindView(R.id.chat_room_root_layout)
    RelativeLayout chatRoomRootLayout;
    @BindView(R.id.chat_room_send)
    Button chatRoomSend;
    private TextView mainToolBarTitle;
    private ImageView mainToolBarRight;
    private FriendsEntityVo friendInfo;
    private MessageEntityVo mMessageVo;
    private ChatMessageAdapter chatMessageAdapter;
    private int mOffset = 0;
    private int mLimit = 10;
    public static ChatMessageHandler chatMessageHandler;

    //聊天监听
    private ChatManager mChatManager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_y_e2), 0);
        ButterKnife.bind(this);
        initToolbar();
        initData();
        initListener();
    }

    private void initToolbar() {
        mainToolBarTitle = (TextView) mainToolBar.findViewById(R.id.main_tool_bar_title);
        mainToolBarRight = (ImageView) mainToolBar.findViewById(R.id.main_tool_bar_right);
        mainToolBar.setNavigationIcon(R.mipmap.white_arrow_left);
    }

    private void initData() {
        chatMessageHandler = new ChatMessageHandler(getMainLooper(), this);

        Intent data = this.getIntent();
        mMessageVo = (MessageEntityVo) data.getSerializableExtra(Constants.MESSAGEVO);
        friendInfo = new FriendsEntityVo();
        friendInfo.jid = mMessageVo.fromJid;
        friendInfo.name = mMessageVo.name;
        setToolbarTitle(mMessageVo.name);
        getChatMessage();
        clearMsgTip();
    }

    private void initListener() {

        mainToolBar.setNavigationOnClickListener(view -> finish());
        mainToolBarRight.setOnClickListener(view -> {

        });
        chatRoomSend.setOnClickListener(this);
        chatRoomEdit.addTextChangedListener(this);

        refreshableView.setAutoRefreshEnabled(true);
        refreshableView.setOnTopRefreshListener(this);
    }

    private void initChat() {
        XMPPTCPConnection connection = XMPPConnectionManager.getInstance().getConnection();
        mChatManager = ChatManager.getInstanceFor(connection);
    }

    /**
     * 设置标题名
     *
     * @param name
     */
    protected void setToolbarTitle(String name) {
        if (!TextUtils.isEmpty(name)) {
            mainToolBarTitle.setText(name);
        }
    }

    private void clearMsgTip() {
        Intent intent = new Intent();
        intent.setAction(Constants.LOCAL_ACTION);
        intent.putExtra("category", Constants.CLEAR_MESSAGE_TIP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void refreshMsg() {
        Intent intent = new Intent();
        intent.setAction(Constants.LOCAL_ACTION);
        intent.putExtra("category", Constants.REFRESH_MESSAGE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getChatMessage() {
        String myJid = PreferenceTool.getString(Constants.LOGIN_JID);
        if (TextUtils.isEmpty(myJid)) {
            myJid = ChatApplication.getUserVo().jid;
        }
        if (TextUtils.isEmpty(myJid)) return;
        queryChatMessage(friendInfo.jid, myJid, mOffset, mLimit);
    }

    /**
     * 查询聊天信息
     *
     * @param fromJid
     * @param myJid
     */
    private void queryChatMessage(String fromJid, String myJid, int offset, int limit) {
        List<ChatMessageEntityVo> oldMessageList = DBHelper.getInstance()
                .queryChatMessageByJid(this, myJid, fromJid, offset, limit);
        setChatAdapter(oldMessageList);
    }

    private void setChatAdapter(List<ChatMessageEntityVo> messageList) {
        if (chatMessageAdapter == null) {
            chatMessageAdapter = new ChatMessageAdapter(this, messageList, refreshableView);
            refreshableView.setAdapter(chatMessageAdapter);
            refreshableView.setSelection(chatMessageAdapter.getCount());
        } else {
            refreshableView.postDelayed(() -> {
                refreshableView.onRefreshComplete();
                if (messageList.size() < mLimit) {
                    refreshableView.onLoadFinish();
                }
                chatMessageAdapter.addChatList(messageList);
                refreshableView.setSelection(messageList.size() + 1);

            }, Constants.DELAY_1000);
        }
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    private void sendMessage(String msg) {
        initChat();

        new Thread(() -> {
            Chat newChat = mChatManager.createChat(friendInfo.jid);
            try {
                newChat.sendMessage(msg);
                saveAndRefreshMessage(Constants.CHAT_MESSAGE_TYPE_TO, msg);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveAndRefreshMessage(int type, String content) {
        UserVo mUserVo = ChatApplication.getUserVo();

        //save to chat list
        long currentTime = TimeUtil.getCurrentTime();
        ChatMessageEntityVo chatMessageEntity = new ChatMessageEntityVo();
        chatMessageEntity.cId = (int) currentTime;
        chatMessageEntity.fromJid = friendInfo.jid;
        chatMessageEntity.myJid = mUserVo.jid;
        chatMessageEntity.type = type;
        chatMessageEntity.content = content;
        chatMessageEntity.time = currentTime;
        DBHelper dbHelper = DBHelper.getInstance();
        dbHelper.insertOrUpdateChatMessage(this, chatMessageEntity);

        //save to message list
        mMessageVo.mId = (int) currentTime;
        mMessageVo.fromJid = friendInfo.jid;
        mMessageVo.myJid = mUserVo.jid;
        mMessageVo.name = friendInfo.name;
        mMessageVo.content = content;
        mMessageVo.time = currentTime;
        mMessageVo.msgCount = 0;
        dbHelper.insertOrUpdateMessage(this, mMessageVo);

        android.os.Message addMessage = new android.os.Message();
        addMessage.what = Constants.ADD_CHAT_MESSAGE;
        addMessage.obj = chatMessageEntity;
        chatMessageHandler.sendMessage(addMessage);

        //清除消息列表中的消息提示
        clearMsgTip();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String content = chatRoomEdit.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            chatRoomSend.setVisibility(View.GONE);
            chatRoomImgPlus.setVisibility(View.VISIBLE);
        } else {
            chatRoomSend.setVisibility(View.VISIBLE);
            chatRoomImgPlus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_room_img_voice:
                break;
            case R.id.chat_room_img_plus:
                break;
            case R.id.chat_room_send:
                String content = chatRoomEdit.getText().toString().trim();
                sendMessage(content);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        mOffset += mLimit;
        getChatMessage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearMsgTip();
    }

    /**
     * 消息处理类
     */
    public static class ChatMessageHandler extends Handler {

        ChatRoomAct theActivity;

        private ChatMessageHandler(Looper looper, ChatRoomAct activity) {
            super(looper);
            WeakReference<ChatRoomAct> mActivity = new WeakReference<>(activity);
            theActivity = mActivity.get();
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CLEAR_EDIT_TEXT:
                    theActivity.chatRoomEdit.setText("");
                    break;
                case Constants.ADD_CHAT_MESSAGE:
                    ChatMessageEntityVo chatMessageEntity = (ChatMessageEntityVo) msg.obj;
                    theActivity.chatMessageAdapter.addChatData(chatMessageEntity);
                    theActivity.refreshableView.setSelection(theActivity.chatMessageAdapter.getCount());
                    theActivity.chatRoomEdit.setText("");
                    theActivity.refreshMsg();
                    break;
                default:
                    break;
            }
        }
    }

}
