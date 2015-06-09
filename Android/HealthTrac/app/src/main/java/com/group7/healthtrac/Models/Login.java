package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mike C on 2/15/2015.
 */
public class Login implements Parcelable {

    private String UserId;
    private String LoginProvider;
    private String ProviderKey;

    public Login(String userId, String loginProvider, String providerKey) {
        UserId = userId;
        LoginProvider = loginProvider;
        ProviderKey = providerKey;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getLoginProvider() {
        return LoginProvider;
    }

    public void setLoginProvider(String loginProvider) {
        LoginProvider = loginProvider;
    }

    public String getProviderKey() {
        return ProviderKey;
    }

    public void setProviderKey(String providerKey) {
        ProviderKey = providerKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UserId);
        dest.writeString(LoginProvider);
        dest.writeString(ProviderKey);
    }

    public static final Creator<Login> CREATOR = new Creator<Login>() {
        @Override
        public Login createFromParcel(Parcel source) {
            return new Login(source);
        }

        @Override
        public Login[] newArray(int size) {
            return new Login[size];
        }
    };

    private Login(Parcel in) {
        UserId = in.readString();
        LoginProvider = in.readString();
        ProviderKey = in.readString();
    }
}
