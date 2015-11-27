package com.metron.service;

import org.json.JSONArray;

import com.metron.orientdb.OrientRest;
import com.metron.orientdb.RestUtils;
import com.metron.service.interfaces.IDomainService;

public class DomainService extends BaseService implements IDomainService {

    @Override
    public Long count() {
        return getCount("select count(*) as count from Domain");
    }

    @Override
    public JSONArray getDomains() {

        String data = new OrientRest()
                .doSql("select name,IN('Session_Domain').size() as session,IN('Request_Domain').size() as request,IN('Transaction_Domain').size() as transaction from Domain");
        JSONArray json = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, true);
        return json;
    }

}
