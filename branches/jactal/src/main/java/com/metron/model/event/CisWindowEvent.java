package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.BaseModel;
import com.metron.model.WindowEvent;

public class CisWindowEvent extends CisEvent {

    protected BaseModel windowevent;

    public CisWindowEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    public CisWindowEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context) {
        super(eventData,metricData,contextType,context);
    }
    
    public CisWindowEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context, JSONObject dialogContextType, JSONObject dialogContext) {
        super(eventData,metricData,contextType,context,dialogContextType,dialogContext);
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        if (! this.getContextattributes().isEmpty()) {
            this.insertcontextType();
        }
        
        // save metric event attributes (i.e window event) - window_length, window_height, window_view
        windowevent = new WindowEvent(this.getMetricValueAttributes(), this.getGraph());
         
        this.updateAssociations(windowevent);
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("WindowEvent");
    }
}
