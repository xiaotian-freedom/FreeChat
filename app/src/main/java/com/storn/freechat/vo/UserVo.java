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
    public String password;
    public String from;
    public String status;
    public String groupName;
    public int imgId;
    public int groupSize;
    public boolean available;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(jid);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(from);
        dest.writeString(status);
        dest.writeString(groupName);
        dest.writeInt(imgId);
        dest.writeInt(groupSize);
        dest.writeInt(available ? 1 : 0);
    }

    public static final Parcelable.Creator<UserVo> CREATOR = new Parcelable.Creator<UserVo>() {

        @Override
        public UserVo createFromParcel(Parcel source) {
            UserVo u = new UserVo();
            u.jid = source.readString();
            u.name = source.readString();
            u.password = source.readString();
            u.from = source.readString();
            u.status = source.readString();
            u.groupName = source.readString();
            u.imgId = source.readInt();
            u.groupSize = source.readInt();
            u.available = source.readInt() == 1;
            return u;
        }

        @Override
        public UserVo[] newArray(int size) {
            return new UserVo[size];
        }

    };

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
