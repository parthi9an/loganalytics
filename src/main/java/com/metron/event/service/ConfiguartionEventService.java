package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ConfiguartionEventService extends BaseEventService{

    public ConfiguartionEventService(String filter) {
        super(filter);
    }

    public ConfiguartionEventService() {}

    public JSONObject getOverridenConfigCount() {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        whereClause.append("type containstext 'config'");

        query.append("select count(*) as count,in.name as name from Metric_Event group by in.name"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'config'");
    }

}
