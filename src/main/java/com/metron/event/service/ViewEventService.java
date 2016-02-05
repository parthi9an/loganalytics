package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ViewEventService extends BaseEventService {
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type = 'view'");
    }

    public JSONObject getAssociatedCount() {
        return getAssociatedCount("select in.view_name as name , count(*) as count from Metric_View group by in.view_name");
    }

    public JSONObject getViewcount(String sessionId, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type ='view'");
        if (sessionId != null) {
            whereClause.append("out.session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("timestamp <= '" + toDate + "' ");
        }

        query.append("select in.view_name as name , count(*) as count from Metric_Event group by in.view_name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;

    }

    public JSONObject getViewActivityDuration(String sessionId, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type ='view'");
        whereClause.append("in.view_event_type ='view_close'");
        if (sessionId != null) {
            whereClause.append("out.session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("timestamp <= '" + toDate + "' ");
        }
        
        query.append("select sum(viewActiveTime) as sum,avg(viewActiveTime) as avg,in.view_name as name from Metric_Event group by in.view_name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getTotalAndAvg(query.toString());

        return result;
    }

}
