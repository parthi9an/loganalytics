package com.metron.event.service;

import org.json.JSONArray;

public class EventPatternService extends BaseEventService{

    public JSONArray getPatterns(String sessionId, String fromDate, String toDate) {
        
        String sql = "select pattern_type as pattern ,association_count as count from Pattern order by association_count DESC";
        
        JSONArray result = new JSONArray();
        
        result = this.getPattern(sql);

        return result;
    }

}
