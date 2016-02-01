package com.metron.event.service;

import org.json.JSONArray;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ErrorEventService extends BaseEventService {

    public JSONArray getExceptionCount(String sessionId, String fromDate, String toDate) {
        
        JSONArray result = new JSONArray();
            StringBuffer query = new StringBuffer();
            QueryWhereBuffer whereClause = new QueryWhereBuffer();
            whereClause.append("metric_type ='type_error'");
            if (sessionId != null) {
                whereClause.append("out.metric_session_id ='" + sessionId + "'");
            }
            if (fromDate != null) {
                whereClause.append("metric_timestamp >= '" + fromDate + "' ");
            }
            if (toDate != null) {
                whereClause.append("metric_timestamp <= '" + toDate + "' ");
            }

            query.append("select in.error_type as name, count(*) as count from Metric_Event group by in.error_type"
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : ""));

            result = this.getAssociatedCounts(query.toString());

        return result;
    }

    public JSONArray getPatterns(String errorType, String sessionId, String fromDate, String toDate) {
        
        //String sql = "select pattern_type as pattern ,association_count as count from ErrorPattern order by association_count DESC";
        
        JSONArray result = new JSONArray();
        
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (errorType != null) {
            whereClause.append("error_type ='" + errorType + "'");
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

        query.append("select pattern_type as pattern ,association_count as count from ErrorPattern order by association_count DESC"
                + ((!whereClause.toString().equals(""))
                        ? " Where " + whereClause.toString()
                        : ""));
        
        result = this.getPattern(query.toString());

        return result;
    }

}
