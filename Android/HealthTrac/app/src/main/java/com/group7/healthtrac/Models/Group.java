package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Group implements Parcelable {

    private List<Membership> GroupMembers = new ArrayList<>();
    private List<User> LeaderBoard = new ArrayList<>();
    private int Id;
    private String GroupName;
    private String Description;
    private String ImageUrl;

    public Group(List<Membership> groupMembers, List<User> leaderBoard, int id, String groupName, String description, String imageUrl) {
        GroupMembers = groupMembers;
        LeaderBoard = leaderBoard;
        Id = id;
        GroupName = groupName;
        Description = description;
        ImageUrl = imageUrl;
    }

    public Group(String groupName, String groupDescription, String imageUrl) {
        this.GroupName = groupName;
        this.Description = groupDescription;
        this.ImageUrl = imageUrl;
        GroupMembers = new ArrayList<>();
        LeaderBoard = new ArrayList<>();
    }

    public Group(Group group){
        this.GroupName = group.getGroupName();
        this.Description = group.getDescription();
        this.GroupMembers = group.getGroupMembers();
        this.LeaderBoard = group.getLeaderBoard();
        this.Id = group.getId();
        this.ImageUrl = group.getImageUrl();
    }

    public List<Membership> getGroupMembers() {
        return GroupMembers;
    }

    public void setGroupMembers(List<Membership> GroupMembers) {
        this.GroupMembers = GroupMembers;
    }

    public List<User> getLeaderBoard() {
        return LeaderBoard;
    }

    public void setLeaderBoard(List<User> LeaderBoard) {
        this.LeaderBoard = LeaderBoard;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void addGroupMember(Membership membership) {
        GroupMembers.add(membership);
    }

    public int getStatusInGroup(String userId) {
        int position = -1;

        for (Membership m : GroupMembers) {
            if (m.getUserId().equals(userId)) {
                position = m.getStatus();
            }
        }

        return position;
    }

    public Membership getMembershipOfUser(String userId) {
        Membership membership = null;

        for (Membership m : GroupMembers) {
            if (m.getUserId().equals(userId)) {
                membership = m;
            }
        }

        return membership;
    }

    public void removeMembership(String userId) {
        for (int i = 0; i < GroupMembers.size(); i++) {
            if (GroupMembers.get(i).getUserId().equals(userId)) {
                GroupMembers.remove(i);
            }
        }
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
        dest.writeString(GroupName);
        dest.writeString(Description);
        dest.writeInt(Id);
        dest.writeList(GroupMembers);
        dest.writeList(LeaderBoard);
        dest.writeString(ImageUrl);
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    private Group(Parcel in) {
        GroupName = in.readString();
        Description = in.readString();
        Id = in.readInt();
        GroupMembers = new ArrayList<>();
        LeaderBoard = new ArrayList<>();
        in.readList(GroupMembers, Membership.class.getClassLoader());
        in.readList(LeaderBoard, User.class.getClassLoader());
        ImageUrl = in.readString();
    }
}