package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.IntegerField;

/**
 * 分组数据库表
 * Created by tianshutong on 2016/12/21.
 */

public class FriendsGroup extends Model {

    public CharField name;
    public IntegerField count;

    public FriendsGroup() {
        this.name = new CharField();
        this.count = new IntegerField();
    }
}
