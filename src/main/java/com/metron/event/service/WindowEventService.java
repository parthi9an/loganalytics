package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class WindowEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type = 'window'");
    }

    public JSONObject getCountOfMovedWindows(String sessionId, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type ='window'");
        
        if (sessionId != null) {
            whereClause.append("out.session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("timestamp <= '" + toDate + "' ");
        }

        query.append("select count(*) as count,in.window_view as name from Metric_Event group by in.window_view"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

}
