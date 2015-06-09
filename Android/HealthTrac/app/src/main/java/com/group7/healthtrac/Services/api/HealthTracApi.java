package com.group7.healthtrac.services.api;

import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.Badge;
import com.group7.healthtrac.models.Challenge;
import com.group7.healthtrac.models.EndOfDayReport;
import com.group7.healthtrac.models.FeedEvent;
import com.group7.healthtrac.models.Food;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.Mood;
import com.group7.healthtrac.models.UpdateUser;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.models.UserMood;
import com.group7.healthtrac.services.utilities.GroupTuple;
import com.group7.healthtrac.services.utilities.Tuple;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface HealthTracApi {

    @GET("/api/muser/group/{id}")
    public void getUsersInGroup(@Path("id") int id, Callback<List<User>> response);

    @GET("/api/muser/{id}")
    public void getUserById(@Path("id") String id, Callback<User> response);

    @GET("/api/muser/search/{name}")
    public void searchUsers(@Path("name") String name, Callback<List<User>> response);

    @GET("/api/muser/key/{key}")
    public void getUserBySocialNetworkId(@Path("key") String key, Callback<User> result);

    @PUT("/api/muser/{id}")
    public void updateUser(@Path("id") String id, @Body UpdateUser user, Callback<User> response);

    @POST("/api/muser")
    public void createUser(@Body User user, Callback<User> response);

    @DELETE("/api/muser/{id}")
    public void deleteUser(@Path("id") String id, Callback<String> response);

    @GET("/api/muser/friends/{id}")
    public void getUserFriends(@Path("id") String id, Callback<List<User>> response);

    @GET("/api/mgroup")
    public void getAllGroups(Callback<List<Group>> response);

    @GET("/api/mgroup/{id}")
    public void getGroupById(@Path("id") int id, Callback<Group> response);

    @GET("/api/mgroup/user/{id}")
    public void getGroupsByUserId(@Path("id") String id, Callback<List<Group>> response);

    @GET("/api/mgroup/search/{name}")
    public void searchGroups(@Path("name") String name, Callback<List<Group>> response);

    @PUT("/api/mgroup/{id}")
    public void updateGroup(@Path("id") int id, @Body Group group, Callback<Group> response);

    @POST("/api/mgroup")
    public void createGroup(@Body Group group, Callback<Group> response);

    @DELETE("/api/mgroup/{id}")
    public void deleteGroup(@Path("id") int id, Callback<Group> response);

    @GET("/api/mgroup/leaderboard/{groupId}/{category}")
    public void obtainLeaderBoard(@Path("groupId") int id, @Path("category") String category, Callback<List<Tuple>> response);

    @GET("/api/mgroup/invites/{userId}")
    public void obtainUserInvites(@Path("userId") String userId, Callback<List<Group>> response);

    @PUT("/api/mmembership/{id}")
    public void updateMembership(@Path("id") int id, @Body Membership membership, Callback<Membership> response);

    @POST("/api/mmembership")
    public void createMembership(@Body Membership membership, Callback<Membership> response);

    @DELETE("/api/mmembership/{id}")
    public void deleteMembership(@Path("id") int id, Callback<Membership> response);

    @GET("/api/mactivity/{id}")
    public void getActivityById(@Path("id") int id, Callback<Activity> response);

    @POST("/api/mactivity")
    public void createActivity(@Body Activity activity, Callback<Activity> response);

    @POST("/api/mactivity/classify")
    public void classifyActivity(@Body Activity activity, Callback<Integer> response);

    @GET("/api/mactivity/{id}/{date}")
    public void getDaysActivities(@Path("id") String id, @Path("date") String date, Callback<List<Activity>> response);

    @GET("/api/mbadge/{id}")
    public void getBadgeById(@Path("id") int id, Callback<Badge> response);

    @POST("/api/muserbadge")
    public void createUserBadge(@Body UserBadge userBadge, Callback<UserBadge> response);

    @GET("/api/muserbadge/{id}")
    public void getUserBadge(@Path("id") int id, Callback<UserBadge> response);

    @GET("/api/muserbadge/user/{userId}")
    public void getUserBadges(@Path("userId") String userId, Callback<List<Badge>> response);

    @GET("/api/mfeedevent/user/{id}")
    public void getFeedEventsByUserId(@Path("id") String id, Callback<List<FeedEvent>> response);

    @GET("/api/mfeedevent/group/{id}")
    public void getFeedEventsByGroupId(@Path("id") int id, Callback<GroupTuple> response);

    @POST("/api/mgoal")
    public void createGoal(@Body Goal goal, Callback<Goal> response);

    @GET("/api/mgoal/{id}")
    public void getGoal(@Path("id") int id, Callback<Goal> response);

    @GET("/api/mgoal/user/{id}")
    public void getUserGoals(@Path("id") String id, Callback<List<Goal>> response);

    @GET("/api/mmood/{id}")
    public void getMoodById(@Path("id") int id, Callback<Mood> response);

    @GET("/api/mmood")
    public void getAllMoods(Callback<List<Mood>> response);

    @POST("/api/musermood")
    public void createUserMood(@Body UserMood userMood, Callback<UserMood> response);

    @GET("/api/musermood/{id}")
    public void getUserMood(@Path("id") int id, Callback<UserMood> response);

    @POST("/api/mfood")
    public void createFood(@Body Food food, Callback<Food> response);

    @GET("/api/mfood/{id}")
    public void getFood(@Path("id") int id, Callback<Food> response);

    @GET("/api/mendofdayreport/{id}")
    public void getEndOfDayReportById(@Path("id") int id, Callback<EndOfDayReport> response);

    @GET("/api/mchallenge/user/{userId}")
    public void getUserChallenges(@Path("userId") String userId, Callback<List<Challenge>> response);

    @GET("/api/mchallenge/challenger/{userId}")
    public void getChallengerChallenges(@Path("userId") String userId, Callback<List<Challenge>> response);

    @GET("/api/mchallenge/friend/{userId}")
    public void getFriendChallenges(@Path("userId") String userId, Callback<List<Challenge>> response);

    @POST("/api/mchallenge")
    public void createChallenge(@Body Challenge challenge, Callback<Challenge> response);

    @PUT("/api/mchallenge/{challengeId}")
    public void updateChallenge(@Path("challengeId") int challengeId, @Body Challenge challenge, Callback<Challenge> response);

    @DELETE("/api/mchallenge/{challengeId}")
    public void deleteChallenge(@Path("challengeId") int challengeId, Callback<Challenge> response);
}
