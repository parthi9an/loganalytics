package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.BaseModel;
import com.metron.model.KeyboardEvent;

public class CisKeyboardEvent extends CisEvent {

    protected BaseModel keyboardevent;

    public CisKeyboardEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    public CisKeyboardEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context) {
        super(eventData,metricData,contextType,context);
    }
    
    public CisKeyboardEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
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
        
        // save metric event attributes (i.e Action event) - key_command, key_target
        keyboardevent = new KeyboardEvent(this.getMetricValueAttributes(),this.getGraph());
        
        this.updateAssociations(keyboardevent);
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("KeyboardEvent");
    }
}
