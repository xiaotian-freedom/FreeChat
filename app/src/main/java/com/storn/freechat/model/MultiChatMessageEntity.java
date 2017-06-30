package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.IntegerField;

/**
 * 多人聊天数据库表
 * Created by tianshutong on 2017/1/9.
 */
public class MultiChatMessageEntity extends Model {

    public IntegerField cId;
    public CharField myJid;
    public CharField roomJid;
    public CharField roomName;
    public CharField fromName;
    public CharField content;
    public IntegerField time;
    public IntegerField type;

    public MultiChatMessageEntity() {
        this.cId = new IntegerField();
        this.myJid = new CharField();
        this.roomJid = new CharField();
        this.roomName = new CharField();
        this.fromName = new CharField();
        this.content = new CharField();
        this.time = new IntegerField();
        this.type = new IntegerField();
    }
}
