package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.IntegerField;

/**
 * 主页消息数据库表
 *
 * @author tianshutong
 *         Created by tianshutong on 2017/1/15.
 */
public class MessageEntity extends Model {

    public IntegerField mId;

    public CharField jid;

    public CharField myJid;

    public CharField fromName;

    public CharField content;

    public IntegerField time;

    public IntegerField msgCount;

    public CharField roomName;

    public IntegerField type;

    public IntegerField messageType;

    public IntegerField status;

    public MessageEntity() {
        this.mId = new IntegerField();
        this.jid = new CharField();
        this.myJid = new CharField();
        this.fromName = new CharField();
        this.content = new CharField();
        this.time = new IntegerField();
        this.msgCount = new IntegerField();
        this.roomName = new CharField();
        this.type = new IntegerField();
        this.messageType = new IntegerField();
        this.status = new IntegerField();
    }
}
