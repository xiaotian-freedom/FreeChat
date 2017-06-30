package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;

/**
 * 好友数据库表
 * Created by tianshutong on 2016/12/21.
 */

public class FriendsEntity extends Model {

    public CharField name;
    public CharField jid;
    public CharField presence;
    public CharField myJid;

    public FriendsEntity() {
        this.name = new CharField();
        this.jid = new CharField();
        this.presence = new CharField();
        this.myJid = new CharField();
    }
}
