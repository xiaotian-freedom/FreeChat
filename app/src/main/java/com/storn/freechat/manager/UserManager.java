package com.storn.freechat.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.common.common.Constants;
import com.common.util.BitmapUtil;
import com.storn.freechat.R;
import com.storn.freechat.login.presenter.LoginContract;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    private VCard getUserVCard(String jid) {
        if (TextUtils.isEmpty(jid)) return null;
        if (jid.contains("/")) {
            jid = jid.split("/")[0];
        }
        if (!XMPPConnectionManager.getInstance().getConnection().isConnected()
                || !XMPPConnectionManager.getInstance().getConnection().isAuthenticated()) {
            return null;
        }
        VCardManager vCardManager = VCardManager.getInstanceFor(XMPPConnectionManager.getInstance().getConnection());
        try {
            if (vCardManager.isSupported(jid))
                return vCardManager.loadVCard(jid);
            else
                return null;
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存用户信息
     *
     * @param vCard
     */
    public void saveUserVCard(VCard vCard) {
        VCardManager vCardManager = VCardManager.getInstanceFor(XMPPConnectionManager.getInstance().getConnection());
        try {
            vCardManager.saveVCard(vCard);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户头像
     *
     * @param jid
     * @return
     */
    public InputStream getUserHead(String jid) {
        if (TextUtils.isEmpty(jid)) return null;
        VCard vCard = getUserVCard(jid);
        if (vCard == null || vCard.getAvatar() == null) {
            return null;
        }
        return new ByteArrayInputStream(vCard.getAvatar());
    }

    /**
     * 保存头像
     *
     * @param context
     * @param bitmap
     */
    public void saveAvatar(Context context, Bitmap bitmap, LoginContract.ILoginListener loginListener) {
        if (loginListener != null) {
            loginListener.start();
        }
        new Thread(() -> {
            try {
                if (XMPPConnectionManager.getInstance().getConnection() != null) {
                    if (!XMPPConnectionManager.getInstance().getConnection().isConnected()) {
                        XMPPConnectionManager.getInstance().getConnection().connect();
                    }
                    if (XMPPConnectionManager.getInstance().getConnection().isConnected()) {
                        if (XMPPConnectionManager.getInstance().getConnection().isAuthenticated()) {
                            VCardManager vCardManager = VCardManager.getInstanceFor(XMPPConnectionManager.getInstance().getConnection());
                            VCard vCard = vCardManager.loadVCard();
                            byte[] bytes = BitmapUtil.bitmapToByteArray(bitmap, true);
                            vCard.setAvatar(bytes);
                            vCardManager.saveVCard(vCard);
                            if (loginListener != null) {
                                loginListener.success();
                            }
                        } else {
                            if (loginListener != null) {
                                loginListener.fail("");
                            }
                        }
                    } else {
                        if (loginListener != null) {
                            loginListener.fail("");
                        }
                    }
                }
            } catch (IOException | XMPPException | SmackException e) {
                e.printStackTrace();
                if (loginListener != null) {
                    loginListener.fail(e.getMessage());
                }
            }
        }).start();
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

    /**
     * 修改密码
     *
     * @param context
     * @param newPass
     * @param listener
     */
    public void changePassword(Context context, String newPass, LoginContract.IChangePwdListener listener) {
        if (listener != null) {
            listener.start();
        }
        new Thread(() -> {
            try {
                if (XMPPConnectionManager.getInstance().getConnection() != null) {
                    if (!XMPPConnectionManager.getInstance().getConnection().isConnected()) {
                        XMPPConnectionManager.getInstance().getConnection().connect();
                    }
                    if (XMPPConnectionManager.getInstance().getConnection().isConnected()) {
                        if (XMPPConnectionManager.getInstance().getConnection().isAuthenticated()) {
                            AccountManager accountManager = AccountManager.getInstance(XMPPConnectionManager.getInstance().getConnection());
                            accountManager.changePassword(newPass);
                            if (listener != null) {
                                listener.success();
                            }
                        } else {
                            if (listener != null) {
                                listener.fail(context.getResources().getString(R.string.error_login));
                            }
                        }
                    } else {
                        if (listener != null) {
                            listener.fail(context.getResources().getString(R.string.error_network));
                        }
                    }
                }
            } catch (IOException | XMPPException | SmackException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.fail(e.getMessage());
                }
            }

        }).start();
    }

}
