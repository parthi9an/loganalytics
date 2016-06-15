package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class DomainEventService extends BaseEventService {

    public DomainEventService(String filter) {
        super(filter);
    }
    
    public DomainEventService() {}

    public JSONObject getCountOfLoginUserByLoginType() {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();

        query.append("select count(*) as count,in.domain_type as name from Session_Domain group by in.domain_type"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    public JSONObject getDomainNames(String source, String version, String sessionId, String serverId) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        if (sessionId != null) {
            whereClause.append("session_id in " + sessionId);
        }
        if (serverId != null) {
            whereClause.append("server_id in " + serverId);
        }
        if (source != null) {
            whereClause.append("source in " + source);
        }
        if (version != null) {
            whereClause.append("version in " + version);
        }

        query.append("select distinct(user_id) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getNames(query.toString());

        return result;

    }

    public JSONObject getCountOfUsers() {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        StringBuffer subquery = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        QueryWhereBuffer subwhereClause = new QueryWhereBuffer();

        if (this.getFilterProps("source") != null && ! isFilterPropValueEmpty("source")) {
            whereClause.append("source in " + this.getFilterProps("source"));
        }
        if (this.getFilterProps("version") != null && ! isFilterPropValueEmpty("version")) {
            whereClause.append("version in " + this.getFilterProps("version"));
        }
        if (this.getFilterProps("server_id") != null && ! isFilterPropValueEmpty("server_id")) {
            whereClause.append("server_id in " + this.getFilterProps("server_id"));
        }
        if (this.getFilterProps("session_id") != null && ! isFilterPropValueEmpty("session_id")) {
            whereClause.append("session_id in " + this.getFilterProps("session_id"));
        }
        if (this.getFilterProps("fromDate") != null) {
            subwhereClause.append("timestamp >= '" + this.getFilterProps("fromDate").toString() + "' ");
        }
        if (this.getFilterProps("toDate") != null) {
            subwhereClause.append("timestamp <= '" + this.getFilterProps("toDate").toString() + "' ");
        }

        //String sql = "select user_id as name,count(*) as count from CisEvents group by user_id";
        subquery.append("select out.user_id as user_id,out.server_id as server_id,out.source as source,out.version as version, out.session_id as session_id from Metric_Event group by out"
                + ((!subwhereClause.toString().equals("")) ? " Where " + subwhereClause.toString() : ""));

        query.append("select user_id as name,count(*) as count from (").append(subquery.toString()).append(") group by user_id order by count desc"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        result = this.getAssociatedCount(query.toString());

        return result;

    }

}
