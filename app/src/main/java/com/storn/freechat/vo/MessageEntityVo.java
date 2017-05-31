package com.storn.freechat.vo;

/**
 * 主页消息vo
 *
 * @author tianshutong
 *         Created by tianshutong on 2017/1/15.
 */
public class MessageEntityVo {

    public String fromJid;

    public String myJid;//登录用户的Jid, 避免切换账号查询聊天记录时混乱

    public String name;

    public String content;

    public long time;

    public int msgCount;//未读消息数量
}
