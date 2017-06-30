package com.storn.freechat.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.Where;
import com.orm.androrm.statement.SelectStatement;
import com.orm.androrm.tovo.CreateVoBySqlite;
import com.storn.freechat.model.ChatMessageEntity;
import com.storn.freechat.model.FriendsEntity;
import com.storn.freechat.model.FriendsGroup;
import com.storn.freechat.model.GroupEntity;
import com.storn.freechat.model.MessageEntity;
import com.storn.freechat.model.MultiChatMessageEntity;
import com.storn.freechat.model.User;
import com.storn.freechat.vo.ChatMessageEntityVo;
import com.storn.freechat.vo.FriendsEntityVo;
import com.storn.freechat.vo.FriendsGroupVo;
import com.storn.freechat.vo.GroupEntityVo;
import com.storn.freechat.vo.MessageEntityVo;
import com.storn.freechat.vo.MultiChatEntityVo;
import com.storn.freechat.vo.UserVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库
 *
 * @author tianshutong
 *         Created by tianshutong on 2017/1/10.
 * @version 1.0
 */

public class DBHelper {

    /**
     * 数据库版本
     */
    public static final int DATABASE_VERSION = 1;

    private static volatile DBHelper dbHelper;

    /**
     * 获取数据库对象
     *
     * @return
     */
    public static DBHelper getInstance() {
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper();
                }
            }
        }
        return dbHelper;
    }

    /**
     * 初始化数据库
     *
     * @param context
     * @param dbName
     */
    public static void initDataBase(Context context, String dbName) {
        List<Class<? extends Model>> models = new ArrayList<>();
        models.add(MessageEntity.class);
        models.add(ChatMessageEntity.class);
        models.add(MultiChatMessageEntity.class);
        models.add(FriendsEntity.class);
        models.add(GroupEntity.class);
        models.add(FriendsGroup.class);
        models.add(FriendsEntity.class);
        models.add(User.class);
        DatabaseAdapter.setDatabaseName(dbName);
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context, DATABASE_VERSION);
        dba.setModels(models);
    }

    /**
     * 插入或更新登录用户
     *
     * @param context
     * @param userVo
     */
    public void insertOrUpdateAccount(Context context, UserVo userVo) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        ContentValues contentValues = DBUtils.UserVo2Cv(userVo);
        Where where = new Where();
        where.and("jid", userVo.jid);
        dba.doInsertOrUpdate("user", contentValues, where);
    }

    /**
     * 插入或更新消息表
     *
     * @param context
     * @param msgVo
     */
    public void insertOrUpdateMessage(Context context, MessageEntityVo msgVo) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        ContentValues contentValues = DBUtils.Message2Cv(msgVo);
        Where where = new Where();
        where.and("jid", msgVo.jid);
        dba.doInsertOrUpdate("messageentity", contentValues, where);
    }

    /**
     * 插入或更新聊天消息表
     *
     * @param context
     * @param chatMsgVo
     */
    public void insertOrUpdateChatMessage(Context context, ChatMessageEntityVo chatMsgVo) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        ContentValues contentValues = DBUtils.ChatMessage2Cv(chatMsgVo);
        Where where = new Where();
        where.and("cId", chatMsgVo.cId);
        dba.doInsertOrUpdate("chatmessageentity", contentValues, where);
    }

    /**
     * 插入或更新多人聊天消息表
     *
     * @param context
     * @param multiChatEntityVo
     */
    public void insertOrUpdateMultiChatMessage(Context context, MultiChatEntityVo multiChatEntityVo) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        ContentValues contentValues = DBUtils.MultiChatMessage2Cv(multiChatEntityVo);
        Where where = new Where();
        where.and("cId", multiChatEntityVo.cId);
        dba.doInsertOrUpdate("multichatmessageentity", contentValues, where);
    }

    /**
     * 插入或更新群列表
     *
     * @param context
     * @param groupEntityVo
     */
    public void insertOrUpdateGroup(Context context, GroupEntityVo groupEntityVo) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        ContentValues contentValues = DBUtils.Group2Cv(groupEntityVo);
        Where where = new Where();
        where.and("roomJid", groupEntityVo.roomJid);
        dba.doInsertOrUpdate("groupentity", contentValues, where);
    }

    /**
     * 插入或更新好友分组
     *
     * @param context
     * @param friendsGroupVo
     */
    public void insertOrUpdateFriendsGroup(Context context, FriendsGroupVo friendsGroupVo) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        ContentValues contentValues = DBUtils.FriendsGroup2Cv(friendsGroupVo);
        Where where = new Where();
        where.and("name", friendsGroupVo.name);
        dba.doInsertOrUpdate("friendsgroup", contentValues, where);
    }

    /**
     * 插入或更新好友
     *
     * @param context
     * @param friendsEntityVo
     */
    public void insertOrUpdateFriends(Context context, FriendsEntityVo friendsEntityVo) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        ContentValues contentValues = DBUtils.Friends2Cv(friendsEntityVo);
        Where where = new Where();
        where.and("jid", friendsEntityVo.jid);
        dba.doInsertOrUpdate("friendsentity", contentValues, where);
    }

    /**
     * 查询登录用户信息
     * @param context
     * @param jid
     * @return
     */
    public UserVo queryProfileInfo(Context context, String jid) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.from("user");
        Where where = new Where();
        where.and("jid", jid);
        selectStatement.where(where);
        dba.open();
        Cursor cursor = dba.query(selectStatement);
        UserVo userVo = new UserVo();
        try {
            if (cursor != null) {
                userVo = CreateVoBySqlite.cursor2VO(cursor, UserVo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return userVo;
    }

    /**
     * 通过jid查询消息列表
     *
     * @param context
     * @param jid
     */
    public List<MessageEntityVo> queryMessageByJid(Context context, String jid) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.from("messageentity");
        Where where = new Where();
        where.and("myJid", jid);
        selectStatement.where(where);
        selectStatement.orderBy("-time");
        dba.open();
        Cursor cursor = dba.query(selectStatement);
        List<MessageEntityVo> messageEntityVoList = null;
        try {
            if (cursor != null) {
                messageEntityVoList = CreateVoBySqlite.cursor2VOList(cursor, MessageEntityVo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return messageEntityVoList;
    }

    /**
     * 查询群列表
     *
     * @param context
     * @return
     */
    public List<GroupEntityVo> queryGroupList(Context context) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.from("groupentity");
        dba.open();
        Cursor cursor = dba.query(selectStatement);
        List<GroupEntityVo> groupEntityVoList = new ArrayList<>();
        try {
            if (cursor != null) {
                groupEntityVoList = CreateVoBySqlite.cursor2VOList(cursor, GroupEntityVo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return groupEntityVoList;
    }

    /**
     * 查询好友分组
     *
     * @param context
     * @return
     */
    public List<FriendsGroupVo> queryFriendsGroupList(Context context, String jid) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.from("friendsgroup");
        Where where = new Where();
        where.and("myJid", jid);
        selectStatement.where(where);
        dba.open();
        Cursor cursor = dba.query(selectStatement);
        List<FriendsGroupVo> friendsGroupVoList = new ArrayList<>();
        try {
            if (cursor != null) {
                friendsGroupVoList = CreateVoBySqlite.cursor2VOList(cursor, FriendsGroupVo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return friendsGroupVoList;
    }

    /**
     * 查询好友列表
     *
     * @param context
     * @return
     */
    public List<FriendsEntityVo> queryFriendsList(Context context, String jid) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.from("friendsentity");
        Where where = new Where();
        where.and("myJid", jid);
        selectStatement.where(where);
        dba.open();
        Cursor cursor = dba.query(selectStatement);
        List<FriendsEntityVo> friendsEntityVoList = new ArrayList<>();
        try {
            if (cursor != null) {
                friendsEntityVoList = CreateVoBySqlite.cursor2VOList(cursor, FriendsEntityVo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return friendsEntityVoList;
    }

    /**
     * 查询单个消息对象
     *
     * @param context
     * @param jid
     * @return
     */
    public MessageEntityVo querySingleMessageByJid(Context context, String jid) {
        MessageEntityVo messageEntityVo = new MessageEntityVo();
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.from("messageentity");
        Where where = new Where();
        where.and("jid", jid);
        selectStatement.where(where);
        dba.open();
        Cursor cursor = dba.query(selectStatement);
        try {
            messageEntityVo = CreateVoBySqlite.cursor2VO(cursor, MessageEntityVo.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return messageEntityVo;
    }

    /**
     * 根据登录用户查询聊天消息
     *
     * @param context
     * @param myJid
     * @param fromJid
     * @return
     */
    public List<ChatMessageEntityVo> queryChatMessageByJid(Context context, String myJid,
                                                           String fromJid, int offset, int limit) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM chatmessageentity ");
        sb.append("WHERE myJid='");
        sb.append(myJid);
        sb.append("'");
        sb.append(" AND ");
        sb.append("fromJid='");
        sb.append(fromJid);
        sb.append("'");
        sb.append("ORDER BY time DESC");
        if (limit != -1) {
            sb.append(" LIMIT ");
            sb.append(limit);
        }
        if (offset != -1) {
            sb.append(" OFFSET ");
            sb.append(offset);
        }

        dba.open();
        Cursor cursor = dba.query(sb.toString());
        List<ChatMessageEntityVo> chatMessageEntityVoList = null;
        try {
            if (cursor != null) {
                chatMessageEntityVoList = CreateVoBySqlite.cursor2VOList(cursor, ChatMessageEntityVo.class);
                Collections.reverse(chatMessageEntityVoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return chatMessageEntityVoList;
    }

    /**
     * 根据登录用户查询多人聊天消息
     *
     * @param context
     * @param myJid
     * @param roomJid
     * @return
     */
    public List<MultiChatEntityVo> queryMultiChatMessageByJid(Context context, String myJid,
                                                              String roomJid, int offset, int limit) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM multichatmessageentity ");
        sb.append("WHERE myJid='");
        sb.append(myJid);
        sb.append("'");
        sb.append(" AND ");
        sb.append("roomJid='");
        sb.append(roomJid);
        sb.append("'");
        sb.append("ORDER BY time DESC");
        if (limit != -1) {
            sb.append(" LIMIT ");
            sb.append(limit);
        }
        if (offset != -1) {
            sb.append(" OFFSET ");
            sb.append(offset);
        }

        dba.open();
        Cursor cursor = dba.query(sb.toString());
        List<MultiChatEntityVo> chatMessageEntityVoList = null;
        try {
            if (cursor != null) {
                chatMessageEntityVoList = CreateVoBySqlite.cursor2VOList(cursor, MultiChatEntityVo.class);
                Collections.reverse(chatMessageEntityVoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dba.close();
        }
        return chatMessageEntityVoList;
    }

    /**
     * 删除单条聊天消息记录
     *
     * @param context
     * @param cId
     */
    public void deleteChatMessage(Context context, int cId) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        Where where = new Where();
        where.and("cId", cId);
        dba.delete("chatmessageentity", where);
    }

    /**
     * 删除单条群聊消息记录
     *
     * @param context
     * @param cId
     */
    public void deleteMultiChatMessage(Context context, int cId) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        Where where = new Where();
        where.and("cId", cId);
        dba.delete("multichatmessageentity", where);
    }

    /**
     * 删除单条消息列表记录
     *
     * @param context
     * @param mId
     */
    public void deleteMessage(Context context, int mId) {
        DatabaseAdapter dba = DatabaseAdapter.getInstance(context);
        Where where = new Where();
        where.and("mId", mId);
        dba.delete("messageentity", where);
    }

}
