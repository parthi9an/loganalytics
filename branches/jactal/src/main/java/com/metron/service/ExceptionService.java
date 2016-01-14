package com.metron.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.orientdb.OrientRest;
import com.metron.orientdb.RestUtils;
import com.metron.service.interfaces.IExceptionService;
import com.metron.util.Utils;

public class ExceptionService extends BaseService implements IExceptionService {

    @Override
    public JSONObject getExceptions(String keyword, String hostId, String fromDate, String toDate) {
        // select all the exceptions matched to the keyword if exist
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        try {

            if (keyword != null) {
                whereClause.append("out.content like '%" + keyword + "%'");
            }
            if (hostId != null) {
                whereClause.append("in='#" + hostId + "'");
            }
            if (fromDate != null) {
                whereClause.append("timestamp >= '" + fromDate + "' ");
            }
            if (toDate != null) {
                whereClause.append("timestamp <= '" + toDate + "' ");
            }

            query.append("select out.@rid as id, out.heading as label, out.content as raw, count(*) as count from Exception_Host"
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : "") + " group by out.content order by count DESC");

            String data = new OrientRest().doSql(query.toString());
            JSONArray exceptions = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);
            result.put(
                    "totalrecords",
                    getCount("select count(distinct(out)) as count from Exception_Host"
                            + ((!whereClause.toString().equals("")) ? " Where "
                                    + whereClause.toString() : "")));
            result.put("exceptions", exceptions);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public JSONObject getExceptionElements(String exceptionId) {
        // TODO Auto-generated method stub
        JSONObject result = new JSONObject();

        try {
            String data = new OrientRest()
                    .doSql("select expand(OUT('Exception_ExceptionElement')) from #" + exceptionId
                            + "");
            JSONArray exceElements = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);
            for (int i = 0; i < exceElements.length(); i++) {
                JSONObject json = exceElements.getJSONObject(i);
                JSONArray edges = json.getJSONArray("in_Exception_ExceptionElement");
                json.put("count", edges.length());
            }
            result.put("results", exceElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public JSONObject getExceptionsGraph(String hostId, String fromDate, String toDate) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        try {

            Long last = null;
            if (fromDate != null) {
                whereClause.append("out.timestamp >= '" + fromDate + "' ");

                Long fromts = getTSFromDate(Utils.parseEventDate(fromDate));
                Long tots = System.currentTimeMillis() / 1000;
                if (toDate != null) {
                    tots = getTSFromDate(Utils.parseEventDate(toDate));
                }

                last = tots - fromts;
            }
            if (toDate != null) {
                whereClause.append("out.timestamp <= '" + toDate + "' ");
            }

            if (hostId != null) {
                whereClause.append("out.OUT('Exception_Host')[0].@rid ='#" + hostId + "' ");
            }

            int window = getWindow(last);

            String table = "TimeWindow" + window;
            query.append("select out.timestamp as timestamp, count(out) from Exception_"
                    + table
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : "") + " group by in");

            String data = new OrientRest().doSql(query.toString());
            JSONArray exceptions = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);
            result.put("exceptions", sortByTimestamp(exceptions));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject search(String keyword, String hostId, String fromDate, String toDate) {

        JSONObject result = new JSONObject();

        StringBuffer excQuery = new StringBuffer();
        QueryWhereBuffer whereExc = new QueryWhereBuffer();
        try {
            JSONArray exceptions = null;

            if (hostId != null) {
                whereExc.append("in='#" + hostId + "'");
            }
            if (fromDate != null) {
                whereExc.append("timestamp >= '" + fromDate + "' ");
            }
            if (toDate != null) {
                whereExc.append("timestamp <= '" + toDate + "' ");
            }
            if (keyword != null) {
                whereExc.append("out.content like '%" + keyword + "%' ");
            }

            excQuery.append("select out.@rid as id, out.@class as type, out.timestamp as timestamp, out.heading as title, out.content as message from Exception_Host"
                    + ((!whereExc.toString().equals("")) ? " Where " + whereExc.toString() : "")
                    + " group by out order by timestamp DESC");
            Long exceptionCount = getCount("select count(distinct(out)) as count from Exception_Host"
                    + ((!whereExc.toString().equals("")) ? " Where " + whereExc.toString() : ""));
            // select out.@rid as id, out.@class as type, in.timestamp as
            // timestamp,out.heading as title, out.rawData as message from
            // Exception_Event where out.OUT('Exception_Host')[0].@rid= '#19:0'

            // select out.@rid as id, out.@class as type, in.timestamp as
            // timestamp from Request_Event where
            // out.OUT('Request_Host')[0].@rid= '#19:0'
            String excData = new OrientRest().doSql(excQuery.toString());
            exceptions = (JSONArray) RestUtils.convertunFormatedToFormatedJson(excData.toString(),
                    null, true);

            result.put("count", exceptionCount);
            result.put("list", exceptions);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
