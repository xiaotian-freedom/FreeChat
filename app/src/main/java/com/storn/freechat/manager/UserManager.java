package com.storn.freechat.manager;

import com.common.common.Constants;

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
    public VCard getUserVCard(String jid) {
        VCardManager vCardManager = VCardManager.getInstanceFor(XMPPConnectionManager.getInstance().getConnection());
        try {
            return vCardManager.loadVCard(jid);
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
        VCard vCard = getUserVCard(jid);
        if (vCard == null || vCard.getAvatar() == null) {
            return null;
        }
        return new ByteArrayInputStream(vCard.getAvatar());
    }

    /**
     * 获取用户名
     *
     * @param jid
     * @return
     */
    public String getUserName(String jid) {
        VCard vCard = getUserVCard(jid);
        if (vCard == null || vCard.getFirstName() == null) {
            return null;
        }
        return vCard.getFirstName();
    }

    /**
     * 获取用户email
     *
     * @param jid
     * @return
     */
    public String getUserEmail(String jid) {
        VCard vCard = getUserVCard(jid);
        if (vCard == null || vCard.getEmailWork() == null) {
            return null;
        }
        return vCard.getEmailWork();
    }

    /**
     * 获取用户电话
     *
     * @param jid
     * @return
     */
    public String getUserPhone(String jid) {
        VCard vCard = getUserVCard(jid);
        if (vCard == null || vCard.getPhoneWork(Constants.PHONE_TYPE_5) == null) {
            return null;
        }
        return vCard.getPhoneWork(Constants.PHONE_TYPE_5);
    }

}
