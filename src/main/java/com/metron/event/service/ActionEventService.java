package com.metron.event.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ActionEventService extends BaseEventService{
    
    public ActionEventService(String filter) {
        super(filter);
    }
    
    public ActionEventService() {}

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'action'");
    }

    public JSONObject getCountOfCommandForAction() throws JSONException {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        whereClause.append("type containstext 'action'");
        if (this.getFilterProps("actionKey") != null) {
            whereClause.append("in.key ='" + this.getFilterProps("actionKey").toString() + "'");
        } else {
            result.put("status", "Failed");
            result.put("message", "Action Key is required");
            return result;
        }
        
        if (getFilterProps("context_type") != null && ! isFilterPropValueEmpty("context_type")) {
            whereClause.append("in.context.type in " + getFilterProps("context_type"));
        }
        query.append("select count(*) as count,in.command as name from Metric_Event group by in.command"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }
    
    public JSONObject getCountOfActionKey() {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        whereClause.append("type containstext 'action'");

        if (getFilterProps("context_type") != null && ! isFilterPropValueEmpty("context_type")) {
            whereClause.append("in.context.type in " + getFilterProps("context_type"));
        }
        query.append("select count(*) as count,in.key as name from Metric_Event group by in.key"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

    /**
     * Returns Action names / action_key
     * @return
     */
    public JSONObject getActionNames() {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        whereClause.append("type containstext 'action'");

        query.append("select distinct(in.key) as name from Metric_Event"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getNames(query.toString());

        return result;
    }

}
