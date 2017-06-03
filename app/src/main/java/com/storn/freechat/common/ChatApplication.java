package com.storn.freechat.common;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.common.common.Constants;
import com.common.util.PreferenceTool;
import com.storn.freechat.manager.XMPPConnectionManager;
import com.storn.freechat.vo.UserVo;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

/**
 * Created by tianshutong on 2016/12/7.
 */

public class ChatApplication extends Application {

    public static UserVo userVo;

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceTool.init(this);
        DBHelper.initDataBase(this, Constants.DB_NAME);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(base);
    }

    public static UserVo getUserVo() {
        if (userVo == null) {
            if (PreferenceTool.contains(Constants.LOGIN_UNAME)) {
                userVo = new UserVo();
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
        return userVo;
    }

    public static void setUserVo(UserVo userVo) {
        ChatApplication.userVo = userVo;
    }

}
