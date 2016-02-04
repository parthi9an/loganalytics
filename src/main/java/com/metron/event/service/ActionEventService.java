package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ActionEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where metric_type = 'type_action'");
    }

    public JSONObject getCountOfCommandForAction(String actionKey, String sessionId,
            String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("metric_type ='type_action'");
        if (actionKey != null) {
            whereClause.append("in.action_key ='" + actionKey + "'");
        }
        if (sessionId != null) {
            whereClause.append("out.metric_session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("metric_timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("metric_timestamp <= '" + toDate + "' ");
        }

        query.append("select count(*) as count,in.action_command as name from Metric_Event group by in.action_command"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }
    
    public JSONObject getCountOfActionKey( String sessionId,
            String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("metric_type ='type_action'");
        
        if (sessionId != null) {
            whereClause.append("out.metric_session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("metric_timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("metric_timestamp <= '" + toDate + "' ");
        }

        query.append("select count(*) as count,in.action_key as name from Metric_Event group by in.action_key"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    /**
     * Returns Action names / action_key
     * @param sessionId
     * @param fromDate
     * @param toDate
     * @return
     */
    public JSONObject getActionNames(String sessionId, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("metric_type ='type_action'");
        if (sessionId != null) {
            whereClause.append("out.metric_session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("metric_timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("metric_timestamp <= '" + toDate + "' ");
        }

        query.append("select distinct(in.action_key) as name from Metric_Event"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getNames(query.toString());

        return result;
    }

}
