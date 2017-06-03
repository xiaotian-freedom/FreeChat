package com.storn.freechat.manager;

import com.common.common.Constants;
import com.common.util.PreferenceTool;
import com.storn.freechat.jni.FreeChatCommon;
import com.storn.freechat.login.presenter.LoginContract;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
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
        configuration.setUsernameAndPassword(userName, passWord);
        configuration.setServiceName(FreeChatCommon.getXMPPServerName());
        configuration.setHost(FreeChatCommon.getXMPPHost());
        configuration.setPort(FreeChatCommon.getXMPPPort());
        configuration.setResource(Constants.RESOURCE);
        configuration.setSendPresence(true);
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
            if (PreferenceTool.contains(Constants.LOGIN_UNAME)
                    && PreferenceTool.contains(Constants.LOGIN_UPASS)) {
                initConnection(PreferenceTool.getString(Constants.LOGIN_UNAME),
                        PreferenceTool.getString(Constants.LOGIN_UPASS));
            }
        }
        return connection;
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
                    connection.login(userName, passWord);
                    if (mLoginListener != null) {
                        mLoginListener.success();
                    }
                }
            } catch (IOException | XMPPException | SmackException e) {
                e.printStackTrace();
                if (mLoginListener != null) {
                    mLoginListener.fail();
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

    /**
     * 断开XMPP连接
     */
    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * 是否已连接
     *
     * @return
     */
    public boolean isConnected() {
        return getConnection().isConnected();
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
}
