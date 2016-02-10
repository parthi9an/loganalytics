package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class SourceEventService extends BaseEventService{

    public JSONObject getSourceNames(String sessionId, String domainId, String serverId) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (domainId != null) {
            whereClause.append("domain_id ='" + domainId + "'");
        }
        if (serverId != null) {
            whereClause.append("server_id ='" + serverId + "'");
        }
        if (sessionId != null) {
            whereClause.append("session_id ='" + sessionId + "'");
        }

        query.append("select distinct(source) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getNames(query.toString());

        return result;
    }

}
