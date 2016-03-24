package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ActionEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'action'");
    }

    public JSONObject getCountOfCommandForAction(String actionKey, String sessionId,String serverId, String userId, String source,
            String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type containstext 'action'");
        if (actionKey != null) {
            whereClause.append("in.key ='" + actionKey + "'");
        }
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

        query.append("select count(*) as count,in.command as name from Metric_Event group by in.command"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }
    
    public JSONObject getCountOfActionKey( String sessionId,String serverId, String userId, String source,
            String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type containstext 'action'");
        
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

        query.append("select count(*) as count,in.key as name from Metric_Event group by in.key"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    /**
     * Returns Action names / action_key
     * @param sessionId
     * @param fromDate
     * @param toDate
     * @param toDate 
     * @param fromDate 
     * @param source 
     * @return
     */
    public JSONObject getActionNames(String sessionId, String serverId, String userId, String source, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type containstext 'action'");
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

        query.append("select distinct(in.key) as name from Metric_Event"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getNames(query.toString());

        return result;
    }

}
