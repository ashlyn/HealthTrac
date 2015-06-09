package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.group7.healthtrac.services.utilities.Utility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Parcelable {

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
    private List<Login> Logins;
    private List<Group> Groups;
    private List<UserBadge> Badges;
    private List<Membership> GroupMembership;
    private List<Goal> Goals;
    private List<Activity> Activities;
    private String ImageUrl;
    private String SocialNetworkId;
    private String Id;

    /* Constructor for creating accounts */
    public User(String FullName, String PreferredName, int HeightFeet, int HeightInches,
                int Weight, String Location, Date Birthday, String Gender, String Email,
                List<Login> Logins, String ImageUrl) {
        this.FullName = FullName;
        this.PreferredName = PreferredName;
        this.HeightFeet = HeightFeet;
        this.HeightInches = HeightInches;
        this.Location = Location;
        this.BirthDate = Birthday;
        this.Gender = Gender;
        this.Weight = Weight;
        this.Email = Email;
        this.UserName = Email;
        this.Logins = Logins;
        this.Groups = new ArrayList<>();
        this.GroupMembership = new ArrayList<>();
        this.Activities = new ArrayList<>();
        this.Goals = new ArrayList<>();
        this.Badges = new ArrayList<>();
        this.SocialNetworkId = Logins.get(0).getProviderKey();
        this.ImageUrl = ImageUrl;
    }

    public User(String FullName, String PreferredName, int HeightFeet, int HeightInches, int Weight,
                String Location, Date Birthday, String Gender, String Email, List<Membership> GroupMembership,
                List<Login> Logins, List<Activity> Activities, List<Goal> Goals, List<UserBadge> Badges,
                String Id, String ImageUrl) {
        this.FullName = FullName;
        this.PreferredName = PreferredName;
        this.HeightFeet = HeightFeet;
        this.HeightInches = HeightInches;
        this.Location = Location;
        this.BirthDate = Birthday;
        this.Gender = Gender;
        this.Weight = Weight;
        this.Email = Email;
        this.UserName = Email;
        this.Logins = Logins;
        this.Groups = new ArrayList<>();
        this.Id = Id;
        this.GroupMembership = GroupMembership;
        this.Badges = Badges;
        this.Activities = Activities;
        this.Goals = Goals;
        this.SocialNetworkId = Logins.get(0).getProviderKey();
        this.ImageUrl = ImageUrl;
    }

    public User(String FullName, String PreferredName, int HeightFeet, int HeightInches, int Weight,
                String Location, Date Birthday, String Gender, String Email, List<Membership> GroupMembership,
                List<Group> Groups, List<Login> Logins, List<Activity> Activities, List<Goal> Goals,
                List<UserBadge> Badges, String Id, String ImageUrl) {
        this.FullName = FullName;
        this.PreferredName = PreferredName;
        this.HeightFeet = HeightFeet;
        this.HeightInches = HeightInches;
        this.Location = Location;
        this.BirthDate = Birthday;
        this.Gender = Gender;
        this.Weight = Weight;
        this.Email = Email;
        this.UserName = Email;
        this.Logins = Logins;
        this.Groups = Groups;
        this.Id = Id;
        this.GroupMembership = GroupMembership;
        this.Goals = Goals;
        this.Badges = Badges;
        this.Activities = Activities;
        this.SocialNetworkId = Logins.get(0).getProviderKey();
        this.ImageUrl = ImageUrl;
    }

    public String getSocialNetworkId() {
        return SocialNetworkId;
    }

    public void setSocialNetworkId(String socialNetworkId) {
        SocialNetworkId = socialNetworkId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getHeightFeet() {
        return HeightFeet;
    }

    public void setHeightFeet(int heightFeet) {
        HeightFeet = heightFeet;
    }

    public int getHeightInches() {
        return HeightInches;
    }

    public void setHeightInches(int heightInches) {
        HeightInches = heightInches;
    }

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPreferredName() {
        return PreferredName;
    }

    public void setPreferredName(String preferredName) {
        PreferredName = preferredName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Date getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(Date birthDate) {
        BirthDate = birthDate;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public List<Login> getLogins() {
        return Logins;
    }

    public void setLogins(List<Login> logins) {
        Logins = logins;
    }

    public List<Group> getGroups() {
        return Groups;
    }

    public void setGroups(List<Group> groups) {
        Groups = groups;
    }

    public List<UserBadge> getBadges() {
        return Badges;
    }

    public void setBadges(List<UserBadge> badges) {
        Badges = badges;
    }

    public List<Membership> getGroupMembership() {
        return GroupMembership;
    }

    public void setGroupMemberships(List<Membership> groupMembership) {
        GroupMembership = groupMembership;
    }

    public List<Activity> getActivities() {
        return Activities;
    }

    public void setActivities(List<Activity> activities) {
        Activities = activities;
    }

    public void addGroup(Group group) {
        Groups.add(group);
    }

    public void addActivity(Activity activity) {
        Activities.add(activity);
    }

    public void setGoals(List<Goal> goals) {
        this.Goals = goals;
    }

    public List<Goal> getGoals() {
        return Goals;
    }

    public void addGoal(Goal goal) {
        Goals.add(goal);
    }

    public void removeGroup(int groupId) {
        for (int i = 0; i < Groups.size(); i++) {
            if (Groups.get(i).getId() == groupId) {
                Groups.remove(i);
            }
        }
    }

    public void addMembership(Membership membership) {
        GroupMembership.add(membership);
    }

    public boolean hasBadge(int badgeId) {
        boolean hasBadge = false;

        for (UserBadge b : Badges) {
            if (b.getBadgeId() == badgeId) {
                hasBadge = true;
            }
        }

        return hasBadge;
    }

    public void addBadge(UserBadge userBadge) {
        Badges.add(userBadge);
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FullName);
        dest.writeString(PreferredName);
        dest.writeString(Location);
        dest.writeString(Gender);
        dest.writeString(BirthDate.toString());
        dest.writeString(Email);
        dest.writeString(UserName);
        dest.writeString(SocialNetworkId);
        dest.writeString(Id);
        dest.writeInt(HeightFeet);
        dest.writeInt(HeightInches);
        dest.writeInt(Weight);
        dest.writeList(GroupMembership);
        dest.writeList(Activities);
        dest.writeList(Logins);
        dest.writeList(Groups);
        dest.writeList(Badges);
        dest.writeList(Goals);
        dest.writeString(ImageUrl);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        FullName = in.readString();
        PreferredName = in.readString();
        Location = in.readString();
        Gender = in.readString();

        try {
            BirthDate = Utility.parseDateFromJSONToDisplay(in.readString());
        } catch (ParseException e) {
            Log.e("User", e.getMessage());
        }

        Email = in.readString();
        UserName = in.readString();
        SocialNetworkId = in.readString();
        Id = in.readString();
        HeightFeet = in.readInt();
        HeightInches = in.readInt();
        Weight = in.readInt();
        GroupMembership = new ArrayList<>();
        Activities = new ArrayList<>();
        Logins = new ArrayList<>();
        Groups = new ArrayList<>();
        Badges = new ArrayList<>();
        Goals = new ArrayList<>();
        in.readList(GroupMembership, Membership.class.getClassLoader());
        in.readList(Activities, Activity.class.getClassLoader());
        in.readList(Logins, Login.class.getClassLoader());
        in.readList(Groups, Group.class.getClassLoader());
        in.readList(Badges, Badge.class.getClassLoader());
        in.readList(Goals, Goal.class.getClassLoader());
        ImageUrl = in.readString();
    }
}
