package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ServerEventService extends BaseEventService{

    public JSONObject getServerNames(String source, String version, String userId, String sessionId) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (userId != null) {
            whereClause.append("user_id in " + userId);
        }
        if (sessionId != null) {
            whereClause.append("session_id in " + sessionId);
        }
        if (source != null) {
            whereClause.append("source in " + source);
        }
        if (version != null) {
            whereClause.append("version in " + version);
        }

        query.append("select distinct(server_id) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getNames(query.toString());

        return result;
    }

    public long count() {
        return getCount("select count(distinct(server_id)) as count from CisEvents");
    }

}
