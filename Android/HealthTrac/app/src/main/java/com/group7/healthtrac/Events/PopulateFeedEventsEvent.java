package com.group7.healthtrac.events;

import com.group7.healthtrac.models.User;

/**
 * Created by mikec_000 on 8/31/2015.
 */
public class PopulateFeedEventsEvent implements IEvent {

    private User user;

    public PopulateFeedEventsEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
