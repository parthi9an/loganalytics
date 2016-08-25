package com.metron.event.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ErrorEventService extends BaseEventService {

    public ErrorEventService(String filter) {
        super(filter);
    }
    
    public ErrorEventService() {}

    public JSONArray getExceptionCount() {
        
        JSONArray result = new JSONArray();
            StringBuffer query = new StringBuffer();
            QueryWhereBuffer whereClause = this.edgeFilter();
            whereClause.append("type containstext 'error'");

            query.append("select in.message as message,in.trace as trace,in.error_trace_checksum as checksum, count(*) as count from Metric_Event group by in.error_trace_checksum"
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : ""));

            result = this.getErrorCounts(query.toString());

        return result;
    }

    public JSONArray getPatterns() {
        
        //String sql = "select pattern_type as pattern ,association_count as count from ErrorPattern order by association_count DESC";
        
        JSONArray result = new JSONArray();
        
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        
        if (this.getFilterProps("errorTracechecksum") != null) {
            whereClause.append("in.error_trace_checksum containstext '" + this.getFilterProps("errorTracechecksum").toString() + "'");
        } else {
            return result.put("Failed");
        }

        query.append("select in.pattern_type as pattern ,count(*) as count from Session_ErrorPattern group by in.pattern_type order by count Desc"
                + ((!whereClause.toString().equals(""))
                        ? " Where " + whereClause.toString()
                        : ""));
        
        result = this.getPattern(query.toString());

        return result;
    }
    
    public JSONArray getErrorCounts(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONArray result = new JSONArray();
        try{
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                JSONObject eventcount = new JSONObject();
                eventcount.put("message",resultArr.getJSONObject(j).getString("message"));
                eventcount.put("trace", resultArr.getJSONObject(j).getString("trace"));
                eventcount.put("checksum",resultArr.getJSONObject(j).getString("checksum"));
                eventcount.put("count", resultArr.getJSONObject(j).getLong("count"));
                result.put(eventcount);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'error'");
    }

}
