package com.group7.healthtrac.events.membershipevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Membership;

/**
 * Created by Mike C on 2/20/2015.
 */
public class MembershipCreatedEvent implements IEvent {

    private Membership mMembership;

    public MembershipCreatedEvent (Membership membership) {
        mMembership = membership;
    }

    public Membership getMembership() {
        return mMembership;
    }
}
