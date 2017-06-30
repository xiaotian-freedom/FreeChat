package com.storn.freechat.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户信息vo
 *
 * @author tianshutong
 *         Created by tianshutong on 2016/12/9.
 */

public class UserVo implements Parcelable {

    public String jid;
    public String name;
    public String nickName;
    public String password;
    public String img;
    public String signature;
    public String telephone;
    public String email;
    public String qq;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(jid);
        dest.writeString(name);
        dest.writeString(nickName);
        dest.writeString(password);
        dest.writeString(img);
        dest.writeString(signature);
        dest.writeString(telephone);
        dest.writeString(email);
        dest.writeString(qq);
    }

    public static final Parcelable.Creator<UserVo> CREATOR = new Parcelable.Creator<UserVo>() {

        @Override
        public UserVo createFromParcel(Parcel source) {
            UserVo u = new UserVo();
            u.jid = source.readString();
            u.name = source.readString();
            u.nickName = source.readString();
            u.password = source.readString();
            u.img = source.readString();
            u.signature = source.readString();
            u.telephone = source.readString();
            u.email = source.readString();
            u.qq = source.readString();
            return u;
        }

        @Override
        public UserVo[] newArray(int size) {
            return new UserVo[size];
        }

    };

}
