package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class KeyboardEventService extends BaseEventService{

    public JSONObject getcommandCount(String sessionId, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        if (sessionId != null) {
            whereClause.append("out.metric_session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("metric_timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("metric_timestamp <= '" + toDate + "' ");
        }

        query.append("select in.key_command as name, count(*) as count from Metric_Keyboard group by in.key_command"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
        
    }
}
