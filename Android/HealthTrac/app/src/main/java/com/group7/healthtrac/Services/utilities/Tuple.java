package com.group7.healthtrac.services.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import com.group7.healthtrac.models.User;

public class Tuple implements Parcelable {

    private User m_Item1;
    private double m_Item2;

    public Tuple(User user, double value) {
        m_Item1 = user;
        m_Item2 = value;
    }

    public User getUser() {
        return m_Item1;
    }

    public void setUser(User user) {
        this.m_Item1 = user;
    }

    public double getValue() {
        return m_Item2;
    }

    public void setValue(double value) {
        this.m_Item2 = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(m_Item1, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeDouble(m_Item2);
    }

    public static final Creator<Tuple> CREATOR = new Creator<Tuple>() {
        @Override
        public Tuple createFromParcel(Parcel source) {
            return new Tuple(source);
        }

        @Override
        public Tuple[] newArray(int size) {
            return new Tuple[size];
        }
    };

    private Tuple(Parcel in) {
        m_Item1 = in.readParcelable(User.class.getClassLoader());
        m_Item2 = in.readDouble();
    }
}
