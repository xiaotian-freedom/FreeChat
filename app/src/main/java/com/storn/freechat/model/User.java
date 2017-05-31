package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.BooleanField;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.IntegerField;

/**
 * 用户信息数据库表
 *
 * @author tianshutong
 *         Created by tianshutong on 2016/12/9.
 */

public class User extends Model {

    public CharField jid;
    public CharField name;
    public CharField password;
    public CharField fromJid;
    public CharField status;
    public CharField groupName;
    public IntegerField imgId;
    public IntegerField groupSize;
    public BooleanField available;

    public User() {
        this.jid = new CharField();
        this.name = new CharField();
        this.password = new CharField();
        this.fromJid = new CharField();
        this.status = new CharField();
        this.groupName = new CharField();
        this.imgId = new IntegerField();
        this.groupSize = new IntegerField();
        this.available = new BooleanField();
    }
}
