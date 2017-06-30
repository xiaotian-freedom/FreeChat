package com.storn.freechat.vo;

import java.io.Serializable;

/**
 * 主页消息vo
 *
 * @author tianshutong
 *         Created by tianshutong on 2017/1/15.
 */
public class MessageEntityVo implements Serializable{

    public int mId;

    public String jid;//聊天对方的jid或聊天室的jid

    public String myJid;//登录用户的Jid, 避免切换账号查询聊天记录时混乱

    public String fromName;//最后一个发消息人名称

    public String roomName;//聊天室名称

    public String content;//消息内容

    public long time;//消息时间

    public int msgCount;//未读消息数量

    public int type;//消息类型 0: 单人聊天 1:多人聊天 2:其他

    public MessageEntityVo() {
    }

}
