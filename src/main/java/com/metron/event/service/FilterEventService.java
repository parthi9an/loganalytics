package com.metron.event.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.model.FilterCritera;
import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public class FilterEventService extends BaseEventService {

    public FilterEventService(String filter) {
        super(filter);
    }
    
    public FilterEventService() {}

    public JSONObject saveFilterCriteria(String filter) {

        JSONObject result = new JSONObject();
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        try {
            JSONObject filterObj = new JSONObject(filter);
            HashMap<String, Object> filterProps = new HashMap<String, Object>();
            Iterator<String> iterator = filterObj.keys();

            while (iterator.hasNext()) {
                String key2 = iterator.next();
                filterProps.put(key2, filterObj.get(key2).toString());
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

    public JSONObject getFilters() {
        
        JSONObject result = new JSONObject();
        try {
            result.put("source", getSourceList());
            result.put("version", getVersionList());
            result.put("server_id", getServerList());
            result.put("user_id", getUserList());
            result.put("session_id", getSessionList());
            result.put("fromDate", new Date().getTime());
            result.put("toDate", new Date().getTime());
            if(getFilterProps("isContextType") != null && (Boolean) getFilterProps("isContextType"))
                result.put("context_type", getContextTypeList());
            //result.put("selectedFilters", new JSONObject(this.filterProps));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private JSONArray getSourceList() {

        StringBuffer query = new StringBuffer();
        query.append("select distinct(source) as name from CisEvents");
        return this.getNamesList(query.toString());
    }
    
    private JSONArray getVersionList() {
        
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (getFilterProps("source") != null && ! isFilterPropValueEmpty("source")) {
            whereClause.append("source in " + getFilterProps("source"));
        }

        query.append("select distinct(version) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        return this.getNamesList(query.toString());
    }

    private JSONArray getServerList() {

        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        if (getFilterProps("source") != null && ! isFilterPropValueEmpty("source")) {
            whereClause.append("source in " + this.getFilterProps("source"));
        }
        if (getFilterProps("version") != null && ! isFilterPropValueEmpty("version")) {
            whereClause.append("version in " + getFilterProps("version"));
        }

        query.append("select distinct(server_id) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        return this.getNamesList(query.toString());
    }

    private JSONArray getUserList() {
        
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();

        if (getFilterProps("source") != null && ! isFilterPropValueEmpty("source")) {
            whereClause.append("source in " + getFilterProps("source"));
        }
        if (getFilterProps("version") != null && ! isFilterPropValueEmpty("version")) {
            whereClause.append("version in " + getFilterProps("version"));
        }
        if (getFilterProps("server_id") != null && ! isFilterPropValueEmpty("server_id")) {
            whereClause.append("server_id in " + getFilterProps("server_id"));
        }

        query.append("select distinct(user_id) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        return this.getNamesList(query.toString());
    }

    private JSONArray getSessionList() {
        
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        
        if (getFilterProps("source") != null && ! isFilterPropValueEmpty("source")) {
            whereClause.append("source in " + getFilterProps("source"));
        }
        if (getFilterProps("version") != null && ! isFilterPropValueEmpty("version")) {
            whereClause.append("version in " + getFilterProps("version"));
        }
        if (getFilterProps("server_id") != null && ! isFilterPropValueEmpty("server_id")) {
            whereClause.append("server_id in " + getFilterProps("server_id"));
        }
        if (getFilterProps("user_id") != null && ! isFilterPropValueEmpty("user_id")) {
            whereClause.append("user_id in " + getFilterProps("user_id"));
        }

        query.append("select distinct(session_id) as name from CisEvents"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        
        return this.getNamesList(query.toString());
    }
    
    private JSONArray getContextTypeList() {
        
        StringBuffer query = new StringBuffer();
        query.append("select distinct(type) as name from ContextType");
        return this.getNamesList(query.toString());
    }
    
    private JSONObject getSelectedFiltersList() {
        
        return new JSONObject(this.filterProps);
    }
}
