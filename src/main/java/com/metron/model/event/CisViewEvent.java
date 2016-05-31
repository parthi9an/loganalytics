package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.BaseModel;
import com.metron.model.ViewEvent;

public class CisViewEvent extends CisEvent {

    protected BaseModel viewevent;

    public CisViewEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e View event) - view_name, view_event_type
        viewevent = new ViewEvent(this.getMetricValueAttributes(), this.getGraph());
               
        this.updateAssociations(viewevent);
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("ViewEvent");
    }
}
