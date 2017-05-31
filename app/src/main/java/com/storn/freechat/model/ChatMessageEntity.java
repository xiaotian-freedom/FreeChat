package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.IntegerField;

/**
 * 聊天数据库表
 * Created by tianshutong on 2017/1/9.
 */
public class ChatMessageEntity extends Model {

    public CharField fromJid;
    public CharField myJid;
    public CharField content;
    public IntegerField time;
    public IntegerField type;

    public ChatMessageEntity() {
        this.fromJid = new CharField();
        this.myJid = new CharField();
        this.content = new CharField();
        this.time = new IntegerField();
        this.type = new IntegerField();
    }
}
