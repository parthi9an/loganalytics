package com.metron.event.service;

import org.json.JSONObject;

public class SessionEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from CisEvents");
    }

    public JSONObject getSessionNames() {
        
        JSONObject result = new JSONObject();
        String query = "select distinct(metric_session_id) as name from CisEvents";
        
        result = this.getNames(query);

        return result;
    }

}
