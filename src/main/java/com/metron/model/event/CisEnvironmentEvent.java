package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.BaseModel;
import com.metron.model.EnvironmentEvent;

public class CisEnvironmentEvent extends CisEvent {

    protected BaseModel envevent;

    public CisEnvironmentEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Environment event) - env_os, env_screen_length, env_screen_height
        envevent = new EnvironmentEvent(this.getMetricValueAttributes(),this.getGraph());
        
        this.updateAssociations(envevent);
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("EnvironmentEvent");
    }
}
