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
        String name;
        long count = 0;
        try{
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            for(int j = 0; j < resultArr.length(); j++){
                name = resultArr.getJSONObject(j).getString("name");
                count = resultArr.getJSONObject(j).getLong("count");
                result.put(name, count);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return result;
    }

}
