package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class SessionEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(distinct(session_id)) as count from CisEvents");
    }

    public JSONObject getSessionNames(String serverId, String userId, String source, String version) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (userId != null) {
            whereClause.append("user_id in " + userId);
        }
        if (serverId != null) {
            whereClause.append("server_id in " + serverId);
        }
        if (source != null) {
            whereClause.append("source in " + source);
        }
        if (version != null) {
            whereClause.append("version in " + version);
        }

        query.append("select distinct(session_id) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getNames(query.toString());

        return result;
    }

    public JSONObject getCountOfSessions(String serverId, String userId, String source, String version, String fromDate, String toDate) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        StringBuffer subquery = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        QueryWhereBuffer subwhereClause = new QueryWhereBuffer();
        
        if (userId != null) {
            whereClause.append("user_id in " + userId);
        }
        if (serverId != null) {
            whereClause.append("server_id in " + serverId);
        }
        if (source != null) {
            whereClause.append("source in " + source);
        }
        if (version != null) {
            whereClause.append("version in " + version);
        }
        if (fromDate != null) {
            subwhereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            subwhereClause.append("timestamp <= '" + toDate + "' ");
        }

        //String sql = "select session_id as name,count(*) as count from CisEvents group by session_id";
        subquery.append("select out.user_id as user_id,out.server_id as server_id,out.source as source,out.version as version,out.session_id as session_id from Metric_Event group by out"
                + ((!subwhereClause.toString().equals("")) ? " Where " + subwhereClause.toString() : ""));

        query.append("select session_id as name,count(*) as count from (").append(subquery.toString()).append(") group by session_id"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

}
