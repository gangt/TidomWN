package com.xiangpu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andi on 2017/6/8.
 */

public class ChatUserInfo implements Parcelable {
    public String name;
    public String avatar;
    public String userId;
    public String headerUrl;

    public ChatUserInfo(){};

    protected ChatUserInfo(Parcel in) {
        name = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatUserInfo> CREATOR = new Creator<ChatUserInfo>() {
        @Override
        public ChatUserInfo createFromParcel(Parcel in) {
            return new ChatUserInfo(in);
        }

        @Override
        public ChatUserInfo[] newArray(int size) {
            return new ChatUserInfo[size];
        }
    };
}
