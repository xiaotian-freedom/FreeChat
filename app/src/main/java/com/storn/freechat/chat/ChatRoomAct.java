package com.storn.freechat.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.common.util.DensityUtil;
import com.common.util.TimeUtil;
import com.jaeger.library.StatusBarUtil;
import com.storn.freechat.R;
import com.storn.freechat.adapter.ChatMessageAdapter;
import com.storn.freechat.common.ChatApplication;
import com.storn.freechat.common.DBHelper;
import com.storn.freechat.manager.XMPPConnectionManager;
import com.storn.freechat.vo.ChatMessageEntityVo;
import com.storn.freechat.vo.FriendsEntityVo;
import com.storn.freechat.vo.MessageEntityVo;
import com.storn.freechat.vo.UserVo;
import com.storn.freechat.widget.KeySoftListView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聊天室
 * Created by tianshutong on 2016/12/9.
 */

public class ChatRoomAct extends AppCompatActivity implements TextWatcher,
        View.OnLayoutChangeListener, View.OnClickListener, ChatManagerListener {

    @BindView(R.id.main_tool_bar)
    Toolbar mainToolBar;
    @BindView(R.id.refreshableView)
    KeySoftListView refreshableView;
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
    private ChatMessageAdapter chatMessageAdapter;
    private int keyHeight = 0;
    private int titleHeight = 0;
    private int oldChatBottomTop = 0;
    private static final int CLEAR_EDIT_TEXT = 1;
    private static final int ADD_CHAT_MESSAGE = 2;
    private ChatMessageHandler chatMessageHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_y_e2), 0);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        initData();
        initListener();
    }

    private void initToolbar() {
        mainToolBarTitle = (TextView) mainToolBar.findViewById(R.id.main_tool_bar_title);
        mainToolBarRight = (ImageView) mainToolBar.findViewById(R.id.main_tool_bar_right);
        mainToolBar.setNavigationIcon(R.mipmap.white_arrow_left);
    }

    private void initView() {
        refreshableView.bindActivity(this);
        keyHeight = DensityUtil.getScreenHeight(this) / 3;
    }

    private void initData() {
        chatMessageHandler = new ChatMessageHandler(getMainLooper(), this);
        chatMessageHandler.postDelayed(() -> {
            titleHeight = mainToolBar.getHeight();
            oldChatBottomTop = chatBottomLayout.getTop();
        }, 1000);

        Intent data = this.getIntent();
        String jid = data.getStringExtra(Constants.FRIEND_JID);
        String name = data.getStringExtra(Constants.FRIEND_NAME);
        friendInfo = new FriendsEntityVo();
        friendInfo.jid = jid;
        friendInfo.name = name;
        setToolbarTitle(name);
        UserVo mUserVo = ChatApplication.getUserVo();
        queryChatMessage(jid, mUserVo.jid);
    }

    private void initListener() {
        mainToolBar.setNavigationOnClickListener(view -> finish());
        mainToolBarRight.setOnClickListener(view -> {

        });
        chatRoomSend.setOnClickListener(this);
        chatRoomEdit.addTextChangedListener(this);
        chatRoomRootLayout.addOnLayoutChangeListener(this);
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

    /**
     * 查询聊天信息
     *
     * @param fromJid
     * @param myJid
     */
    private void queryChatMessage(String fromJid, String myJid) {
        List<ChatMessageEntityVo> oldMessageList = DBHelper.getInstance()
                .queryChatMessageByJid(this, myJid, fromJid);
        setChatAdapter(oldMessageList);
    }

    private void setChatAdapter(List<ChatMessageEntityVo> messageList) {
        if (chatMessageAdapter == null) {
            chatMessageAdapter = new ChatMessageAdapter(this, messageList);
            refreshableView.setAdapter(chatMessageAdapter);
        } else {
            chatMessageAdapter.addChatList(messageList);
        }
        refreshableView.setSelection(chatMessageAdapter.getCount());
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    private void sendMessage(String msg) {
        XMPPConnectionManager manager = XMPPConnectionManager.getInstance();
        XMPPTCPConnection connection = manager.getConnection();
        new Thread(() -> {
            UserVo mUserVo = ChatApplication.getUserVo();
            if (mUserVo == null) {
                throw new RuntimeException("用户信息未保存，请重新登录");
            }
            if (TextUtils.isEmpty(friendInfo.jid)) {
                return;
            }
            if (!manager.isLogin()) {
                manager.login(mUserVo.name, mUserVo.password);
            }
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(this);
            Chat newChat = chatManager.createChat(friendInfo.jid);

            try {
                newChat.sendMessage(msg);
                //save to chat list
                long currentTime = TimeUtil.getCurrentTime();
                ChatMessageEntityVo chatMessageEntity = new ChatMessageEntityVo();
                chatMessageEntity.fromJid = friendInfo.jid;
                chatMessageEntity.myJid = mUserVo.jid;
                chatMessageEntity.type = Constants.CHAT_MESSAGE_TYPE_TO;
                chatMessageEntity.content = msg;
                chatMessageEntity.time = currentTime;
                DBHelper dbHelper = DBHelper.getInstance();
                dbHelper.insertOrUpdateChatMessage(this, chatMessageEntity);
                //save to message list
                MessageEntityVo messageEntity = new MessageEntityVo();
                messageEntity.fromJid = friendInfo.jid;
                messageEntity.myJid = mUserVo.jid;
                messageEntity.name = friendInfo.name;
                messageEntity.content = msg;
                messageEntity.time = currentTime;
                dbHelper.insertOrUpdateMessage(this, messageEntity);
                android.os.Message addMessage = new android.os.Message();
                addMessage.what = ADD_CHAT_MESSAGE;
                addMessage.obj = chatMessageEntity;
                chatMessageHandler.sendMessage(addMessage);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        chat.addMessageListener((Chat userChat, Message message) -> {
            UserVo mUserVo = ChatApplication.getUserVo();
            if (message.getBody() == null) {
                return;
            }
            long currentTime = TimeUtil.getCurrentTime();
            ChatMessageEntityVo chatMessageEntity = new ChatMessageEntityVo();
            chatMessageEntity.fromJid = friendInfo.jid;
            chatMessageEntity.myJid = mUserVo.jid;
            chatMessageEntity.type = Constants.CHAT_MESSAGE_TYPE_FROM;
            chatMessageEntity.content = message.getBody();
            chatMessageEntity.time = currentTime;
            DBHelper dbHelper = DBHelper.getInstance();
            dbHelper.insertOrUpdateChatMessage(this, chatMessageEntity);
            MessageEntityVo messageEntity = new MessageEntityVo();
            messageEntity.fromJid = friendInfo.jid;
            messageEntity.myJid = mUserVo.jid;
            messageEntity.name = friendInfo.name;
            messageEntity.content = message.getBody();
            messageEntity.time = currentTime;
            dbHelper.insertOrUpdateMessage(this, messageEntity);
            android.os.Message addMessage = new android.os.Message();
            addMessage.what = ADD_CHAT_MESSAGE;
            addMessage.obj = chatMessageEntity;
            chatMessageHandler.sendMessage(addMessage);

        });
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
//            Rect frame = new Rect();
//            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//            int statusHeight = frame.top;
//            int inputNewTop = chatBottomLayout.getTop() + statusHeight;
//            final int diffY = oldChatBottomTop - inputNewTop + titleHeight;

//            refreshableView.smoothScrollByOffset(keyHeight);

//            chatMessageHandler.postDelayed(() -> refreshableView.scrollBy(0, diffY), 100);
        }
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
                case CLEAR_EDIT_TEXT:
                    theActivity.chatRoomEdit.setText("");
                    break;
                case ADD_CHAT_MESSAGE:
                    ChatMessageEntityVo chatMessageEntity = (ChatMessageEntityVo) msg.obj;
                    theActivity.chatMessageAdapter.addChatData(chatMessageEntity);
                    theActivity.refreshableView.setSelection(theActivity.chatMessageAdapter.getCount());
                    theActivity.chatRoomEdit.setText("");
                    break;
                default:
                    break;
            }
        }
    }

}
