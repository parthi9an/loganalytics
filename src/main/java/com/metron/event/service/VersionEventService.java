package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class VersionEventService extends BaseEventService {

    public JSONObject getVersionNames(String source, String serverId, String userId,
            String sessionId) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (userId != null) {
            whereClause.append("user_id in " + userId);
        }
        if (serverId != null) {
            whereClause.append("server_id in " + serverId);
        }
        if (source != null) {
            whereClause.append("source in " + source);
        }
        if (sessionId != null) {
            whereClause.append("session_id in " + sessionId);
        }

        query.append("select distinct(version) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getNames(query.toString());

        return result;
    }
}
