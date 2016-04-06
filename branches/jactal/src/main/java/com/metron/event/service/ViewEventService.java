package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ViewEventService extends BaseEventService {
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'view'");
    }

    public JSONObject getAssociatedCount() {
        return getAssociatedCount("select in.name as name , count(*) as count from Metric_View group by in.name");
    }

    public JSONObject getViewcount(String sessionId,String serverId, String userId, String source, String version, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);
        whereClause.append("type containstext 'view'");

        query.append("select in.name as name , count(*) as count from Metric_Event group by in.name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;

    }

    public JSONObject getViewActivityDuration(String sessionId,String serverId, String userId, String source,String version, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);
        whereClause.append("type containstext 'view'");
        whereClause.append("in.event containstext 'close'");
        
        query.append("select sum(viewActiveTime) as sum,avg(viewActiveTime) as avg,in.name as name from Metric_Event group by in.name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getTotalAndAvg(query.toString());

        return result;
    }

}
