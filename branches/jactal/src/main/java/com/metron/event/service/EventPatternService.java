package com.metron.event.service;

import org.json.JSONArray;

import com.metron.controller.QueryWhereBuffer;

public class EventPatternService extends BaseEventService{

    public JSONArray getPatterns(String sessionId, String serverId, String userId, String source,String version,String fromDate, String toDate) {
                
        JSONArray result = new JSONArray();
          
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);

        query.append("select in.pattern_type as pattern ,count(*) as count from Session_Pattern group by in.pattern_type order by count Desc"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getPattern(query.toString());

        return result;

    }

}
