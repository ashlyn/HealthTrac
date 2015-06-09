package com.group7.healthtrac.events.membershipevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Membership;

/**
 * Created by Josh on 4/2/2015.
 */
public class UpdateMembershipEvent implements IEvent{
    private Membership mMembership;
    private String mMessage;

    public UpdateMembershipEvent(Membership membership, String message){
        mMembership = membership;
        mMessage = message;
    }

    public Membership getMembership(){
        return mMembership;
    }

    public String getMessage() {
        return mMessage;
    }
}
