package com.storn.freechat.manager;

import com.common.common.Constants;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 用户信息管理类
 * Created by tianshutong on 2016/12/9.
 */

public class UserManager {

    private static UserManager userManager;

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (userManager == null) {
            synchronized (UserManager.class) {
                if (userManager == null) {
                    userManager = new UserManager();
                }
            }
        }
        return userManager;
    }

    /**
     * 获取用户信息管理类
     *
     * @param jid
     * @return
     */
    public VCardManager getUserVCardManager(String jid) {
        AbstractXMPPConnection connection = XMPPConnectionManager.getInstance().getConnection();
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        try {
            vCardManager.loadVCard(jid);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return vCardManager;
    }

    /**
     * 保存用户信息
     *
     * @param vCard
     * @return
     */
    public VCardManager saveUserVCard(VCard vCard) {
        AbstractXMPPConnection connection = XMPPConnectionManager.getInstance().getConnection();
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        try {
            vCardManager.saveVCard(vCard);
            return getUserVCardManager(vCard.getJabberId());
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户头像
     *
     * @param jid
     * @return
     */
    public InputStream getUserHead(String jid) {
        VCardManager vCardManager = getUserVCardManager(jid);
        try {
            VCard vCard = vCardManager.loadVCard(jid);
            if (vCard == null || vCard.getAvatar() == null) {
                return null;
            }
            return new ByteArrayInputStream(vCard.getAvatar());
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户名
     *
     * @param jid
     * @return
     */
    public String getUserName(String jid) {
        VCardManager vCardManager = getUserVCardManager(jid);
        try {
            VCard vCard = vCardManager.loadVCard(jid);
            if (vCard == null || vCard.getFirstName() == null) {
                return null;
            }
            return vCard.getFirstName();
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户email
     *
     * @param jid
     * @return
     */
    public String getUserEmail(String jid) {
        VCardManager vCardManager = getUserVCardManager(jid);
        try {
            VCard vCard = vCardManager.loadVCard(jid);
            if (vCard == null || vCard.getEmailWork() == null) {
                return null;
            }
            return vCard.getEmailWork();
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户电话
     *
     * @param jid
     * @return
     */
    public String getUserPhone(String jid) {
        VCardManager vCardManager = getUserVCardManager(jid);
        try {
            VCard vCard = vCardManager.loadVCard(jid);
            if (vCard == null || vCard.getPhoneWork(Constants.PHONE_TYPE_5) == null) {
                return null;
            }
            return vCard.getPhoneWork(Constants.PHONE_TYPE_5);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
