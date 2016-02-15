package com.metron.event.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.model.CisEventMappings;

public class EnvironmentEventService extends BaseEventService{

    public JSONObject getCountOfEnv(String property, String sessionId, String serverId, String domainId,
            String source, String fromDate, String toDate) {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("type ='env'");
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

        query.append("select in."+property +" as name, count(*) as count from Metric_Event group by in."+property
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

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

}
