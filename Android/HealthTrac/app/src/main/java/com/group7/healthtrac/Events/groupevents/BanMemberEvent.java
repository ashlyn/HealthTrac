package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Josh on 4/2/2015.
 */
public class BanMemberEvent implements IEvent {
    private int mPosition;

    public BanMemberEvent(int position){
        mPosition = position;
    }

    public int getPosition(){
        return mPosition;
    }
}
