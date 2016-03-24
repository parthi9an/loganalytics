package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ConfiguartionEventService extends BaseEventService{

    public JSONObject getOverridenConfigCount(String sessionId,String serverId, String userId, String source, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type containstext 'config'");
        if (sessionId != null) {
            whereClause.append("out.session_id in " + sessionId);
        }
        if (userId != null) {
            whereClause.append("out.user_id in " + userId);
        }
        if (serverId != null) {
            whereClause.append("out.server_id in " + serverId);
        }
        if (source != null) {
            whereClause.append("out.source in " + source);
        }
        if (fromDate != null) {
            whereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("timestamp <= '" + toDate + "' ");
        }

        query.append("select count(*) as count,in.name as name from Metric_Event group by in.name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'config'");
    }

}
