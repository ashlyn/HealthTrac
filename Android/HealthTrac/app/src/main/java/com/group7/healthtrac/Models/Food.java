package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.group7.healthtrac.services.utilities.Utility;

import java.text.ParseException;
import java.util.Date;

public class Food implements Parcelable {

    public enum Measurement {

        Oz(0),
        FlOz(1),
        Cups(2),
        Grams(3),
        Tbsp(4),
        Milliliters(5);

        private int value;

        Measurement(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Measurement fromInt(int i) {
            for (Measurement t : Measurement.values()) {
                if (t.getValue() == i) return t;
            }

            return null;
        }
    }

    private final static String TAG = "Food";
    private int Id;
    private String FoodName;
    private double Amount;
    private int Unit;
    private Date Time;
    private String UserId;

    public Food(int id, String foodName, double amount, int unit, Date time, String userId) {
        Id = id;
        FoodName = foodName;
        Amount = amount;
        Unit = unit;
        Time = time;
        UserId = userId;
    }

    public Food(String foodName, double amount, int unit, Date time, String userId) {
        FoodName = foodName;
        Amount = amount;
        Unit = unit;
        Time = time;
        UserId = userId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public int getUnit() {
        return Unit;
    }

    public void setUnit(int unit) {
        Unit = unit;
    }

    public Date getTime() {
        return Time;
    }

    public void setTime(Date time) {
        Time = time;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(FoodName);
        dest.writeDouble(Amount);
        dest.writeInt(Unit);
        dest.writeString(Time.toString());
        dest.writeString(UserId);
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel source) {
            return new Food(source);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    private Food(Parcel in) {
        Id = in.readInt();
        FoodName = in.readString();
        Amount = in.readDouble();
        Unit = in.readInt();
        try {
            Time = Utility.parseDateFromUtcToDisplay(in.readString());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        UserId = in.readString();
    }
}
