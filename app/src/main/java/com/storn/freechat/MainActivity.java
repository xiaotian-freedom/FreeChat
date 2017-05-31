package com.storn.freechat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.common.Constants;
import com.common.util.DensityUtil;
import com.common.util.SoftKeyBoardUtil;
import com.common.widget.ConfirmDialog;
import com.jaeger.library.StatusBarUtil;
import com.storn.freechat.base.BaseActivity;
import com.storn.freechat.common.ChatApplication;
import com.storn.freechat.common.DBHelper;
import com.storn.freechat.fragment.MainContentFragment;
import com.storn.freechat.manager.XMPPConnectionManager;
import com.storn.freechat.util.ActivityManagerUtil;
import com.storn.freechat.util.SlideMenuUtil;
import com.storn.freechat.vo.UserVo;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主界面
 * Created by tianshutong on 2016/12/7.
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.menu_left_panel)
    FrameLayout menuLeftPanel;
    @BindView(R.id.menu_right_panel)
    FrameLayout menuRightPanel;
    @BindView(R.id.main_tool_bar)
    Toolbar mainToolBar;
    @BindView(R.id.sliding_panel)
    LinearLayout slidingPanel;
    @BindView(R.id.main_transparent_view)
    TextView mainTransparentView;
    private TextView mainToolBarTitle;
    private ImageView mainToolBarRight;
    private AppCompatEditText createEditText;
    private int startX;
    private int lastX;
    private boolean isExit;
    private boolean isLeftExpanded;
    private boolean isRightExpanded;
    private boolean isEdgeExpanded;
    private boolean canSwipeOpenLeft;
    private boolean canSwipeOpenRight;
    private boolean canSwipeCloseLeft;
    private boolean canSwipeCloseRight;
    //主界面在当前的显示界面 默认消息界面
    private int mainIndex = Constants.MENU_LEFT_MESSAGE;
    private static final int touchSlop = 100;
    private static final int minWidth = 200;
    public static MainHandler mainHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_y_e2), 0);
        ButterKnife.bind(this);
        initExplode();
        DBHelper.initDataBase(this, Constants.DB_NAME);
        mainHandler = new MainHandler(getMainLooper(), this);
        initPanelWidthAndParams();
        initContentFragment();
        initToolbar();
        initSwipeMenu();
        initListener();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int left = slidingPanel.getLeft();
        int right = slidingPanel.getRight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                lastX = startX;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int diffX = moveX - lastX;

                if (!isLeftExpanded) {
                    if (startX <= touchSlop && diffX > 0 && diffX >= minWidth
                            && !isRightExpanded && canSwipeOpenLeft) {
                        isEdgeExpanded = true;
                        openMenuLeft();
                        return true;
                    }
                } else {
                    if (diffX < 0 && -diffX >= touchSlop && lastX >= left && canSwipeCloseLeft) {
                        isLeftExpanded = false;
                        closeMenuLeft();
                        return true;
                    }
                }
                if (!isRightExpanded) {
                    if (startX > (DensityUtil.getScreenWidth(this) - touchSlop)
                            && startX <= DensityUtil.getScreenWidth(this)
                            && diffX < 0 && -diffX >= minWidth
                            && !isLeftExpanded && canSwipeOpenRight) {
                        isEdgeExpanded = true;
                        openMenuRight();
                        return true;
                    }
                } else {
                    if (diffX > 0 && diffX >= touchSlop && right >= lastX && canSwipeCloseRight) {
                        isEdgeExpanded = false;
                        closeMenuRight();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int lastX = (int) event.getX();
                if (isEdgeExpanded) {
                    isEdgeExpanded = false;
                    return true;
                }
                if (isLeftExpanded) {
                    if (lastX > left) {
                        closeMenuLeft();
                        return true;
                    }
                }
                if (isRightExpanded) {
                    if (lastX < right) {
                        closeMenuRight();
                        return true;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void initExplode() {
        Explode explode = new Explode();
        explode.setDuration(Constants.ANIM_500);
        getWindow().setEnterTransition(explode);
        getWindow().setExitTransition(explode);
    }

    private void initPanelWidthAndParams() {
        FrameLayout.LayoutParams slidingPanelParameters = (FrameLayout.LayoutParams) slidingPanel.getLayoutParams();
        slidingPanelParameters.width = DensityUtil.getScreenWidth(this);
        slidingPanel.setLayoutParams(slidingPanelParameters);
    }

    private void initToolbar() {
        mainToolBarTitle = (TextView) mainToolBar.findViewById(R.id.main_tool_bar_title);
        mainToolBarRight = (ImageView) mainToolBar.findViewById(R.id.main_tool_bar_right);
        mainToolBar.setNavigationIcon(R.mipmap.slide_menu);
        setToolbarTitle("小田一郎君");
        setToolbarRight(R.mipmap.slide_user);
    }

    private void initSwipeMenu() {
        setCanSwipeOpenLeft(false);
        setCanSwipeCloseLeft(true);
        setCanSwipeOpenRight(false);
        setCanSwipeCloseRight(true);
    }

    private void initContentFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_frame_layout);
        if (fragment == null) {
            fragment = new MainContentFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.main_frame_layout, fragment);
            transaction.commit();
        }
    }

    private void initListener() {
        mainToolBar.setNavigationOnClickListener(view -> {
            if (!isLeftExpanded) {
                openMenuLeft();
            } else {
                closeMenuLeft();
            }
        });
        mainToolBarRight.setOnClickListener(view -> {
            switch (mainIndex) {
                case Constants.MENU_LEFT_MESSAGE:
                    if (!isRightExpanded) {
                        openMenuRight();
                    } else {
                        closeMenuRight();
                    }
                    break;
                case Constants.MENU_LEFT_GROUP:
                    showChatRoomDialog();
                    break;
                default:
                    break;
            }

        });
    }

    /**
     * 设置导航栏标题
     *
     * @param name
     */
    protected void setToolbarTitle(String name) {
        if (!TextUtils.isEmpty(name)) {
            mainToolBarTitle.setText(name);
        }
    }

    /**
     * 设置导航栏右侧图标
     *
     * @param resId
     */
    private void setToolbarRight(int resId) {
        if (resId != 0) {
            mainToolBarRight.setImageResource(resId);
        }
    }

    /**
     * 打开左侧菜单
     */
    private void openMenuLeft() {
        isLeftExpanded = true;
        menuLeftPanel.setVisibility(View.VISIBLE);
        menuRightPanel.setVisibility(View.GONE);
        SlideMenuUtil.openLeftMenu(this, slidingPanel, menuLeftPanel);
        cancelMainFocus();
    }

    /**
     * 关闭左侧菜单
     */
    private void closeMenuLeft() {
        isLeftExpanded = false;
        SlideMenuUtil.closeLeftMenu(this, slidingPanel, menuLeftPanel);
        setMainFocus();
    }

    /**
     * 打开右侧菜单
     */
    private void openMenuRight() {
        isRightExpanded = true;
        menuLeftPanel.setVisibility(View.GONE);
        menuRightPanel.setVisibility(View.VISIBLE);
        SlideMenuUtil.openRightMenu(this, slidingPanel, menuRightPanel);
        cancelMainFocus();
    }

    /**
     * 关闭右侧菜单
     */
    private void closeMenuRight() {
        isRightExpanded = false;
        SlideMenuUtil.closeRightMenu(this, slidingPanel, menuRightPanel);
        setMainFocus();
    }

    /**
     * 设置主界面失去焦点
     */
    protected void cancelMainFocus() {
//        if (swipeMessageRefreshLayout.getVisibility() == View.VISIBLE) {
//            swipeMessageRefreshLayout.setEnabled(false);
//            mainMessageRecyclerView.setClickable(false);
//        }
//        if (swipeExpandableRefreshLayout.getVisibility() == View.VISIBLE) {
//            swipeExpandableRefreshLayout.setEnabled(false);
//            mainExpandableListView.setClickable(false);
//        }
        mainTransparentView.setClickable(true);
        mainTransparentView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置主界面获得焦点
     */
    protected void setMainFocus() {
//        if (swipeMessageRefreshLayout.getVisibility() == View.VISIBLE) {
//            swipeMessageRefreshLayout.setEnabled(true);
//            mainMessageRecyclerView.setClickable(true);
//        }
//        if (swipeExpandableRefreshLayout.getVisibility() == View.VISIBLE) {
//            swipeExpandableRefreshLayout.setEnabled(true);
//            mainExpandableListView.setClickable(true);
//        }
        mainTransparentView.setClickable(false);
        mainTransparentView.setVisibility(View.GONE);
    }

    private void setCanSwipeOpenLeft(boolean b) {
        canSwipeOpenLeft = b;
    }

    private void setCanSwipeCloseLeft(boolean b) {
        canSwipeCloseLeft = b;
    }

    private void setCanSwipeOpenRight(boolean b) {
        canSwipeOpenRight = b;
    }

    private void setCanSwipeCloseRight(boolean b) {
        canSwipeCloseRight = b;
    }


    /**
     * 显示创建聊天室对话框
     */
    private void showChatRoomDialog() {
        View customView = LayoutInflater.from(this).inflate(R.layout.create_chat_room_layout, null);
        customView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.space_i_10)));
        createEditText = (AppCompatEditText) customView.findViewById(R.id.create_chat_room_edit);
        createEditText.addTextChangedListener(mCreateChatRoomWatcher);
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        builder.setTitle(R.string.create_room);
        builder.setCustomView(customView);
        builder.setContentPanelHeight((int) getResources().getDimension(R.dimen.content_panel_height));
        builder.setAutoDismiss(false);
        builder.setPositiveButton(getString(R.string.confirm), (DialogInterface dialogInterface, int i) -> {
            String chatRoom = createEditText.getText().toString().trim();
            if (TextUtils.isEmpty(chatRoom)) {
                Toast.makeText(this, getString(R.string.create_room_empty), Toast.LENGTH_SHORT).show();
            } else {
                if (chatRoom.length() < 2) {
                    Toast.makeText(this, getString(R.string.create_room_tip_min), Toast.LENGTH_SHORT).show();
                } else {
                    boolean isCreated = createChatRooms(chatRoom);
                    if (isCreated) {
                        Toast.makeText(this, getString(R.string.create_room_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.create_room_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), (DialogInterface dialogInterface, int i) -> {
                    SoftKeyBoardUtil.hideSoftKeyboard(MainActivity.this);
                    dialogInterface.dismiss();
                }
        );
        ConfirmDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 创建聊天室
     */
    private boolean createChatRooms(String roomName) {
        XMPPTCPConnection connection = XMPPConnectionManager.getInstance().getConnection();
        MultiUserChatManager mMultiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        UserVo mUserVo = ChatApplication.getUserVo();
        if (mUserVo == null) {
            throw new RuntimeException("用户信息未保存，请重新登录");
        }
        if (TextUtils.isEmpty(mUserVo.jid)) {
            return false;
        }
        try {
            List<String> SERVICE_NAME = mMultiUserChatManager.getServiceNames();
            MultiUserChat userChat = mMultiUserChatManager.getMultiUserChat(roomName + "@" + SERVICE_NAME.get(0));
            userChat.create(mUserVo.name);
            return configChatRoom(userChat);
        } catch (XMPPException.XMPPErrorException | SmackException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 配置创建的聊天室信息
     *
     * @param mUserChat
     */
    private boolean configChatRoom(MultiUserChat mUserChat) {
        try {
            Form form = mUserChat.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            List<FormField> fieldList = form.getFields();
            for (int i = 0; i < fieldList.size(); i++) {
                FormField field = fieldList.get(i);
                if (!FormField.Type.hidden.equals(field.getType())
                        && field.getVariable() != null) {
                    // 设置默认值作为答复
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            // 能够发现占有者真实 JID 的角色
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            // 允许使用者修改昵称
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            // 允许用户注册房间
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            // 发送已完成的表单（有默认值）到服务器来配置聊天室
            mUserChat.sendConfigurationForm(submitForm);
            return true;

        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 双击退出
     */
    private void exit2Click() {
        Timer mTimer;
        if (isExit) {
            XMPPConnectionManager manager = XMPPConnectionManager.getInstance();
            if (manager.isConnected()) {
                manager.disconnect();
            }
            ActivityManagerUtil.exitApp();
        } else {
            isExit = true;
            Toast.makeText(this, getResources().getString(R.string.click_one_more_exit), Toast.LENGTH_SHORT).show();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, Constants.DELAY_1000);
        }
    }

    /**
     * 创建群对话框输入监听
     */
    private TextWatcher mCreateChatRoomWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String chatRoom = createEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(chatRoom) && chatRoom.length() >= 10) {
                Toast.makeText(MainActivity.this, getString(R.string.create_room_tip_max), Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit2Click();
        }
        return false;
    }

    /**
     * 消息处理类
     */
    public static class MainHandler extends Handler {

        MainActivity theActivity;

        private MainHandler(Looper looper, MainActivity activity) {
            super(looper);
            WeakReference<MainActivity> mActivity = new WeakReference<>(activity);
            theActivity = mActivity.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MENU_LEFT_CLOSED:
                    theActivity.closeMenuLeft();
                    switch (msg.arg1) {
                        case Constants.MENU_LEFT_MESSAGE:
                            theActivity.mainIndex = Constants.MENU_LEFT_MESSAGE;

                            break;
                        case Constants.MENU_LEFT_GROUP:
                            theActivity.mainIndex = Constants.MENU_LEFT_GROUP;

                            break;
                        case Constants.MENU_LEFT_CONTACT:
                            theActivity.mainIndex = Constants.MENU_LEFT_CONTACT;

                            break;
                        case Constants.MENU_LEFT_SERVICE:
                            theActivity.mainIndex = Constants.MENU_LEFT_SERVICE;

                            break;
                        case Constants.MENU_LEFT_SETTINGS:
                            theActivity.mainIndex = Constants.MENU_LEFT_SETTINGS;

                            break;
                    }

                    break;
                default:
                    break;
            }
        }
    }
}
