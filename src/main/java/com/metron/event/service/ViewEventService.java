package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ViewEventService extends BaseEventService {

    public JSONObject getAssociatedCount() {
        return getAssociatedCount("select in.view_name as name , count(*) as count from Metric_View group by in.view_name");
    }

    public JSONObject getViewcount(String sessionId, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("metric_type ='type_view'");
        if (sessionId != null) {
            whereClause.append("out.metric_session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("metric_timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("metric_timestamp <= '" + toDate + "' ");
        }

        query.append("select in.view_name as name , count(*) as count from Metric_Event group by in.view_name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;

    }

}
