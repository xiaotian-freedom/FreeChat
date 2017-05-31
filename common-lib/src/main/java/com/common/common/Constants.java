package com.common.common;

import com.common.R;

/**
 * 常量
 * Created by tianshutong on 16/7/16.
 */

public class Constants {

    //随机头像
    public static final int COLORS[] = new int[]{R.drawable.color01, R.drawable.color02, R.drawable.color03,
            R.drawable.color04, R.drawable.color05, R.drawable.color06, R.drawable.color07, R.drawable.color08};

    /* APP包名 */
    public static final String APP_PACKAGE_NAME = "com.storn.freechat";
    /* 数据库名 */
    public static final String DB_NAME = "freechat.db";
    /* 用户信息 */
    public static final String APP_ACCOUNT_INFO = "user_info";
    /* 登录状态 */
    public static final String LOGIN_STATUS = "is_login";
    /* 最后登录用户名 */
    public static final String LOGIN_UNAME = "userName";
    /* 登录密码 */
    public static final String LOGIN_UPASS = "password";
    /* 加密密钥 */
    public static final String ENCRYPT_SEED = "lovestory";
    //平移属性
    public static final String TRANS_PROPERTY = "translationY";
    //渐变属性
    public static final String ALPHA_PROPERTY = "alpha";
    //缩放属性
    public static final String SCALE_X_PROPERTY = "scaleX";
    public static final String SCALE_Y_PROPERTY = "scaleY";
    //侧滑栏阴影宽度
    public static final float MENU_SHADOW_WIDTH = 30.0f;
    //侧滑栏边缘触控区域宽度
    public static final int MENU_EDGE_WIDTH = 100;
    //Toolbar右侧图标的宽高
    public static final int MENU_RIGHT_WIDTH = 15;
    public static final int MENU_RIGHT_HEIGHT = 16;

    //左侧菜单栏
    public static final int MENU_LEFT_MESSAGE = 1;
    public static final int MENU_LEFT_GROUP = 2;
    public static final int MENU_LEFT_CONTACT = 3;
    public static final int MENU_LEFT_SERVICE = 4;
    public static final int MENU_LEFT_SETTINGS = 5;
    public static final int MENU_LEFT_CLOSED = 6;

    //延迟时间
    public static final int DELAY_500 = 500;
    public static final int DELAY_1000 = 1000;

    //动画时长
    public static final int ANIM_300 = 300;
    public static final int ANIM_500 = 500;
    public static final int ANIM_1000 = 1000;
    public static final int RESPONSE_TIME_OUT = 10000;
    public static final String RESOURCE = "android";

    //XMPP 电话类型
    public static final String PHONE_TYPE_1 = "VOICE";
    public static final String PHONE_TYPE_2 = "FAX";
    public static final String PHONE_TYPE_3 = "PAGER";
    public static final String PHONE_TYPE_4 = "MSG";
    public static final String PHONE_TYPE_5 = "CELL";
    public static final String PHONE_TYPE_6 = "VIDEO";
    public static final String PHONE_TYPE_7 = "BBS";
    public static final String PHONE_TYPE_8 = "MODEM";
    public static final String PHONE_TYPE_9 = "ISDN";
    public static final String PHONE_TYPE_10 = "PCS";
    public static final String PHONE_TYPE_11 = "PREF";

    ///////////////////////////////////传递信息//////////////////////////////////
    public static final String FRIEND_NAME = "friend_name";
    public static final String FRIEND_JID = "friend_jid";

    //聊天类型
    public static final int CHAT_MESSAGE_TYPE_FROM = 0;
    public static final int CHAT_MESSAGE_TYPE_TO = 1;
    public static final int CHAT_MESSAGE_TYPE_COUNT = 2;
}
