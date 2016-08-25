package com.metron.event.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.model.CisEventMappings;

public class EnvironmentEventService extends BaseEventService{

    public EnvironmentEventService(String filter) {
        super(filter);
    }
    
    public EnvironmentEventService() {}

    public JSONObject getCountOfEnv() throws JSONException {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        whereClause.append("type containstext 'env'");

        if (this.getFilterProps("property") != null) {
            String property = this.getFilterProps("property").toString();
            query.append("select in."+property +" as name, count(*) as count from Metric_Event group by in."+property
                    + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

            result = this.getAssociatedCount(query.toString());
        } else {
            result.put("status", "Failed");
            result.put("message", "Choose a env property");
        }
        
        return result;
    }

    public JSONObject getEnvProperties() {

        JSONObject result = new JSONObject();
        try {
            List<String> keys = CisEventMappings.getInstance().getEventMapping("EnvironmentEvent");
            JSONArray props = new JSONArray();
            for (String key : keys) {
                props.put(key);
            }

            result.put("names", props);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'env'");
    }

}
