package com.metron.event.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.util.CisEventUtil;

public class BaseEventService {
    
    public Long getCount(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        long count = 0;
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            count = resultArr.getJSONObject(0).getLong("count");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return count;
    }
    
    //Return a json object which contains json array's with names & associated counts, suitable for pie chart reports
    public JSONObject getAssociatedCount(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONObject result = new JSONObject();
        JSONArray name = new JSONArray();
        JSONArray count = new JSONArray();
        try{
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                if(resultArr.getJSONObject(j).has("name"))
                    name.put(resultArr.getJSONObject(j).getString("name"));
                if(resultArr.getJSONObject(j).has("count"))
                    count.put(resultArr.getJSONObject(j).getLong("count"));
            }
            result.put("name", name);
            result.put("count", count);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return result;
    }
    
    //Return a json object which contains json array's with data , series & name suitable for bar chart
    public JSONObject getTotalAndAvg(String sql) {
        String query = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONObject result = new JSONObject();
        JSONArray name = new JSONArray();
        JSONArray sum = new JSONArray();
        JSONArray avg = new JSONArray();
        JSONArray data = new JSONArray();
        JSONArray series = new JSONArray();
        try{
            JSONObject jsondata = new JSONObject(query.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                name.put(resultArr.getJSONObject(j).getString("name"));
                sum.put(resultArr.getJSONObject(j).getLong("sum"));
                avg.put(resultArr.getJSONObject(j).getLong("avg"));
            }
            data.put(sum);
            data.put(avg);
            series.put("Total");
            series.put("Avg");
            result.put("name", name);
            result.put("data", data);
            result.put("series", series);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return result;
    }
    
  //Return a json object which contains json array's with names & associated counts, suitable for reports display in table 
    public JSONArray getAssociatedCounts(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONArray result = new JSONArray();
        try{
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                JSONObject eventcount = new JSONObject();
                if(resultArr.getJSONObject(j).has("name"))
                    eventcount.put("name",resultArr.getJSONObject(j).getString("name"));
                if(resultArr.getJSONObject(j).has("count"))
                    eventcount.put("count", resultArr.getJSONObject(j).getLong("count"));
                result.put(eventcount);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return result;
    }
    
    public JSONObject getNames(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONObject result = new JSONObject();
        JSONArray names = new JSONArray();
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++)
                names.put(resultArr.getJSONObject(j).getString("name"));
            result.put("names", names);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return result;
    }
    
    /**
     * Return the frequently occurred patterns
     * @param sql
     * @return
     */
    public JSONArray getPattern(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONArray result = new JSONArray();
        CisEventUtil eventUtil = new CisEventUtil();
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                JSONObject pattern = new JSONObject();
                pattern.put("pattern",resultArr.getJSONObject(j).getString("pattern"));
                pattern.put("association_count", resultArr.getJSONObject(j).getString("count"));
                pattern.put("pattern_class", eventUtil.getEventClass(resultArr.getJSONObject(j).getString("pattern")));
                result.put(pattern);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }    
        return result;
    }

    /**
     * Retrieve all the events 
     * @param sessionId
     * @param fromDate
     * @param toDate
     * @param limit
     * @param serverId 
     * @param domainId 
     * @param source 
     * @return list of events along with event type, timestamp and event details
     */
    public JSONArray getAllEvents(String sessionId, String serverId, String domainId,String source, String fromDate, String toDate, String limit) {
        
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

        query.append("select * from Metric_Event"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        String data = null;
        if (limit != null) {
            data = new com.metron.orientdb.OrientRest().doSql(query.toString(),Integer.parseInt(limit));
        }else{
            data = new com.metron.orientdb.OrientRest().doSql(query.toString());
        }
        
        CisEventUtil eventUtil = new CisEventUtil();
        JSONArray result = new JSONArray();
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                JSONObject eventobject = new JSONObject();
                eventobject.put("metric_type", resultArr.getJSONObject(j).getString("type"));
                eventobject.put("metric_timestamp", resultArr.getJSONObject(j).getString("timestamp"));
                //Retrieve Event details
                JSONObject eventdetails = eventUtil.getdetails(resultArr.getJSONObject(j).getString("in"));
                eventobject.put("event_details", eventdetails);
                result.put(eventobject);
            }    
        } catch (JSONException e1) {
            e1.printStackTrace();
        }    
        return result;
    }

    public JSONObject getEventDetails(String rid) {
        
        CisEventUtil eventUtil = new CisEventUtil();
        JSONObject eventdetails = null;
        try {
            eventdetails = eventUtil.getdetails(rid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventdetails;
    }

}
