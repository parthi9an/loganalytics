package com.metron.service;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.orientdb.OrientRest;
import com.metron.orientdb.RestUtils;
import com.metron.service.interfaces.IHostService;
import com.metron.util.Utils;

public class HostService extends BaseService implements IHostService {

    @Override
    public JSONArray getHosts() {
        String data = new OrientRest().doSql("select @rid as id,hostname as name from Host");
        JSONArray jsonArr = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, true);
        return jsonArr;
    }

    @Override
    public JSONArray getHostsWithRequestFilter(String status, Integer maxBytesIn,
            Integer minBytesIn, Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last,  String host,String keyword, String fromDate,
            String toDate) {

        StringBuffer query = new StringBuffer();

        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        if (last != null) {
            whereClause.append(" out.startTime > '" + getStartDateFromLast(last) + "' ");
        }

        if (fromDate != null) {
            whereClause.append("out.startTime >= '" + fromDate + "' ");
        }
        if (toDate != null) {
            whereClause.append("out.startTime <= '" + toDate + "' ");
        }

        if (host != null) {
            whereClause.append("in = '#" + host + "' ");
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

        if (keyword != null) {
            whereClause.append(" out.sqlQuery like  '%" + keyword + "%'");
        }

        query.append("select in.@rid as id,in.hostname as name,count(out) as request,count(distinct(out.OUTE('Request_Session')[0].in)) as session from Request_Host"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : "")
                + " GROUP BY in ");

        String data = new OrientRest().doSql(query.toString());
        JSONArray json = (JSONArray) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, true);

        HashSet<String> hosts = new HashSet<String>();
        for (int i = 0; i < json.length(); i++) {
            try {
                hosts.add(json.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        query = new StringBuffer();
        if (keyword != null) {
            query.append("select in.@rid as id,in.hostname as name from Exception_Host where out.content like '%"
                    + keyword + "%'" + " GROUP BY in ");

            data = new OrientRest().doSql(query.toString());
            JSONArray jsonException = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);

            for (int i = 0; i < jsonException.length(); i++) {
                try {
                    if (!hosts.contains(jsonException.getJSONObject(i).getString("name"))) {
                        json.put(jsonException.getJSONObject(i));
                        jsonException.getJSONObject(i).put("sessions" ,0);
                        jsonException.getJSONObject(i).put("request" ,0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        for (int i = 0; i < json.length(); i++) {
            JSONObject jo;
            try {
                jo = json.getJSONObject(i);
                jo.put("matches", i * (new Random().nextInt(100)));
//                jo.put("customer", "cisco_client_" + i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
    @Override
    public JSONObject getHostInfo(String id) {
        String data = new OrientRest().doSql("select from #" + id + "");
        JSONObject json = (JSONObject) RestUtils.convertunFormatedToFormatedJson(data.toString(),
                null, false);
        return json;
    }

    @Override
    public JSONObject getServerStatsGraph(String hostId, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        try {

            Long last = null;
            if (fromDate != null) {
                whereClause.append("in.startTime >= '"
                        + getTSFromDate(Utils.parseEventDate(fromDate)) + "' ");
                // window = 5;

                Long fromts = getTSFromDate(Utils.parseEventDate(fromDate));
                Long tots = System.currentTimeMillis() / 1000;
                if (toDate != null) {
                    tots = getTSFromDate(Utils.parseEventDate(toDate));
                }

                last = tots - fromts;
            }
            if (toDate != null) {
                whereClause.append("in.startTime <= '"
                        + getTSFromDate(Utils.parseEventDate(toDate)) + "' ");
            }

            if (hostId != null) {
                whereClause.append(" out.OUT('HostStatus_Host')[0].@rid = #" + hostId + " ");
            }

            int window = getWindow(last);

            String table = "TimeWindow" + window;

            query.append("select out.timestamp as timestamp, avg(out.totalMemoryUsedPer) as totalMemoryUsedPercentage, "
                    + "avg(out.userCacheAccessPer) as userCacheAccessPercentage, "
                    + "avg(out.userCacheCapacityPer) as userCacheCapacityPercentage, "
                    + "avg(out.repositoryCacheAccessPer) as repositoryCacheAccessPercentage, "
                    + "avg(out.repositoryCacheCapacityPer) as repositoryCacheCapacityPercentage, "
                    + "avg(out.privilegeCacheAccessPer) as privilegeCacheAccessPerc, "
                    + "avg(out.privilegeCacheCapacityPer) as privilegeCacheCapacityPercentage from HostStatus_"
                    + table
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : "") + " group by in");

            String data = new OrientRest().doSql(query.toString());
            JSONArray statDatas = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);
            result.put("statDatas", sortByTimestamp(statDatas));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getRequestAndSessionStatus(String hostId, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        try {

            Long last = null;
            if (fromDate != null) {
                whereClause.append("in.startTime >= '"
                        + getTSFromDate(Utils.parseEventDate(fromDate)) + "' ");
                Long fromts = getTSFromDate(Utils.parseEventDate(fromDate));
                Long tots = System.currentTimeMillis() / 1000;
                if (toDate != null) {
                    tots = getTSFromDate(Utils.parseEventDate(toDate));
                }

                last = tots - fromts;
            }
            if (toDate != null) {
                whereClause.append("in.startTime <= '"
                        + getTSFromDate(Utils.parseEventDate(toDate)) + "' ");
            }
            if (hostId != null) {
                whereClause.append(" out.OUT('HostStatus_Host')[0].@rid = #" + hostId + " ");
            }
            int window = getWindow(last);
            String table = "TimeWindow" + window;
            query.append("select out.timestamp as timestamp, sum(out.totalServerRequests) as totalServerRequests, "
                    + "sum(out.totalSessions) as totalSessions, "
                    + "sum(out.totalDataSourceRequests) as totalDataSourceRequests from HostStatus_"
                    + table
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : "") + " group by in");

            String data = new OrientRest().doSql(query.toString());
            JSONArray statDatas = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);
            result.put("statDatas", sortByTimestamp(statDatas));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public JSONObject getRequestAndSession(String status, Integer maxBytesIn, Integer minBytesIn,
            Integer maxBytesOut, Integer minBytesOut, Integer minRowsAffected,
            Integer maxRowsAffected, Long last, String host) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        int window = getWindow(last);
        String table = "TimeWindow" + window;
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        try {
            if (last != null) {
                whereClause.append(" in.startTime > '" + getTsFromLast(last) + "' ");
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

            query.append("select count(*) as requestCount,count(distinct(out.OUT('Request_Session').@rid)) as sessionCount, in.startTime as startTime from Request_"
                    + table
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : "") + "  group by in");
            query.append(" Order By startTime DESC");

            String data = new OrientRest().doSql(query.toString());
            JSONArray jsonArr = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);

            JSONArray reqArr = new JSONArray();
            JSONArray sesArr = new JSONArray();
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jo;
                jo = jsonArr.getJSONObject(i);
                String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .format(new java.util.Date(jo.getLong("startTime") * 1000));
                JSONArray arr = new JSONArray();
                arr.put(dateString);
                arr.put(jo.getInt("requestCount"));
                reqArr.put(new JSONArray(arr.toString()));
                arr.put(1, jo.getInt("sessionCount"));
                sesArr.put(arr);
            }
            result.put("session", sesArr);
            result.put("request", reqArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
