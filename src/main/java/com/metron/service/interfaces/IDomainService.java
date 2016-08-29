package com.metron.service.interfaces;

import org.json.JSONArray;


public interface IDomainService {

    JSONArray getDomains();
    
    Long count();

}
