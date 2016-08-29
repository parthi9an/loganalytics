package com.metron.event.service;


public class FieldEventService extends BaseEventService{

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'field'");
    }

}
