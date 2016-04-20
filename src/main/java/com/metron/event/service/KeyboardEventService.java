package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class KeyboardEventService extends BaseEventService{
    
    public KeyboardEventService(String filter) {
        super(filter);
    }

    public KeyboardEventService() {}
    
    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'keyb'");
    }

    public JSONObject getcommandCount() {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        whereClause.append("type containstext 'keyb'");

        if (getFilterProps("context_type") != null && ! isFilterPropValueEmpty("context_type")) {
            whereClause.append("in.context.type in " + getFilterProps("context_type"));
        }
        query.append("select in.command as name, count(*) as count from Metric_Event group by in.command"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
        
    }
}
