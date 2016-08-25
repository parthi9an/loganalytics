package com.metron.service.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IHostService {

    JSONArray getHosts();

    JSONArray getHostsWithRequestFilter(String status, Integer maxBytesIn, Integer minBytesIn,
            Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last, String host,String keyword, String fromDate, String toDate);

    JSONObject getHostInfo(String id);
    
    JSONObject getServerStatsGraph(String hostId, String fromDate, String toDate);
    
    JSONObject getRequestAndSession(String status, Integer maxBytesIn, Integer minBytesIn,
            Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last, String host);
    
    JSONObject getRequestAndSessionStatus(String hostId, String fromDate, String toDate);

}
