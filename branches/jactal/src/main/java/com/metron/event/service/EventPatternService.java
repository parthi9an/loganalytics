package com.metron.event.service;

import org.json.JSONArray;

import com.metron.controller.QueryWhereBuffer;

public class EventPatternService extends BaseEventService{

    public JSONArray getPatterns(String sessionId, String serverId, String domainId, String source,String fromDate, String toDate) {
                
        JSONArray result = new JSONArray();
          
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
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

        query.append("select in.pattern_type as pattern ,count(*) as count from Session_Pattern group by in.pattern_type order by count Desc"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getPattern(query.toString());

        return result;

    }

}
