package com.storn.freechat.login.model;

import android.content.Context;

import com.storn.freechat.common.DBHelper;
import com.storn.freechat.login.presenter.LoginContract;
import com.storn.freechat.vo.UserVo;

/**
 * Created by tianshutong on 2017/4/7.
 */

public class LoginRepository implements LoginContract.ILoginRepo {

    private static LoginRepository Instance = null;

    private LoginRepository() {
    }

    public static LoginRepository getInstance() {
        if (Instance == null) {
            synchronized (LoginRepository.class) {
                if (Instance == null) {
                    Instance = new LoginRepository();
                }
            }
        }
        return Instance;
    }

    @Override
    public void insertOrUpdateAccount(Context context, UserVo userVo) {
        DBHelper.getInstance().insertOrUpdateAccount(context, userVo);
    }
}
