package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Courtney on 4/26/2015.
 */
public class Challenge implements Parcelable {

    private int Id;
    private String ChallengerId;
    private int ChallengerGoalId;
    private String FriendId;
    private int FriendGoalId;
    private Goal ChallengerGoal;
    private Goal FriendGoal;
    private boolean Accepted;

    public Challenge(String challengerId, int challengerGoalId, String friendId, boolean accepted) {
        ChallengerId = challengerId;
        ChallengerGoalId = challengerGoalId;
        FriendId = friendId;
        Accepted = accepted;
    }

    public Challenge(int id, String challengerId, int challengerGoalId, String friendId, int friendGoalId, Goal challengerGoal, Goal friendGoal, boolean accepted) {
        Id = id;
        ChallengerId = challengerId;
        ChallengerGoalId = challengerGoalId;
        FriendId = friendId;
        FriendGoalId = friendGoalId;
        ChallengerGoal = challengerGoal;
        FriendGoal = friendGoal;
        Accepted = accepted;
    }

    public Challenge(String challengerId, int challengerGoalId, String friendId, int friendGoalId, Goal challengerGoal, Goal friendGoal, boolean accepted) {
        ChallengerId = challengerId;
        ChallengerGoalId = challengerGoalId;
        FriendId = friendId;
        FriendGoalId = friendGoalId;
        ChallengerGoal = challengerGoal;
        FriendGoal = friendGoal;
        Accepted = accepted;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getChallengerId() {
        return ChallengerId;
    }

    public void setChallengerId(String challengerId) {
        ChallengerId = challengerId;
    }

    public int getChallengerGoalId() {
        return ChallengerGoalId;
    }

    public void setChallengerGoalId(int challengerGoalId) {
        ChallengerGoalId = challengerGoalId;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public int getFriendGoalId() {
        return FriendGoalId;
    }

    public void setFriendGoalId(int friendGoalId) {
        FriendGoalId = friendGoalId;
    }

    public boolean isAccepted() {
        return Accepted;
    }

    public void setAccepted(boolean accepted) {
        Accepted = accepted;
    }

    public Goal getChallengerGoal() {
        return ChallengerGoal;
    }

    public void setChallengerGoal(Goal challengerGoal) {
        ChallengerGoal = challengerGoal;
    }

    public Goal getFriendGoal() {
        return FriendGoal;
    }

    public void setFriendGoal(Goal friendGoal) {
        FriendGoal = friendGoal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(ChallengerId);
        dest.writeInt(ChallengerGoalId);
        dest.writeString(FriendId);
        dest.writeInt(FriendGoalId);
        dest.writeByte((byte)(Accepted ? 1 : 0));
        dest.writeParcelable(ChallengerGoal, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(FriendGoal, PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Parcelable.Creator<Challenge> CREATOR = new Parcelable.Creator<Challenge>() {
        @Override
        public Challenge createFromParcel(Parcel source) {
            return new Challenge(source);
        }

        @Override
        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };

    private Challenge(Parcel in) {
        Id = in.readInt();
        ChallengerId = in.readString();
        ChallengerGoalId = in.readInt();
        FriendId = in.readString();
        FriendGoalId = in.readInt();
        Accepted = in.readByte() != 0;
        ChallengerGoal = in.readParcelable(Goal.class.getClassLoader());
        FriendGoal = in.readParcelable(Goal.class.getClassLoader());
    }
}
