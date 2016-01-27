package com.metron.event.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    
    public JSONObject getAssociatedCount(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONObject result = new JSONObject();
        JSONArray name = new JSONArray();
        JSONArray count = new JSONArray();
        try{
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                name.put(resultArr.getJSONObject(j).getString("name"));
                count.put(resultArr.getJSONObject(j).getLong("count"));
            }
            result.put("name", name);
            result.put("count", count);
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
            result.put("action_names", names);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    public void getAllEvents(String sessionId, String fromDate, String toDate) {
        String sql = "select expand(out()) from CisEvents limit 10";
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }    
    }

}
