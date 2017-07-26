package com.storn.freechat.manager;

import android.text.TextUtils;

import com.common.common.Constants;
import com.common.util.PreferenceTool;
import com.storn.freechat.common.ChatApplication;
import com.storn.freechat.jni.FreeChatCommon;
import com.storn.freechat.login.presenter.LoginContract;
import com.storn.freechat.vo.UserVo;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.io.IOException;
import java.util.List;

/**
 * XMPP服务器连接工具类
 * Created by tianshutong on 2016/12/9.
 */

public class XMPPConnectionManager {

    private XMPPTCPConnection connection;
    private static XMPPConnectionManager connectionManager;
    private LoginContract.ILoginListener mLoginListener;

    public static XMPPConnectionManager getInstance() {
        if (connectionManager == null) {
            synchronized (XMPPConnectionManager.class) {
                if (connectionManager == null) {
                    connectionManager = new XMPPConnectionManager();
                }
            }
        }
        return connectionManager;
    }

    /**
     * 初始化连接并登录
     *
     * @param userName
     * @param passWord
     */
    public void initConnection(String userName, String passWord) {
        XMPPTCPConnectionConfiguration.Builder configuration = XMPPTCPConnectionConfiguration.builder();
        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configuration.setDebuggerEnabled(true);
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
            configuration.setUsernameAndPassword(userName, passWord);
        }
        configuration.setHost(FreeChatCommon.getXMPPHost());
        configuration.setServiceName(FreeChatCommon.getXMPPServerName());
        configuration.setPort(FreeChatCommon.getXMPPPort());
        configuration.setSendPresence(true);

//        configuration.setResource(Constants.RESOURCE);
//        xmpptcpConnectionConfiguration.setCompressionEnabled(false);
//        SASLMechanism mechanism = new SASLDigestMD5Mechanism();
//        SASLAuthentication.registerSASLMechanism(mechanism);
//        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
//        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");
//        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        connection = new XMPPTCPConnection(configuration.build());
        connection.setPacketReplyTimeout(Constants.RESPONSE_TIME_OUT);
    }

    /**
     * 获取连接对象
     *
     * @return
     */
    public XMPPTCPConnection getConnection() {
        if (connection == null) {
            initConnection(PreferenceTool.getString(Constants.LOGIN_UNAME),
                    PreferenceTool.getString(Constants.LOGIN_UPASS));
        }
        return connection;
    }

    /**
     * 移除连接对象
     */
    public void removeConnection() {
        if (connection != null) {
            connection = null;
        }
    }

    /**
     * 登录服务器
     *
     * @param userName
     * @param passWord
     */
    public void login(String userName, String passWord) {
        if (mLoginListener != null) {
            mLoginListener.start();
        }
        if (connection == null) {
            getConnection();
        }
        new Thread(() -> {
            try {
                if (getConnection() != null) {
                    if (!connection.isConnected()) {
                        connection.connect();
                    }
                    if (!isLogin()) {
                        connection.login(userName, passWord);
                    }
                    if (connection.isAuthenticated()) {
                        UserVo userVo = new UserVo();
                        userVo.name = PreferenceTool.getString(Constants.LOGIN_UNAME);
                        try {
                            VCardManager vCardManager = VCardManager.getInstanceFor(connection);

                            VCard vCard = vCardManager.loadVCard();
                            String jid = vCard.getJabberId();
                            if (TextUtils.isEmpty(jid)) {
                                jid = vCard.getTo();
                            }
                            userVo.jid = jid;
                            userVo.nickName = vCard.getNickName();
                            PreferenceTool.putString(Constants.LOGIN_JID, jid);
                            PreferenceTool.commit();
                        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                                | SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        ChatApplication.setUserVo(userVo);
                        if (mLoginListener != null) {
                            mLoginListener.success();
                        }
                    }
                }
            } catch (IOException | XMPPException | SmackException e) {
                boolean error = e.getMessage().equals("XMPPError: conflict - cancel");
                if (error) {
                    // 關閉連接
                    XMPPConnectionManager.getInstance().connection.disconnect();
                    // 重连服务器
                    login();
                } else {
                    if (mLoginListener != null) {
                        mLoginListener.fail(e.getMessage());
                    }
                }

            }
        }).start();
    }

    public void login() {
        login(PreferenceTool.getString(Constants.LOGIN_UNAME),
                PreferenceTool.getString(Constants.LOGIN_UPASS));
    }

    public void login(LoginContract.ILoginListener loginListener) {
        mLoginListener = loginListener;
        login(PreferenceTool.getString(Constants.LOGIN_UNAME),
                PreferenceTool.getString(Constants.LOGIN_UPASS));
    }

    public void register(String user, String pwd, LoginContract.ILoginListener loginListener) {
        mLoginListener = loginListener;
        register(user, pwd);
    }

    /**
     * 注册账号
     *
     * @param user
     * @param pwd
     */
    private void register(String user, String pwd) {
        if (mLoginListener != null) {
            mLoginListener.start();
        }
        if (connection == null) {
            getConnection();
        }
        new Thread(() -> {
            try {
                if (connection != null) {
                    if (!connection.isConnected()) {
                        connection.connect();
                    }
                    if (connection.isConnected()) {
                        String serviceName = connection.getServiceName();
                        try {
                            AccountManager accountManager = AccountManager.getInstance(connection);
                            if (accountManager.supportsAccountCreation()) {
                                accountManager.createAccount(user + "\\40" + serviceName, pwd);
//
//                                Thread.sleep(500);
//
//                                VCard vCard = UserManager.getInstance().getUserVCard(user + "@" + serviceName);
//
//                                if (!TextUtils.isEmpty(nick)) {
//                                    vCard.setNickName(nick);
//                                }
//                                if (!TextUtils.isEmpty(telephone)) {
//                                    vCard.setPhoneWork(Constants.PHONE_TYPE_5, telephone);
//                                }
//                                if (!TextUtils.isEmpty(email)) {
//                                    vCard.setEmailWork(email);
//                                }
//                                UserManager.getInstance().saveUserVCard(vCard);
                                if (mLoginListener != null) {
                                    mLoginListener.success();
                                }
                            } else {
                                if (mLoginListener != null) {
                                    mLoginListener.fail("");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mLoginListener != null) {
                                mLoginListener.fail(e.getMessage());
                            }
                        }
                    } else {
                        if (mLoginListener != null) {
                            mLoginListener.fail("");
                        }
                    }
                } else {
                    if (mLoginListener != null) {
                        mLoginListener.fail("");
                    }
                }
            } catch (IOException | XMPPException | SmackException e) {
                e.printStackTrace();
                if (mLoginListener != null) {
                    mLoginListener.fail(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 是否已连接
     *
     * @return
     */

    public boolean isConnected() {
        if (connection != null) {
            return connection.isConnected();
        }
        return false;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * 是否已登录
     *
     * @return
     */
    public boolean isLogin() {
        return getConnection().isAuthenticated();
    }

    /**
     * 配置创建的聊天室信息
     *
     * @param mUserChat
     */
    public boolean configChatRoom(MultiUserChat mUserChat) {
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
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);
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
}
