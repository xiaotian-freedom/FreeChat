package com.storn.freechat.vo;

import java.io.Serializable;

/**
 * 聊天实体类
 * Created by tianshutong on 2017/1/9.
 */
public class ChatMessageEntityVo implements Serializable {

    //主键
    public int cId;

    //对方的jid
    public String fromJid;

    //登录用户的Jid, 避免切换账号查询聊天记录时混乱
    public String myJid;

    public String content;

    public String audioPath;

    public String imgPath;

    public long audioTime;

    public long time;

    //发送、接收类型
    public int type;

    //消息类型 0:文本 1:语音 2:图片
    public int messageType;

    //发送状态 0:失败 1:成功
    public int status;

    @Override
    public String toString() {
        return "ChatMessageEntityVo{" +
                "cId=" + cId +
                ", fromJid='" + fromJid + '\'' +
                ", myJid='" + myJid + '\'' +
                ", content='" + content + '\'' +
                ", audioPath='" + audioPath + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", audioTime=" + audioTime +
                ", time=" + time +
                ", type=" + type +
                ", messageType=" + messageType +
                ", status=" + status +
                '}';
    }
}
