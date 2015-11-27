package com.metron.service.interfaces;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IBaseService {
    
    JSONObject getEvents(String timestamp, String hostId, String getPage);
    
    JSONArray sortByTimestamp(JSONArray json);

    Long getCount(String sql);
    
    Long getTSFromDate(Date date);
    
    Long getTsFromLast(Long last);
    
    String getStartDateFromLast(Long last);
    
    String changeDateBySeconds(Date date, int seconds);
    
    int getWindow(Long last);
    
    

}
