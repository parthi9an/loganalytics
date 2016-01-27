package com.metron.event.service;

import org.json.JSONObject;

public class ViewEventService extends BaseEventService{
        
    public JSONObject getAssociatedCount(){
        return getAssociatedCount("select in.view_name as name , count(*) as count from Metric_View group by in.view_name");
    }

    

}
