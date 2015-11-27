package com.metron.service.interfaces;

import org.json.JSONArray;

public interface ISessionService {

    JSONArray getSessions(Integer window);
    
    Long count();
    
    JSONArray getSessionsWithRequest(String status, Integer maxBytesIn, Integer minBytesIn,
            Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last, String host);

}
