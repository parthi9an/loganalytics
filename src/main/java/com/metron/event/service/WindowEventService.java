package com.metron.event.service;

public class WindowEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type = 'window'");
    }

}
