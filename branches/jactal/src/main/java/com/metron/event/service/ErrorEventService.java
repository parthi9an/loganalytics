package com.metron.event.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class ErrorEventService extends BaseEventService {

    public JSONArray getExceptionCount(String sessionId, String fromDate, String toDate) {
        
        JSONArray result = new JSONArray();
            StringBuffer query = new StringBuffer();
            QueryWhereBuffer whereClause = new QueryWhereBuffer();
            whereClause.append("type ='error'");
            if (sessionId != null) {
                whereClause.append("out.session_id ='" + sessionId + "'");
            }
            if (fromDate != null) {
                whereClause.append("timestamp >= '" + fromDate + "' ");
            }
            if (toDate != null) {
                whereClause.append("timestamp <= '" + toDate + "' ");
            }

            query.append("select in.error_message as message,in.error_trace as trace,in.error_trace_checksum as checksum, count(*) as count from Metric_Event group by in.error_trace_checksum"
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : ""));

            result = this.getErrorCounts(query.toString());

        return result;
    }

    public JSONArray getPatterns(String errorTracechecksum, String sessionId, String fromDate, String toDate) {
        
        //String sql = "select pattern_type as pattern ,association_count as count from ErrorPattern order by association_count DESC";
        
        JSONArray result = new JSONArray();
        
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (errorTracechecksum != null) {
            whereClause.append("error_trace_checksum ='" + errorTracechecksum + "'");
        }
        if (sessionId != null) {
            whereClause.append("out.session_id ='" + sessionId + "'");
        }
        if (fromDate != null) {
            whereClause.append("timestamp >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("timestamp <= '" + toDate + "' ");
        }

        query.append("select pattern_type as pattern ,association_count as count from ErrorPattern order by count DESC"
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

}
