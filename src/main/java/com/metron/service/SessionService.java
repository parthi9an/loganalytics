package com.metron.service;

import org.json.JSONArray;

import com.metron.controller.QueryWhereBuffer;
import com.metron.orientdb.OrientRest;
import com.metron.orientdb.RestUtils;
import com.metron.service.interfaces.ISessionService;

public class SessionService extends BaseService implements ISessionService {

   
    @Override
    public JSONArray getSessions(Integer window) {
        String data = new OrientRest()
                .doSql("select startTime,endTime,IN('Request_session').size() as request,IN('Transaction_session').size() as transaction from Session ORDER By request DESC");
        JSONArray json = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, true);
        return json;
    }

    @Override
    public Long count() {
        return getCount("select count(*) as count from Session");
    }

    @Override
    public JSONArray getSessionsWithRequest(String status, Integer maxBytesIn, Integer minBytesIn,
            Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last, String host) {
        
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        if (last != null) {
            whereClause.append(" out.startTime > '" + getStartDateFromLast(last) + "' ");
        }

        if (host != null) {
            whereClause.append(" out.OUT('Request_Host')[0].@rid = #" + host + " ");
        }

        if (status != null) {
            whereClause.append(" out.status = '" + status.toUpperCase() + "' ");
        }

        if (maxBytesIn != null) {
            whereClause.append(" out.bytesIn < " + maxBytesIn);
        }

        if (maxBytesOut != null) {
            whereClause.append(" out.bytesOut < " + maxBytesOut);
        }

        if (minBytesIn != null) {
            whereClause.append(" out.bytesIn > " + minBytesIn);
        }

        if (minBytesOut != null) {
            whereClause.append(" out.bytesOut > " + minBytesOut);
        }

        if (maxRowsAffected != null) {
            whereClause.append(" out.rowsAffected < " + maxRowsAffected);
        }

        if (minRowsAffected != null) {
            whereClause.append(" out.rowsAffected > " + minRowsAffected);
        }

        query.append("select in.@rid as id,in.startTime as startTime,in.endTime as endTime,count(out) as request from Request_Session"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : "")
                + " GROUP BY in ");

        String data = new OrientRest().doSql(query.toString());
        JSONArray json = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, true);
        return json;
    }

}
