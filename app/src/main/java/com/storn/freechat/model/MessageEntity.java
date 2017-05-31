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

    public CharField fromJid;

    public CharField myJid;

    public CharField name;

    public CharField content;

    public IntegerField time;

    public IntegerField msgCount;

    public MessageEntity() {
        this.fromJid = new CharField();
        this.myJid = new CharField();
        this.name = new CharField();
        this.content = new CharField();
        this.time = new IntegerField();
        this.msgCount = new IntegerField();
    }
}
