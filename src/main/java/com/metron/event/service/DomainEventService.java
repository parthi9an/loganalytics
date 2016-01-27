package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class DomainEventService extends BaseEventService {

    public JSONObject getCountOfLoginUserByLoginType(String sessionId, String fromDate,
            String toDate) {

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

        query.append("select count(*) as count,in.domain_type as name from Metric_Domain group by in.domain_type"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

}
