package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.IntegerField;

/**
 * 聊天数据库表
 * Created by tianshutong on 2017/1/9.
 */
public class ChatMessageEntity extends Model {

    public IntegerField cId;
    public CharField fromJid;
    public CharField myJid;
    public CharField content;
    public CharField audioPath;
    public CharField imgPath;
    public IntegerField audioTime;
    public IntegerField time;
    public IntegerField type;
    public IntegerField messageType;
    public IntegerField status;

    public ChatMessageEntity() {
        this.cId = new IntegerField();
        this.fromJid = new CharField();
        this.myJid = new CharField();
        this.content = new CharField();
        this.audioPath = new CharField();
        this.imgPath = new CharField();
        this.audioTime = new IntegerField();
        this.time = new IntegerField();
        this.type = new IntegerField();
        this.messageType = new IntegerField();
        this.status = new IntegerField();
    }
}
