package com.group7.healthtrac.services.api;

import android.content.Context;

import com.group7.healthtrac.events.IEvent;

public interface IApiCaller {

    public void requestData(IEvent event);

    public void registerObject(Object object);

    public void unregisterObject(Object object);

    public void setUpServices();

    public void setContext(Context context);
}
