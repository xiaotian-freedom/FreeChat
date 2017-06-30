package com.storn.freechat.vo;

import java.io.Serializable;

/**
 * 多人聊天对象
 * Created by tianshutong on 2017/6/19.
 */

public class MultiChatEntityVo implements Serializable {

    //主键
    public int cId;

    //登录用户的Jid, 避免切换账号查询聊天记录时混乱
    public String myJid;

    public String roomJid;

    public String roomName;

    public String fromName;

    public String content;

    public long time;

    public int type;
}
