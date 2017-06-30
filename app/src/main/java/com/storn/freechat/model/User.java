package com.storn.freechat.model;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;

/**
 * 用户信息数据库表
 *
 * @author tianshutong
 *         Created by tianshutong on 2016/12/9.
 */

public class User extends Model {

    public CharField jid;//jid
    public CharField name;//用户名
    public CharField password;//密码
    public CharField img;//头像
    public CharField signature;//个性签名
    public CharField nickName;//昵称
    public CharField telephone;
    public CharField email;
    public CharField qq;

    public User() {
        this.jid = new CharField();
        this.name = new CharField();
        this.password = new CharField();
        this.img = new CharField();
        this.signature = new CharField();
        this.nickName = new CharField();
        this.telephone = new CharField();
        this.email = new CharField();
        this.qq = new CharField();
    }
}
