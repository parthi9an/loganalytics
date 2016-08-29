package com.metron.event.service;

import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;

public class WindowEventService extends BaseEventService{
    
    public WindowEventService(String filter) {
        super(filter);
    }
    
    public WindowEventService() {}

    public Long count() {
        return getCount("select count(*) as count from Metric_Event where type containstext 'window'");
    }

    public JSONObject getCountOfMovedWindows() {
        
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        StringBuffer query1 = new StringBuffer();
        StringBuffer query2 = new StringBuffer();
        QueryWhereBuffer whereClause = this.edgeFilter();
        QueryWhereBuffer whereClause1 = this.edgeFilter();
        whereClause.append("type containstext 'window'");
        whereClause1.append("type containstext 'window'");

        if (getFilterProps("context_type") != null && ! isFilterPropValueEmpty("context_type")) {

            // If Context Type contains Dialog, invoke two queries one for dialog type and another for manager,modeler,discovery types & union the results 
            if((Boolean) isContextTypeDialog("context_type").get("isContextType")) {
                
                whereClause.append("in.context.type in " + isContextTypeDialog("context_type").get("ContextType"));
                query1.append("select count(*) as count,in.context.context.view as name from Metric_Event group by in.context.context.view"
                        + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
                
                whereClause1.append("in.context.type = 'dialog'");
                query2.append("select count(*) as count,in.context.context.context.source.view as name from Metric_Event group by in.context.context.context.source.view"
                        + ((!whereClause1.toString().equals("")) ? " Where " + whereClause1.toString() : ""));
                
                query.append("SELECT EXPAND( $c ) LET $a = (").append(query1).append("), $b = (").append(query2).append("), $c = UNIONALL( $a, $b )");
                
                result = this.getAssociatedCount(query.toString());

                return result;
            }
            
            whereClause.append("in.context.type in " + getFilterProps("context_type"));
        }
        
        query.append("select count(*) as count,in.context.context.view as name from Metric_Event group by in.context.context.view"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));

        result = this.getAssociatedCount(query.toString());

        return result;
    }

}
