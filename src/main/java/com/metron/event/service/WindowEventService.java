package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class WindowEventService extends BaseEventService{
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type = 'window'");
    }

    public JSONObject getCountOfMovedWindows(String sessionId,String serverId, String domainId, String source, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type ='window'");
        
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

        query.append("select count(*) as count,in.window_view as name from Metric_Event group by in.window_view"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

}
