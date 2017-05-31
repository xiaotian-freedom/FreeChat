package com.storn.freechat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.common.Constants;
import com.common.util.AnimationUtil;
import com.common.util.DensityUtil;
import com.common.util.PreferenceTool;
import com.common.util.ToastUtil;
import com.common.widget.PinnedHeaderExpandableListView;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.storn.freechat.R;
import com.storn.freechat.adapter.MainFriendsAdapter;
import com.storn.freechat.adapter.MainGroupAdapter;
import com.storn.freechat.adapter.MainMessageAdapter;
import com.storn.freechat.chat.ChatRoomAct;
import com.storn.freechat.common.ChatApplication;
import com.storn.freechat.common.DBHelper;
import com.storn.freechat.interfac.OnItemClickListener;
import com.storn.freechat.manager.XMPPConnectionManager;
import com.storn.freechat.vo.FriendsEntityVo;
import com.storn.freechat.vo.FriendsGroupVo;
import com.storn.freechat.vo.GroupEntityVo;
import com.storn.freechat.vo.MessageEntityVo;
import com.storn.freechat.vo.UserVo;
import com.yalantis.phoenix.PullToRefreshView;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tianshutong on 2017/4/1.
 */

public class MainContentFragment extends Fragment {

    @BindView(R.id.main_recycler_view)
    SwipeMenuRecyclerView mainMessageRecyclerView;
    @BindView(R.id.swipe_recycler_refresh_layout)
    PullToRefreshView swipeMessageRefreshLayout;
    @BindView(R.id.main_expandable_list_view)
    PinnedHeaderExpandableListView mainExpandableListView;
    @BindView(R.id.swipe_expandable_refresh_layout)
    PullToRefreshView swipeExpandableRefreshLayout;

    private boolean isFriendsRefreshing;

    private View rootView;

    public static ContentHandler contentHandler;
    private MainMessageAdapter messageAdapter;
    private MainFriendsAdapter friendsAdapter;
    private MainGroupAdapter groupAdapter;

    //消息列表
    private List<MessageEntityVo> mMessageList = new ArrayList<>();
    //群列表
    private List<GroupEntityVo> mGroupList = new ArrayList<>();
    //好友列表
    private ArrayList<FriendsGroupVo> groupList = new ArrayList<>();
    private ArrayList<List<FriendsEntityVo>> childList = new ArrayList<>();

    public MainContentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_content_panel, container, false);
            ButterKnife.bind(this, rootView);
            contentHandler = new ContentHandler(Looper.getMainLooper(), this);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initConn();
        initListener();
    }

    /**
     * 初始化连接
     */
    private void initConn() {
        XMPPConnectionManager.getInstance().login();
        if (XMPPConnectionManager.getInstance().isLogin()
                && XMPPConnectionManager.getInstance().isConnected()) {
            initUserVo();
            initMessageRecyclerView();
            initExpandableView();
        }
    }

    private void initListener() {
        mainExpandableListView.setOnChildClickListener(mOnChildClickListener);
        mainExpandableListView.setOnGroupClickListener(mOnGroupClickListener);
        mainExpandableListView.setOnHeaderUpdateListener(mOnHeaderUpdateListener);
        swipeMessageRefreshLayout.setOnRefreshListener(mMessageRefreshListener);
        swipeExpandableRefreshLayout.setOnRefreshListener(mExpandableListener);
        mainMessageRecyclerView.setSwipeMenuItemClickListener(mOnSwipeMenuItemClickListener);
    }

    /**
     * 初始化消息列表
     */
    private void initMessageRecyclerView() {
        mainMessageRecyclerView.setHasFixedSize(true);
        mainMessageRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mainMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mainMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        queryMessageList();
    }

    /**
     * 初始化好友列表
     */
    private void initExpandableView() {
        getFriendsData();
        setFriendsAdapter();
    }

    /**
     * 查询聊天消息记录
     */
    private void queryMessageList() {
        if (ChatApplication.getUserVo() == null) initUserVo();
        mMessageList = DBHelper.getInstance().queryMessageByJid(getActivity(),
                ChatApplication.getUserVo().jid);
        setMessageAdapter();
    }

    /**
     * 初始化UserVo
     */
    private void initUserVo() {
        if (ChatApplication.getUserVo() == null) {
            UserVo userVo = new UserVo();
            userVo.name = PreferenceTool.getString(Constants.LOGIN_UNAME);
            try {
                VCardManager vCardManager = VCardManager
                        .getInstanceFor(XMPPConnectionManager.getInstance().getConnection());
                VCard vCard = vCardManager.loadVCard();
                String jid = vCard.getJabberId();
                if (TextUtils.isEmpty(jid)) {
                    jid = vCard.getTo();
                }
                userVo.jid = jid;
            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                    | SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
            ChatApplication.setUserVo(userVo);
        }
    }

    /**
     * 获取已加入的聊天室
     */
    private void getChatRooms() {
        XMPPTCPConnection connection = XMPPConnectionManager.getInstance().getConnection();
        MultiUserChatManager mMultiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        UserVo mUserVo = ChatApplication.getUserVo();
        if (mUserVo == null) {
            throw new RuntimeException("用户信息未保存，请重新登录");
        }
        try {
            if (TextUtils.isEmpty(mUserVo.jid)) {
                return;
            }
            List<String> tempRoomList = mMultiUserChatManager.getJoinedRooms(mUserVo.jid);
            for (String roomId : tempRoomList) {
                RoomInfo roomInfo = mMultiUserChatManager.getRoomInfo(roomId);
                GroupEntityVo groupVo = new GroupEntityVo();
                groupVo.roomJid = roomInfo.getRoom();
                groupVo.roomName = roomInfo.getName();
                mGroupList.add(groupVo);
            }
            setGroupAdapter(mGroupList);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置消息适配器
     */
    private void setMessageAdapter() {
        if (messageAdapter == null) {
            messageAdapter = new MainMessageAdapter(getActivity(), mMessageList);
            messageAdapter.setOnItemClickListener(mOnMessageItemClickListener);
            mainMessageRecyclerView.setAdapter(messageAdapter);
        } else {
            messageAdapter.setRefreshData(mMessageList);
        }
    }

    /**
     * 设置群适配器
     */
    private void setGroupAdapter(List<GroupEntityVo> groupList) {
        if (groupAdapter == null) {
            groupAdapter = new MainGroupAdapter(getActivity(), groupList);
            groupAdapter.setOnItemClickListener(mOnGroupItemClickListener);
            mainMessageRecyclerView.setAdapter(groupAdapter);
        } else {
            groupAdapter.setRefreshData(groupList);
        }
    }

    /**
     * 设置好友适配器
     */
    private void setFriendsAdapter() {
        if (friendsAdapter == null) {
            friendsAdapter = new MainFriendsAdapter(getActivity(), groupList, childList);
            mainExpandableListView.setAdapter(friendsAdapter);
        } else {
            friendsAdapter.refreshData(groupList, childList);
        }
    }

    /**
     * 显示message recyclerview的刷新控件
     */
    private void showMessageRecyclerRefreshLayout() {
        if (swipeMessageRefreshLayout.getVisibility() == View.GONE) {
            mainExpandableListView.setVisibility(View.GONE);
            swipeMessageRefreshLayout.setRefreshStyle(PullToRefreshView.STYLE_SUN);
            AnimationUtil.startAlphaAnim(swipeMessageRefreshLayout, swipeExpandableRefreshLayout);
        }
    }

    /**
     * 显示expandable的刷新控件
     */
    private void showExpandableRefreshLayout() {
        if (mainExpandableListView.getVisibility() == View.GONE) {
            mainExpandableListView.setVisibility(View.VISIBLE);
            swipeExpandableRefreshLayout.setRefreshStyle(PullToRefreshView.STYLE_SUN);
            AnimationUtil.startAlphaAnim(swipeExpandableRefreshLayout, swipeMessageRefreshLayout);
        }
    }

    /**
     * 获取好友列表
     */
    private void getFriendsData() {
        groupList.clear();
        childList.clear();
        XMPPTCPConnection connection = XMPPConnectionManager.getInstance().getConnection();
        Roster roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException
                    | InterruptedException e) {
                e.printStackTrace();
            }
        }
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {

            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {

            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {

            }

            @Override
            public void presenceChanged(Presence presence) {

            }
        });

        Collection<RosterGroup> rosterEntries = roster.getGroups();

        for (RosterGroup rosterGroup : rosterEntries) {
            String groupName = rosterGroup.getName();
            int count = rosterGroup.getEntryCount();
            FriendsGroupVo friendsGroupVo = new FriendsGroupVo();
            friendsGroupVo.name = groupName;
            friendsGroupVo.count = count;
            groupList.add(friendsGroupVo);

            List<RosterEntry> rosterEntryList = rosterGroup.getEntries();
            List<FriendsEntityVo> tempChildList = new ArrayList<>();
            for (int i = 0; i < rosterEntryList.size(); i++) {
                RosterEntry rosterEntry = rosterEntryList.get(i);
                FriendsEntityVo friendsEntityVo = new FriendsEntityVo();
                Presence presence = roster.getPresence(rosterEntry.getUser());
                Log.e("状态", "presence=" + presence);
                Log.e("状态", "presence status=" + presence.getStatus());
                Log.e("状态", "presence mode=" + presence.getMode());

                if (presence.isAvailable()) {
                    friendsEntityVo.presence = "[在线]";
                } else {
                    friendsEntityVo.presence = "[离线]";
                }
                friendsEntityVo.name = rosterEntry.getName();
                friendsEntityVo.jid = rosterEntry.getUser();
                tempChildList.add(friendsEntityVo);
            }
            childList.add(tempChildList);
        }
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(chatManagerListener);
        contentHandler.postDelayed(() -> {
            if (isFriendsRefreshing) {
                isFriendsRefreshing = false;
                swipeExpandableRefreshLayout.setRefreshing(false);
                ToastUtil.showToast(getActivity(), "暂无新朋友", R.id.main_frame_layout, Effects.standard);
            }
        }, Constants.DELAY_1000);
    }

    /**
     * 注册消息监听器
     */
    private ChatManagerListener chatManagerListener = (Chat chat, boolean createdLocally) ->
            chat.addMessageListener((Chat splitChat, org.jivesoftware.smack.packet.Message message) -> {
                Log.e("消息", message.getBody());
            });

    /**
     * 展开或关闭分组
     */
    private ExpandableListView.OnGroupClickListener mOnGroupClickListener =
            (ExpandableListView expandableListView, View view, int i, long l) -> false;
    /**
     * 点击进入好友聊天
     */
    private ExpandableListView.OnChildClickListener mOnChildClickListener =
            (ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) -> {
                FriendsEntityVo mainChild = childList.get(groupPosition).get(childPosition);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ChatRoomAct.class);
                intent.putExtra(Constants.FRIEND_JID, mainChild.jid);
                intent.putExtra(Constants.FRIEND_NAME, mainChild.name);
                startActivity(intent);
                return false;
            };

    /**
     * 分组头部监听
     */
    private PinnedHeaderExpandableListView.OnHeaderUpdateListener mOnHeaderUpdateListener = new PinnedHeaderExpandableListView.OnHeaderUpdateListener() {

        @Override
        public View getPinnedHeader() {
            View view = getActivity().getLayoutInflater()
                    .inflate(R.layout.main_expandable_group, mainExpandableListView, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return view;
        }

        @Override
        public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
            if (friendsAdapter == null || friendsAdapter.getGroupCount() == 0) {
                return;
            }
            FriendsGroupVo mainGroup = (FriendsGroupVo) friendsAdapter.getGroup(firstVisibleGroupPos);
            if (mainGroup == null || TextUtils.isEmpty(mainGroup.name)
                    || mainGroup.count == 0) {
                return;
            }
            TextView tvGroupName = (TextView) headerView.findViewById(R.id.main_group_name);
            TextView tvCount = (TextView) headerView.findViewById(R.id.main_group_count);
            tvGroupName.setText(mainGroup.name);
            tvCount.setText(String.valueOf(mainGroup.count));
        }
    };

    private OnItemClickListener mOnGroupItemClickListener = (int position) -> {
        Toast.makeText(getActivity(), "这是点击的第" + position + "个群", Toast.LENGTH_SHORT).show();
    };

    /**
     * 消息列表点击监听
     */
    private OnItemClickListener mOnMessageItemClickListener = (int position) -> {
        MessageEntityVo messageEntity = mMessageList.get(position);
        if (messageEntity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ChatRoomAct.class);
        intent.putExtra(Constants.FRIEND_JID, messageEntity.fromJid);
        intent.putExtra(Constants.FRIEND_NAME, messageEntity.name);
        startActivity(intent);
    };

    /**
     * 滑动菜单点击事件
     */
    private OnSwipeMenuItemClickListener mOnSwipeMenuItemClickListener =
            (Closeable closeable, int adapterPosition, int menuPosition, @SwipeMenuRecyclerView.DirectionMode int direction) -> {
                closeable.smoothCloseRightMenu();
                mMessageList.remove(adapterPosition);
                messageAdapter.notifyItemRemoved(adapterPosition);
            };

    /**
     * 滑动菜单构造器
     */
    private SwipeMenuCreator mSwipeMenuCreator =
            (SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) -> {
                int width = getResources().getDimensionPixelSize(R.dimen.item_menu_size);
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackgroundDrawable(R.drawable.item_menu_select_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setWidth(width)
                        .setHeight(height)
                        .setTop(DensityUtil.dip2px(getActivity(), 11))
                        .setRight(DensityUtil.dip2px(getActivity(), 11))
                        .setBottom(DensityUtil.dip2px(getActivity(), 11));
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            };

    /**
     * 消息列表刷新
     */
    private PullToRefreshView.OnRefreshListener mMessageRefreshListener = () ->
            contentHandler.postDelayed(() -> {
                swipeMessageRefreshLayout.setRefreshing(false);
                int menu = ChatApplication.getMenu_left();
                if (menu == Constants.MENU_LEFT_MESSAGE) {
                    UserVo mUserVo = ChatApplication.getUserVo();
                    DBHelper dbHelper = DBHelper.getInstance();
                    List<MessageEntityVo> messageEntityList = dbHelper.queryMessageByJid(getActivity(), mUserVo.jid);
                    if (messageEntityList.size() == mMessageList.size()) {
                        ToastUtil.showToast(getActivity(), "暂无新消息", R.id.main_frame_layout, Effects.standard);
                    } else {
                        messageAdapter.setRefreshData(messageEntityList);
                    }
                } else if (menu == Constants.MENU_LEFT_GROUP) {
                    ToastUtil.showToast(getActivity(), "暂无新的群消息", R.id.main_frame_layout, Effects.standard);
                }
            }, Constants.DELAY_1000);

    /**
     * 好友列表刷新
     */
    private PullToRefreshView.OnRefreshListener mExpandableListener = () -> {
        isFriendsRefreshing = true;
        getFriendsData();
    };

    public static class ContentHandler extends Handler {

        MainContentFragment contentFragment;

        private ContentHandler(Looper looper, MainContentFragment fragment) {
            super(looper);
            WeakReference<MainContentFragment> mFragment = new WeakReference<>(fragment);
            contentFragment = mFragment.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MENU_LEFT_MESSAGE:
                    contentFragment.setMessageAdapter();
                    contentFragment.showMessageRecyclerRefreshLayout();

                    break;
                case Constants.MENU_LEFT_GROUP:
                    contentFragment.getChatRooms();
                    contentFragment.showMessageRecyclerRefreshLayout();

                    break;
                case Constants.MENU_LEFT_CONTACT:
                    contentFragment.showExpandableRefreshLayout();

                    break;
                case Constants.MENU_LEFT_SERVICE:

                    break;
                case Constants.MENU_LEFT_SETTINGS:

                    break;

                default:
                    break;
            }
        }
    }
}
