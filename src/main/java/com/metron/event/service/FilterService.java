package com.metron.event.service;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterService {

    HashMap<String, Object> filterProps = new HashMap<String, Object>();
    
    public FilterService() {}
    
    public FilterService(String filter) {

        try {
            JSONObject filterObj = new JSONObject(filter);
            Iterator<String> iterator = filterObj.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                filterProps.put(key, filterObj.get(key));
            }    
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }   
    
    public Object getFilterProps(String key) {
        return (this.filterProps.containsKey(key)) ? this.filterProps.get(key) : null;
    }
    
    public HashMap<String, Object> isContextTypeDialog(String key) {
        
        JSONArray obj = null;
        HashMap<String, Object> contextObj = new HashMap<String, Object>();
        try {
            obj = new JSONArray(getFilterProps(key).toString());
            for(int j = 0; j < obj.length(); j++){
                if(obj.getString(j).compareToIgnoreCase("dialog") == 0){
                    obj.remove(j);
                    contextObj.put("isContextType", true);
                    contextObj.put("ContextType", obj);
                    return contextObj;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        contextObj.put("isContextType", false);
        return contextObj;
    }
    
    public boolean isFilterPropValueEmpty(String key) {
        
        JSONArray obj = null;
        try {
            obj = new JSONArray(getFilterProps(key).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (obj.length() != 0) {
            return false;
        } else
            return true;
    }
}
