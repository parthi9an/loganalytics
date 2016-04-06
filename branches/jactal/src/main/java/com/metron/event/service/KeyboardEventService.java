package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class KeyboardEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'keyb'");
    }

    public JSONObject getcommandCount(String sessionId, String serverId, String userId, String source,String version, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);
        whereClause.append("type containstext 'keyb'");

        query.append("select in.command as name, count(*) as count from Metric_Event group by in.command"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
        
    }
}
