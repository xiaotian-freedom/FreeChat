package com.storn.freechat.manager;

import com.common.common.Constants;
import com.common.util.PreferenceTool;
import com.storn.freechat.jni.FreeChatCommon;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * XMPP服务器连接工具类
 * Created by tianshutong on 2016/12/9.
 */

public class XMPPConnectionManager {

    private XMPPTCPConnection connection;
    private static XMPPConnectionManager connectionManager;

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
                }
            } catch (IOException | XMPPException | SmackException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void login() {
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
}
