package com.metron.service.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IRequestService {

    JSONArray getRequests(String timestamp);
    
    JSONObject search(String keyword, String hostId, String fromDate, String toDate);
    
    JSONObject getRequestSummary(String status, Integer maxBytesIn,
            Integer minBytesIn, Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last, String host);
    
    Long count();

}
