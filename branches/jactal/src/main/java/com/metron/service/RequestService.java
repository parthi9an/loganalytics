package com.metron.service;

import org.json.JSONArray;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.orientdb.OrientRest;
import com.metron.orientdb.RestUtils;
import com.metron.service.interfaces.IRequestService;

public class RequestService extends BaseService implements IRequestService {

    @Override
    public JSONArray getRequests(String timestamp) {
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        JSONArray requests = new JSONArray();
        if (timestamp != null) {
            whereClause.append("startTime >='" + timestamp + "'");
        }
        query.append("select *, OUT('Request_User').name as user from Request"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : "")
                + " Order By timestamp DESC");

        String data = new OrientRest().doSql(query.toString());
        requests = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(), null,
                true);
        return requests;
    }

    @Override
    public JSONObject search(String keyword, String hostId, String fromDate, String toDate) {
        JSONObject result = new JSONObject();

        StringBuffer reqQuery = new StringBuffer();
        QueryWhereBuffer whereReq = new QueryWhereBuffer();
        try {
            JSONArray requests = null;
            if (hostId != null) {
                whereReq.append("in='#" + hostId + "'");
            }
            if (fromDate != null) {
                whereReq.append("out.startTime >= '" + fromDate + "' ");
            }
            if (toDate != null) {
                whereReq.append("out.startTime <= '" + toDate + "' ");
            }
            if (keyword != null) {
                whereReq.append("out.sqlQuery like '%" + keyword + "%' ");
            }

            reqQuery.append("select out.@rid as id, out.@class as type, out.startTime as timestamp, out.status as status, out.endTime as message, out.sqlQuery as sqlQuery from Request_Host"
                    + ((!whereReq.toString().equals("")) ? " Where " + whereReq.toString() : "")
                    + " group by out order by out.startTime DESC");
            Long requestCount = getCount("select count(distinct(out)) as count from Request_Host"
                    + ((!whereReq.toString().equals("")) ? " Where " + whereReq.toString() : ""));
            String reqData = new OrientRest().doSql(reqQuery.toString());
            requests = (JSONArray) RestUtils.convertunFormatedToFormatedJson(reqData.toString(),
                    null, true);

            result.put("count", requestCount);

            for (int i = 0; i < requests.length(); i++) {
                requests.getJSONObject(i).put("title",
                        "Request for " + requests.getJSONObject(i).getString("sqlQuery"));
            }
            result.put("list", requests);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Long count() {
        return getCount("select count(*) as count from Request");
    }

    @Override
    public JSONObject getRequestSummary(String status, Integer maxBytesIn, Integer minBytesIn,
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

        query.append("select avg(distinct(out).delta) as avgRequestDelta,count(distinct(out)) as totalRequest ,count(distinct(out).status = 'FAIL') as failedRequest,count(distinct(out).status = 'END') as succeededRequest,count(distinct(out).status = 'CANCEL') as canceledRequest,count(distinct(out.OUTE('Request_User')[0].in)) as users,count(distinct(out.OUTE('Request_Host')[0].in)) as hosts,count(distinct(in)) as totalSession,avg(in.delta) as avgSessionDelta from Request_Session"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        String data = new OrientRest().doSql(query.toString());
        JSONObject json = (JSONObject) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, false);

        return json;
    }
}
