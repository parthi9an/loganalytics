package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.BaseModel;
import com.metron.model.FieldEvent;

public class CisFieldEvent extends CisEvent {

    protected BaseModel fieldevent;

    public CisFieldEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    public CisFieldEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context) {
        super(eventData,metricData,contextType,context);
    }
    
    public CisFieldEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
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
        
        // save metric event attributes (i.e field event) - field_name, field_parent
        fieldevent = new FieldEvent(this.getMetricValueAttributes(), this.getGraph());
        
        this.updateAssociations(fieldevent);
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("FieldEvent");
    }
}
