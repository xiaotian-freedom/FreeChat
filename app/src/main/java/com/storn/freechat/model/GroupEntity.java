package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;

/**
 * 群数据库表
 * Created by tianshutong on 2017/1/9.
 */

public class GroupEntity extends Model {

    public CharField roomName;
    public CharField roomJid;

    public GroupEntity() {
        this.roomName = new CharField();
        this.roomJid = new CharField();
    }
}
