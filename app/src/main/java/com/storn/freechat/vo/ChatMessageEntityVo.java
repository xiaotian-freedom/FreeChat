package com.storn.freechat.vo;

import java.io.Serializable;

/**
 * 聊天实体类
 * Created by tianshutong on 2017/1/9.
 */
public class ChatMessageEntityVo implements Serializable{

    //主键
    public int cId;

    //对方的jid
    public String fromJid;

    //登录用户的Jid, 避免切换账号查询聊天记录时混乱
    public String myJid;

    public String content;

    public long time;

    public int type;

    @Override
    public String toString() {
        return "ChatMessageEntityVo{" +
                "cId=" + cId +
                ", fromJid='" + fromJid + '\'' +
                ", myJid='" + myJid + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", type=" + type +
                '}';
    }
}
