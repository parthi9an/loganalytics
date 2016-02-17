package com.metron.event.service;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.model.FilterCritera;
import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public class FilterEventService extends BaseEventService{

    public JSONObject saveFilterCriteria(String userName, String sessionId, String serverId, String domainId, String source, String fromDate, String toDate) {
        
        JSONObject json = new JSONObject();
        try{
        HashMap<String, Object> filterProps = new HashMap<String, Object>();
        filterProps.put("userName",userName);
        if(sessionId != null)
            filterProps.put("sessionId",sessionId);
        if(serverId != null)
            filterProps.put("serverId",serverId);
        if(domainId != null)
            filterProps.put("domainId",domainId);
        if(source != null)
            filterProps.put("source",source);
        if(fromDate != null)
            filterProps.put("fromDate",fromDate);
        if(toDate != null)
            filterProps.put("toDate",toDate);
        
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        new FilterCritera(filterProps,graph);
        json.put("status", "Successfully Saved");
        }catch(Exception e){
            try {
                json.put("status", "Failed");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        
        return json;
    }

    public JSONArray getSavedFilterCriteria(String userName) {
        
        JSONArray result = new JSONArray();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("userName ='" + userName + "'");
        
        query.append("select * from FilterCriteria"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        String data = new com.metron.orientdb.OrientRest().doSql(query.toString());
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            result = jsondata.getJSONArray("result");
        }catch (JSONException e) {
            e.printStackTrace();
        }    
        
        return result;
    }

}
