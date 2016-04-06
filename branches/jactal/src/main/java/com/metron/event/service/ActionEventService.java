package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ActionEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'action'");
    }

    public JSONObject getCountOfCommandForAction(String actionKey, String sessionId,String serverId, String userId, String source,
            String version, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);
        whereClause.append("type containstext 'action'");
        if (actionKey != null) {
            whereClause.append("in.key ='" + actionKey + "'");
        }

        query.append("select count(*) as count,in.command as name from Metric_Event group by in.command"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }
    
    public JSONObject getCountOfActionKey( String sessionId,String serverId, String userId, String source,
            String version, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);
        whereClause.append("type containstext 'action'");

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
    public JSONObject getActionNames(String sessionId, String serverId, String userId, String source, String version, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);
        whereClause.append("type containstext 'action'");

        query.append("select distinct(in.key) as name from Metric_Event"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getNames(query.toString());

        return result;
    }

}
