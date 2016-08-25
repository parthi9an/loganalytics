package com.metron.service.interfaces;

import org.json.JSONArray;

public interface IUserService {

    JSONArray getUsersWithRequestFilter(String status, Integer maxBytesIn, Integer minBytesIn,
            Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last, String host);

    JSONArray getUsers();
    
    Long count();

}
