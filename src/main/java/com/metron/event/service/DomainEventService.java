package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class DomainEventService extends BaseEventService {

    public JSONObject getCountOfLoginUserByLoginType(String sessionId, String serverId,
            String domainId, String source, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
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

        query.append("select count(*) as count,in.domain_type as name from Session_Domain group by in.domain_type"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    public JSONObject getDomainNames(String source, String sessionId, String serverId) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        if (sessionId != null) {
            whereClause.append("session_id ='" + sessionId + "'");
        }
        if (serverId != null) {
            whereClause.append("server_id ='" + serverId + "'");
        }
        if (source != null) {
            whereClause.append("source ='" + source + "'");
        }

        query.append("select distinct(domain_id) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getNames(query.toString());

        return result;

    }

    public JSONObject getCountOfUsers(String serverId, String sessionId, String source, String fromDate, String toDate) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        StringBuffer subquery = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        QueryWhereBuffer subwhereClause = new QueryWhereBuffer();

        if (sessionId != null) {
            whereClause.append("session_id ='" + sessionId + "'");
        }
        if (serverId != null) {
            whereClause.append("server_id ='" + serverId + "'");
        }
        if (source != null) {
            whereClause.append("source ='" + source + "'");
        }
        if (fromDate != null) {
            subwhereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            subwhereClause.append("timestamp <= '" + toDate + "' ");
        }

        //String sql = "select domain_id as name,count(*) as count from CisEvents group by domain_id";
        subquery.append("select out.domain_id as domain_id,out.server_id as server_id,out.source as source, out.session_id as session_id from Metric_Event group by out"
                + ((!subwhereClause.toString().equals("")) ? " Where " + subwhereClause.toString() : ""));

        query.append("select domain_id as name,count(*) as count from (").append(subquery.toString()).append(") group by domain_id"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getAssociatedCount(query.toString());

        return result;

    }

}
