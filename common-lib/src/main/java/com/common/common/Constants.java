package com.common.common;

import com.common.R;

/**
 * 常量
 * Created by tianshutong on 16/7/16.
 */

public class Constants {

    //本地广播
    public static final String LOCAL_ACTION = "com.storn.freechat.BROADCAST_ACTION";

    //随机头像
    public static final int COLORS[] = new int[]{R.drawable.color01, R.drawable.color02, R.drawable.color03,
            R.drawable.color04, R.drawable.color05, R.drawable.color06, R.drawable.color07, R.drawable.color08};

    /* APP包名 */
    public static final String APP_PACKAGE_NAME = "com.storn.freechat";
    /* 数据库名 */
    public static final String DB_NAME = "freechat.db";
    /* jid后缀 */
    public static final String JID_POST = "@freechat.storn.com/android";
    /* 用户信息 */
    public static final String APP_ACCOUNT_INFO = "user_info";
    /* 登录状态 */
    public static final String LOGIN_STATUS = "is_login";
    /* 最后登录用户名 */
    public static final String LOGIN_UNAME = "userName";
    /* 最后登录的用户jid */
    public static final String LOGIN_JID = "myJid";
    /* 登录密码 */
    public static final String LOGIN_UPASS = "password";
    /* 加密密钥 */
    public static final String ENCRYPT_SEED = "lovestory";
    //平移属性
    public static final String TRANS_PROPERTY = "translationY";
    //渐变属性
    public static final String ALPHA_PROPERTY = "alpha";
    //旋转
    public static final String ROTATION_X = "rotationX";
    public static final String ROTATION_Y = "rotationY";
    public static final String ROTATION = "rotation";
    //缩放属性
    public static final String SCALE_X_PROPERTY = "scaleX";
    public static final String SCALE_Y_PROPERTY = "scaleY";
    //Toolbar右侧图标的宽高
    public static final int MENU_RIGHT_WIDTH = 15;
    public static final int MENU_RIGHT_HEIGHT = 16;

    //延迟时间
    public static final int DELAY_300 = 300;
    public static final int DELAY_500 = 500;
    public static final int DELAY_1000 = 1000;
    public static final int DELAY_2000 = 2000;

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
    public static final String MESSAGEVO = "message_entity_vo";

    //聊天类型
    public static final int CHAT_MESSAGE_TYPE_FROM = 0;
    public static final int CHAT_MESSAGE_TYPE_TO = 1;
    public static final int CHAT_MESSAGE_TYPE_COUNT = 2;

    //聊天室保存的历史记录数量
    public static final int MULTICHAT_MAX_HISTORY = 10 * 1000;

    //刷新消息通知
    public static final int REFRESH_MESSAGE = 200;
    public static final int ADD_CHAT_MESSAGE = 201;
    public static final int CLEAR_EDIT_TEXT = 202;
    public static final int CLEAR_MESSAGE_TIP = 203;
    public static final int CONNECT_SERVICE = 204;

}
