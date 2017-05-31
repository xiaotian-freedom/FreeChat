package com.storn.freechat.login.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.util.CommonUtil;
import com.common.util.FastBlur;
import com.common.util.PermissionUtil;
import com.common.util.SoftKeyBoardUtil;
import com.common.util.ToastUtil;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.jude.beam.bijection.RequiresPresenter;
import com.jude.beam.expansion.BeamBaseActivity;
import com.storn.freechat.R;
import com.storn.freechat.login.presenter.LoginContract;
import com.storn.freechat.login.presenter.LoginPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import storn.github.io.loadingview.CircleLoadingView;

/**
 * A login screen that offers login via email/password.
 */
@RequiresPresenter(LoginPresenter.class)
public class LoginActivity extends BeamBaseActivity<LoginPresenter> implements LoginContract.View {

    private static final int READ_PHONE = 100;
    @BindView(R.id.account)
    AutoCompleteTextView mAccountView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_sign_in_button)
    Button mLoginFormView;
    @BindView(R.id.login_card_view)
    CardView loginCardView;
    @BindView(R.id.login_fab)
    FloatingActionButton loginFab;
    @BindView(R.id.blur_background)
    ImageView blurBackground;
    @BindView(R.id.login_progress)
    CircleLoadingView mProgressView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                ToastUtil.showToast(this, "授权失败,无法记录密码", R.id.login_form, Effects.standard);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        transparentStatusBar();
        Bitmap resourceBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.bg);
        Bitmap blurBmp = FastBlur.blurBitmap(this, resourceBmp, 20);
        blurBackground.setImageBitmap(blurBmp);
        initData();
        initListener();
    }

    protected void transparentStatusBar() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    private void initData() {
        if (!PermissionUtil.isReadPhone(this)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        }
    }

    private void initListener() {
        mPasswordView.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent) -> {
                    if (id == R.id.login_sign_in_button || id == EditorInfo.IME_NULL) {
                        checkFocusView();
                        return true;
                    }
                    return false;
                }
        );
    }

    @Override
    public void fillLastAccount(String userName, String password) {
        if (!TextUtils.isEmpty(userName)) {
            mAccountView.setText(userName);
        }
        if (!TextUtils.isEmpty(password)) {
            mPasswordView.setText(password);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addAccountsToAutoComplete(List<?> list) {
        List<String> accountList = (List<String>) list;
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, accountList);
        mAccountView.setAdapter(adapter);
    }

    @Override
    public void showProgress(boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        if (mProgressView.getVisibility() == View.VISIBLE) {
            mProgressView.postDelayed(() -> mProgressView.startAnim(), 200);
        }
    }

    @Override
    public void loadSuccess() {
        SoftKeyBoardUtil.hideSoftKeyboard(LoginActivity.this);
        mProgressView.success();
    }

    @Override
    public void loadFailed() {
        SoftKeyBoardUtil.hideSoftKeyboard(LoginActivity.this);
        mProgressView.fail();
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
    }

    @Override
    public void checkFocusView() {
        boolean cancel = false;
        View focusView = null;

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        String userName = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            focusView = mAccountView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            CommonUtil.setShakeAnimation(focusView);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            SoftKeyBoardUtil.hideSoftKeyboard(this);
            getPresenter().attemptLogin(userName, password);
        }
    }

    @OnClick({R.id.login_sign_in_button, R.id.login_fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_sign_in_button:
                checkFocusView();
                break;
            case R.id.login_fab:
                getPresenter().goToRegAct(loginFab, loginFab.getTransitionName());
                break;
            default:
                break;
        }
    }

}

