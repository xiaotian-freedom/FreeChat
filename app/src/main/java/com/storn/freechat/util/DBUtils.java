package com.storn.freechat.util;

import android.content.ContentValues;

import com.storn.freechat.vo.ChatMessageEntityVo;
import com.storn.freechat.vo.FriendsEntityVo;
import com.storn.freechat.vo.FriendsGroupVo;
import com.storn.freechat.vo.GroupEntityVo;
import com.storn.freechat.vo.MessageEntityVo;
import com.storn.freechat.vo.MultiChatEntityVo;
import com.storn.freechat.vo.UserVo;

/**
 * 数据库工具类
 *
 * @author tianshutong
 *         Created by tianshutong on 2017/1/16.
 */

public class DBUtils {

    /**
     * 组装登录用户数据
     *
     * @param userVo
     * @return
     */
    public static ContentValues UserVo2Cv(UserVo userVo) {
        ContentValues ctv = new ContentValues();
        ctv.put("jid", userVo.jid);
        ctv.put("name", userVo.name);
        ctv.put("password", userVo.password);
        ctv.put("img", userVo.img);
        ctv.put("signature", userVo.signature);
        ctv.put("nickName", userVo.nickName);
        ctv.put("telephone", userVo.telephone);
        ctv.put("email", userVo.email);
        ctv.put("qq", userVo.qq);
        return ctv;
    }

    /**
     * 组装消息数据
     *
     * @param messageEntityVo
     * @return
     */
    public static ContentValues Message2Cv(MessageEntityVo messageEntityVo) {
        ContentValues ctv = new ContentValues();
        ctv.put("mId", messageEntityVo.mId);
        ctv.put("jid", messageEntityVo.jid);
        ctv.put("myJid", messageEntityVo.myJid);
        ctv.put("roomName", messageEntityVo.roomName);
        ctv.put("fromName", messageEntityVo.fromName);
        ctv.put("content", messageEntityVo.content);
        ctv.put("time", messageEntityVo.time);
        ctv.put("msgCount", messageEntityVo.msgCount);
        ctv.put("type", messageEntityVo.type);
        return ctv;
    }

    /**
     * 组装聊天消息
     *
     * @param chatMessageEntityVo
     * @return
     */
    public static ContentValues ChatMessage2Cv(ChatMessageEntityVo chatMessageEntityVo) {
        ContentValues ctv = new ContentValues();
        ctv.put("cId", chatMessageEntityVo.cId);
        ctv.put("fromJid", chatMessageEntityVo.fromJid);
        ctv.put("myJid", chatMessageEntityVo.myJid);
        ctv.put("content", chatMessageEntityVo.content);
        ctv.put("time", chatMessageEntityVo.time);
        ctv.put("type", chatMessageEntityVo.type);
        return ctv;
    }

    /**
     * 组装多人聊天消息
     *
     * @param multiChatEntityVo
     * @return
     */
    public static ContentValues MultiChatMessage2Cv(MultiChatEntityVo multiChatEntityVo) {
        ContentValues ctv = new ContentValues();
        ctv.put("cId", multiChatEntityVo.cId);
        ctv.put("myJid", multiChatEntityVo.myJid);
        ctv.put("roomJid", multiChatEntityVo.roomJid);
        ctv.put("roomName", multiChatEntityVo.roomName);
        ctv.put("fromName", multiChatEntityVo.fromName);
        ctv.put("content", multiChatEntityVo.content);
        ctv.put("time", multiChatEntityVo.time);
        ctv.put("type", multiChatEntityVo.type);
        return ctv;
    }

    /**
     * 组装群数据
     *
     * @param groupEntityVo
     * @return
     */
    public static ContentValues Group2Cv(GroupEntityVo groupEntityVo) {
        ContentValues ctv = new ContentValues();
        ctv.put("roomName", groupEntityVo.roomName);
        ctv.put("roomJid", groupEntityVo.roomJid);
        return ctv;
    }

    /**
     * 组装好友分组
     *
     * @param friendsGroupVo
     * @return
     */
    public static ContentValues FriendsGroup2Cv(FriendsGroupVo friendsGroupVo) {
        ContentValues ctv = new ContentValues();
        ctv.put("name", friendsGroupVo.name);
        ctv.put("myJid",friendsGroupVo.myJid);
        ctv.put("count", friendsGroupVo.count);
        return ctv;
    }

    /**
     * 组装好友
     *
     * @param friendsEntityVo
     * @return
     */
    public static ContentValues Friends2Cv(FriendsEntityVo friendsEntityVo) {
        ContentValues ctv = new ContentValues();
        ctv.put("name", friendsEntityVo.name);
        ctv.put("jid", friendsEntityVo.jid);
        ctv.put("presence", friendsEntityVo.presence);
        ctv.put("myJid", friendsEntityVo.myJid);
        return ctv;
    }
}
