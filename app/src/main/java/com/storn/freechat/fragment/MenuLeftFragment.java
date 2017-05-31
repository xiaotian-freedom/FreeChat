package com.storn.freechat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.common.common.Constants;
import com.common.util.AnimationUtil;
import com.storn.freechat.MainActivity;
import com.storn.freechat.R;
import com.storn.freechat.common.ChatApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 左侧菜单栏
 * Created by tianshutong on 2016/12/8.
 */

public class MenuLeftFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.slide_menu_left_head_view)
    ImageView slideMenuLeftHeadView;
    @BindView(R.id.slide_menu_left_name)
    TextView slideMenuLeftName;
    @BindView(R.id.slide_menu_left_position)
    TextView slideMenuLeftPosition;
    @BindView(R.id.slide_menu_left_line)
    ImageView slideMenuLeftLine;
    @BindView(R.id.slide_menu_left_rb_message)
    RadioButton slideMenuLeftRbMessage;
    @BindView(R.id.slide_menu_left_rb_group)
    RadioButton slideMenuLeftRbGroup;
    @BindView(R.id.slide_menu_left_rb_contact)
    RadioButton slideMenuLeftRbContact;
    @BindView(R.id.slide_menu_left_rb_service)
    RadioButton slideMenuLeftRbService;
    @BindView(R.id.slide_menu_left_rb_settings)
    RadioButton slideMenuLeftRbSettings;
    @BindView(R.id.slide_menu_left_rg)
    RadioGroup slideMenuLeftRg;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_left_panel, container, false);
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        slideMenuLeftRg.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int cursorTop = slideMenuLeftLine.getTop();
        int diffY = 0;
        switch (i) {
            case R.id.slide_menu_left_rb_message:
                int messageTop = slideMenuLeftRbMessage.getTop();
                diffY = messageTop - cursorTop;
                break;
            case R.id.slide_menu_left_rb_group:
                int groupTop = slideMenuLeftRbGroup.getTop();
                diffY = groupTop - cursorTop;
                break;
            case R.id.slide_menu_left_rb_contact:
                int contactTop = slideMenuLeftRbContact.getTop();
                diffY = contactTop - cursorTop;
                break;
            case R.id.slide_menu_left_rb_service:
                int serviceTop = slideMenuLeftRbService.getTop();
                diffY = serviceTop - cursorTop;
                break;
            case R.id.slide_menu_left_rb_settings:
                int settingsTop = slideMenuLeftRbSettings.getTop();
                diffY = settingsTop - cursorTop;
                break;
            default:
                break;
        }
        AnimationUtil.SlideDown(slideMenuLeftLine, diffY);
        refreshView(i);
    }

    private void refreshView(int resId) {
        switch (resId) {
            case R.id.slide_menu_left_rb_message:
                ChatApplication.setMenu_left(Constants.MENU_LEFT_MESSAGE);
                if (MainActivity.mainHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_CLOSED;
                    MainActivity.mainHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                if (MainContentFragment.contentHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_MESSAGE;
                    MainContentFragment.contentHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                break;
            case R.id.slide_menu_left_rb_group:
                ChatApplication.setMenu_left(Constants.MENU_LEFT_GROUP);
                if (MainActivity.mainHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_CLOSED;
                    MainActivity.mainHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                if (MainContentFragment.contentHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_GROUP;
                    MainContentFragment.contentHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                break;
            case R.id.slide_menu_left_rb_contact:
                ChatApplication.setMenu_left(Constants.MENU_LEFT_CONTACT);
                if (MainActivity.mainHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_CLOSED;
                    MainActivity.mainHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                if (MainContentFragment.contentHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_CONTACT;
                    MainContentFragment.contentHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                break;
            case R.id.slide_menu_left_rb_service:
                ChatApplication.setMenu_left(Constants.MENU_LEFT_SERVICE);
                if (MainActivity.mainHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_CLOSED;
                    MainActivity.mainHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                if (MainContentFragment.contentHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_SERVICE;
                    MainContentFragment.contentHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                break;
            case R.id.slide_menu_left_rb_settings:
                ChatApplication.setMenu_left(Constants.MENU_LEFT_SETTINGS);
                if (MainActivity.mainHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_CLOSED;
                    MainActivity.mainHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                if (MainContentFragment.contentHandler != null) {
                    Message message = new Message();
                    message.what = Constants.MENU_LEFT_SETTINGS;
                    MainContentFragment.contentHandler.sendMessageDelayed(message, Constants.DELAY_500);
                }
                break;
            default:
                break;
        }
    }

}
