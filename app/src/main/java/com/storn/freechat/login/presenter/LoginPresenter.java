package com.storn.freechat.login.presenter;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.transition.Explode;

import com.common.common.Constants;
import com.common.util.CommonUtil;
import com.common.util.PreferenceTool;
import com.jude.beam.expansion.BeamBasePresenter;
import com.storn.freechat.MainActivity;
import com.storn.freechat.RegisterActivity;
import com.storn.freechat.common.ChatApplication;
import com.storn.freechat.login.model.LoginRepository;
import com.storn.freechat.login.ui.LoginActivity;
import com.storn.freechat.manager.XMPPConnectionManager;
import com.storn.freechat.vo.UserVo;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录控制器
 * Created by tianshutong on 2017/4/7.
 */

public class LoginPresenter extends BeamBasePresenter<LoginActivity> implements LoginContract.Presenter {

    private LoginRepository loginRepository;
    private List<UserVo> mAccountList = new ArrayList<>();

    @Override
    protected void onCreateView(@NonNull LoginActivity view) {
        super.onCreateView(view);
        getAccounts();
        initData();
    }

    @Override
    protected void onCreate(@NonNull LoginActivity view, Bundle savedState) {
        super.onCreate(view, savedState);
        loginRepository = LoginRepository.getInstance();
    }

    private void initData() {
        if (mAccountList == null || mAccountList.size() == 0) {
            return;
        }
        UserVo userVo = mAccountList.get(mAccountList.size() - 1);
        if (userVo == null) {
            return;
        }
        String userPwd = CommonUtil.AESDecrypt(getView(), userVo.password);
        getView().fillLastAccount(userVo.name, userPwd);

        List<String> mTempList = new ArrayList<>();
        for (UserVo u : mAccountList) {
            String userName = u.name;
            mTempList.add(userName);
        }
        getView().addAccountsToAutoComplete(mTempList);
    }

    @Override
    public void goToMainAct() {
        Explode explode = new Explode();
        explode.setDuration(500);

        getView().getWindow().setExitTransition(explode);
        getView().getWindow().setEnterTransition(explode);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getView());
        getView().startActivity(new Intent(getView(), MainActivity.class), optionsCompat.toBundle());
        getView().finish();
    }

    @Override
    public void goToRegAct(android.view.View view, String s) {
        getView().getWindow().setExitTransition(null);
        getView().getWindow().setEnterTransition(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(getView(), view, s);
            getView().startActivity(new Intent(getView(), RegisterActivity.class), options.toBundle());
        } else {
            getView().startActivity(new Intent(getView(), RegisterActivity.class));
        }
    }

    @Override
    public void getAccounts() {
        String userInfos = PreferenceTool.getString(Constants.APP_ACCOUNT_INFO);
        if (!TextUtils.isEmpty(userInfos)) {
            if (userInfos.contains(",")) {
                String[] userArray = userInfos.split(",");
                for (String users : userArray) {
                    String[] user = users.split("/");
                    UserVo userVo = new UserVo();
                    userVo.name = user[0];
                    userVo.password = user[1];
                    mAccountList.add(userVo);
                }
            } else {
                String[] user = userInfos.split("/");
                UserVo userVo = new UserVo();
                userVo.name = user[0];
                if (user.length == 2) {
                    userVo.password = user[1];
                }
                mAccountList.add(userVo);
            }
        }
    }

    @Override
    public void saveAccount(String userName, String password) {
        String encryptPwd = CommonUtil.AESEncrypt(getView(), password);
        checkAccount(userName, password);
        UserVo userVo = new UserVo();
        userVo.name = userName;
        userVo.password = password;
        mAccountList.add(userVo);
        String userInfos = "";
        for (UserVo mUserVo : mAccountList) {
            String uName = mUserVo.name;
            String uPwd = mUserVo.password;
            String userInfo = uName + "/" + uPwd;
            if (TextUtils.isEmpty(userInfos)) {
                userInfos = userInfo;
            } else {
                userInfos += "," + userInfo;
            }
        }
        PreferenceTool.putString(Constants.APP_ACCOUNT_INFO, userInfos);
        PreferenceTool.commit();
    }

    @Override
    public void checkAccount(String uName, String pwd) {
        if (mAccountList.size() == 0) {
            return;
        }
        int position = 0;

        for (int i = 0; i < mAccountList.size(); i++) {
            UserVo userVo = mAccountList.get(i);
            String userName = userVo.name;
            String password = userVo.password;
            String decryptPwd = CommonUtil.AESEncrypt(getView(), pwd);
            if (!TextUtils.isEmpty(uName)
                    && !TextUtils.isEmpty(pwd)
                    && !TextUtils.isEmpty(password)
                    && userName.equals(uName) && password.equals(pwd)) {
                position = i;
                break;
            }
        }
        mAccountList.remove(position);
    }

    @Override
    public void attemptLogin(String userName, String password) {
        getView().showProgress(true);
        new Thread(() -> {
            boolean isLogin;
            AbstractXMPPConnection connection;
            XMPPConnectionManager manager = XMPPConnectionManager.getInstance();
            manager.initConnection(userName, password);
            connection = manager.getConnection();
            try {
                connection.connect();
                if (connection.isConnected()) {
                    connection.login(userName, password);
                }
                isLogin = connection.isAuthenticated();
            } catch (IOException | XMPPException | SmackException e) {
                e.printStackTrace();
                isLogin = false;
            }

            dealResult(isLogin, userName, password, connection);
        }).start();
    }

    private void dealResult(boolean login, String userName, String password, AbstractXMPPConnection conn) {

        getView().runOnUiThread(() -> getView().showProgress(false));

        if (login) {
            getView().runOnUiThread(() -> getView().loadSuccess());

            saveAccount(userName, password);
            UserVo userVo = new UserVo();
            userVo.name = userName;
            try {
                VCardManager vCardManager = VCardManager.getInstanceFor(conn);
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
            loginRepository.insertOrUpdateAccount(getView(), userVo);
            PreferenceTool.putBoolean(Constants.LOGIN_STATUS, true);
            PreferenceTool.putString(Constants.LOGIN_UNAME, userName);
            PreferenceTool.putString(Constants.LOGIN_UPASS, password);
            PreferenceTool.commit();
            getView().runOnUiThread(() -> goToMainAct());

        } else {
            getView().runOnUiThread(() -> getView().loadFailed());
        }
    }
}
