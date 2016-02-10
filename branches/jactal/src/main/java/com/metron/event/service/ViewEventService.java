package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ViewEventService extends BaseEventService {
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type = 'view'");
    }

    public JSONObject getAssociatedCount() {
        return getAssociatedCount("select in.name as name , count(*) as count from Metric_View group by in.name");
    }

    public JSONObject getViewcount(String sessionId,String serverId, String domainId, String source, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type ='view'");
        if (sessionId != null) {
            whereClause.append("out.session_id ='" + sessionId + "'");
        }
        if (domainId != null) {
            whereClause.append("out.domain_id ='" + domainId + "'");
        }
        if (serverId != null) {
            whereClause.append("out.server_id ='" + serverId + "'");
        }
        if (source != null) {
            whereClause.append("out.source ='" + source + "'");
        }
        if (fromDate != null) {
            whereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("timestamp <= '" + toDate + "' ");
        }

        query.append("select in.name as name , count(*) as count from Metric_Event group by in.name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;

    }

    public JSONObject getViewActivityDuration(String sessionId,String serverId, String domainId, String source, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type ='view'");
        whereClause.append("in.event ='close'");
        if (sessionId != null) {
            whereClause.append("out.session_id ='" + sessionId + "'");
        }
        if (domainId != null) {
            whereClause.append("out.domain_id ='" + domainId + "'");
        }
        if (serverId != null) {
            whereClause.append("out.server_id ='" + serverId + "'");
        }
        if (source != null) {
            whereClause.append("out.source ='" + source + "'");
        }
        if (fromDate != null) {
            whereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("timestamp <= '" + toDate + "' ");
        }
        
        query.append("select sum(viewActiveTime) as sum,avg(viewActiveTime) as avg,in.name as name from Metric_Event group by in.name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getTotalAndAvg(query.toString());

        return result;
    }

}
