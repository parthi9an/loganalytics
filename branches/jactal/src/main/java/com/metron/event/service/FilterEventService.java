package com.metron.event.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.model.FilterCritera;
import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public class FilterEventService extends BaseEventService {

    public JSONObject saveFilterCriteria(String filter) {

        JSONObject result = new JSONObject();
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        try {
            JSONObject filterObj = new JSONObject(filter);
            HashMap<String, Object> filterProps = new HashMap<String, Object>();
            Iterator<String> iterator = filterObj.keys();

            while (iterator.hasNext()) {
                String key2 = iterator.next();
                filterProps.put(key2, filterObj.getString(key2));
            }
            filterProps.put("timestamp", new Date().getTime());

            if (new FilterCritera(graph).filterExists(filterProps.get("filtername"),
                    filterProps.get("uName")) != null) {
                result.put("status", "Failed");
                result.put("message", "FilterName Exists");
            } else {
                new FilterCritera(filterProps, graph);
                result.put("status", "Success");
                result.put("message", "Successfully saved");
            }
        } catch (Exception e) {
            try {
                result.put("status", "Failed");
                result.put("message", e.toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        graph.shutdown();
        return result;
    }

    public JSONObject getSavedFilterCriteria(String uName, String limit) {

        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        StringBuffer countquery = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("uName ='" + uName + "'");

        query.append("select * from FilterCriteria order by timestamp desc"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        String data = null;
        if (limit != null) {
            data = new com.metron.orientdb.OrientRest().doSql(query.toString(),
                    Integer.parseInt(limit));
        } else {
            data = new com.metron.orientdb.OrientRest().doSql(query.toString());
        }
        try {
            result = new JSONObject(data.toString());
            // send count of filters the user has
            countquery
                    .append("select count(*) as count from FilterCriteria order by timestamp desc"
                            + ((!whereClause.toString().equals("")) ? " Where "
                                    + whereClause.toString() : ""));
            Long totalFilterCountOfUser = this.getCount(countquery.toString());
            result.put("totalFilterCount", totalFilterCountOfUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public JSONObject deleteAllFilters(String uName) {

        JSONObject result = new JSONObject();
        try {
            new com.metron.orientdb.OrientRest().postSql("delete vertex from FilterCriteria where uName ='"+uName+"'");
            result.put("status", "Success");
            result.put("message", "Successfully deleted all the filters");
        } catch (Exception e) {
            try {
                result.put("status", "Failed");
                result.put("message", e);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }
}
