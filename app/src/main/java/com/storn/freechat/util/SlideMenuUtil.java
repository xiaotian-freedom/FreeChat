package com.storn.freechat.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.common.util.DensityUtil;
import com.storn.freechat.fragment.MenuLeftFragment;
import com.storn.freechat.fragment.MenuRightFragment;
import com.storn.freechat.R;
import com.storn.freechat.anim.CollapseAnimation;
import com.storn.freechat.anim.ExpandAnimation;

/**
 * 菜单打开与关闭工具
 * Created by tianshutong on 2016/12/8.
 */

public class SlideMenuUtil {

    private static int panelWidth;

    public static void openLeftMenu(Activity theActivity, LinearLayout slidingPanel,
                                    FrameLayout menuPanel) {
        panelWidth = (int) (DensityUtil.getScreenWidth(theActivity) * 0.5);
        FragmentManager fragmentManager = theActivity.getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.menu_left_panel);
        if (fragment == null) {
            fragment = new MenuLeftFragment();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.add(R.id.menu_left_panel, fragment);
            fragmentTransaction.commit();
        }
        new ExpandAnimation(slidingPanel, panelWidth,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, 0, 0.0f, 0, 0.0f);
        FrameLayout.LayoutParams menuPanelParameters = (FrameLayout.LayoutParams) menuPanel.getLayoutParams();
        menuPanelParameters.width = panelWidth;
        menuPanelParameters.gravity = Gravity.START;
        menuPanel.setLayoutParams(menuPanelParameters);
    }

    public static void openRightMenu(Activity theActivity, LinearLayout slidingPanel,
                                     FrameLayout menuPanel) {
        int screenWidth = DensityUtil.getScreenWidth(theActivity);
        panelWidth = (int) (screenWidth * -0.75);

        FragmentManager fragmentManager = theActivity.getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.menu_right_panel);
        if (fragment == null) {
            fragment = new MenuRightFragment();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.add(R.id.menu_right_panel, fragment);
            fragmentTransaction.commit();
        }
        new ExpandAnimation(slidingPanel, panelWidth,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -0.75f, 0, 0.0f, 0,
                0.0f);
        FrameLayout.LayoutParams menuPanelParameters = (FrameLayout.LayoutParams) menuPanel.getLayoutParams();
        menuPanelParameters.width = (int) (screenWidth * 0.75);
        menuPanelParameters.gravity = Gravity.END;
        menuPanel.setLayoutParams(menuPanelParameters);
    }

    public static void closeLeftMenu(Activity theActivity, LinearLayout slidingPanel,
                                     FrameLayout menuPanel) {
        int screenWidth = DensityUtil.getScreenWidth(theActivity);
        new CollapseAnimation(slidingPanel, menuPanel, screenWidth,
                TranslateAnimation.RELATIVE_TO_SELF, 0.5f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f,
                0, 0.0f);
    }

    public static void closeRightMenu(Activity theActivity, LinearLayout slidingPanel,
                                      FrameLayout menuPanel) {
        int screenWidth = DensityUtil.getScreenWidth(theActivity);
        new CollapseAnimation(slidingPanel, menuPanel, screenWidth,
                TranslateAnimation.RELATIVE_TO_SELF, -0.75f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f,
                0, 0.0f);
    }
}
