package com.metron.event.service;

public class SessionEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from CisEvents");
    }

}
