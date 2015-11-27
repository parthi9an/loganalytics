package com.metron.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.orientdb.OrientRest;
import com.metron.orientdb.RestUtils;
import com.metron.service.interfaces.IUserService;

public class UserService extends BaseService implements IUserService {

    @Override
    public JSONArray getUsersWithRequestFilter(String status, Integer maxBytesIn,
            Integer minBytesIn, Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
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
        query.append("select in.@rid as id,in.name as name,count(out) as request,count(distinct(out.OUTE('Request_Session')[0].in)) as session from Request_User"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : "")
                + " GROUP BY in ");

        String data = new OrientRest().doSql(query.toString());
        JSONArray json = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, true);

        for (int i = 0; i < json.length(); i++) {
            JSONObject jo;
            try {
                jo = json.getJSONObject(i);
                jo.put("customer", "cisco_client_" + i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    @Override
    public JSONArray getUsers() {
        String data = new OrientRest()
                .doSql("select name,IN('Session_User').size() as session,IN('Request_User').size() as request,IN('Transaction_User').size() as transaction from User");
        JSONArray json = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, true);
        return json;
    }

    @Override
    public Long count() {
        return getCount("select count(*) as count from User");
    }

}
