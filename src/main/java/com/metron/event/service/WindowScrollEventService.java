package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class WindowScrollEventService extends BaseEventService {

    public JSONObject getCountOfScrollWindows(String sessionId, String serverId, String userId,
            String source, String version, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter(sessionId,serverId,userId,source,version,fromDate,toDate);
        whereClause.append("type containstext 'scroll'");

        query.append("select count(*) as count,in.context as name from Metric_Event group by in.context"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'scroll'");
    }

}
