package com.group7.healthtrac.models;

import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by Mike C on 4/10/2015.
 */
public class UpdateUser {

    private int HeightFeet;
    private int HeightInches;
    private int Weight;
    private String FullName;
    private String PreferredName;
    private String Location;
    private Date BirthDate;
    private String Gender;
    private String Email;
    private String UserName;
    private String ImageUrl;
    private String FacebookId;
    private String Id;

    public UpdateUser(User user) {
        HeightFeet = user.getHeightFeet();
        HeightInches = user.getHeightInches();
        Weight = user.getWeight();
        FullName = user.getFullName();
        PreferredName = user.getPreferredName();
        Location = user.getLocation();
        BirthDate = user.getBirthDate();
        Gender = user.getGender();
        Email = user.getEmail();
        UserName = user.getEmail();
        ImageUrl = user.getImageUrl();
        FacebookId = user.getSocialNetworkId();
        Id = user.getId();
    }
}
