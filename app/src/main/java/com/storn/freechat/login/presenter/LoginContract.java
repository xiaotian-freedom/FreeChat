package com.storn.freechat.login.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.storn.freechat.vo.UserVo;

import java.util.List;

/**
 * Created by tianshutong on 2017/4/7.
 */

public interface LoginContract {

    interface View {

        void loadSuccess();

        void loadFailed();

        void checkFocusView();

        void showProgress(boolean active);

        void addAccountsToAutoComplete(List<?> list);

        void fillLastAccount(String userName, String password);

    }

    interface Presenter {

        void goToMainAct();

        void goToRegAct(android.view.View view, String s);

        void getAccounts();

        void saveAccount(String userName, String password);

        void checkAccount(String userName, String password);

        void attemptLogin(String userName, String password);

    }

    interface ILoginRepo {

        interface LoginTaskCallback extends Runnable {

            void onLoadSuccess();

            void onLoadFailed();
        }

        void getTask(@NonNull String uName, @NonNull String uPwd, @NonNull LoginTaskCallback callback);

        void insertOrUpdateAccount(Context context, UserVo userVo);
    }
}
