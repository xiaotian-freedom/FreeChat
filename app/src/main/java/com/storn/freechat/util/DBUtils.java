package com.storn.freechat.util;

import android.content.ContentValues;

import com.storn.freechat.vo.ChatMessageEntityVo;
import com.storn.freechat.vo.FriendsEntityVo;
import com.storn.freechat.vo.FriendsGroupVo;
import com.storn.freechat.vo.GroupEntityVo;
import com.storn.freechat.vo.MessageEntityVo;
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
        ctv.put("fromJid", userVo.from);
        ctv.put("status", userVo.status);
        ctv.put("groupName", userVo.groupName);
        ctv.put("imgId", userVo.imgId);
        ctv.put("groupSize", userVo.groupSize);
        ctv.put("available", userVo.available);
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
        ctv.put("fromJid", messageEntityVo.fromJid);
        ctv.put("myJid", messageEntityVo.myJid);
        ctv.put("name", messageEntityVo.name);
        ctv.put("content", messageEntityVo.content);
        ctv.put("time", messageEntityVo.time);
        ctv.put("msgCount", messageEntityVo.msgCount);
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
        return ctv;
    }
}
