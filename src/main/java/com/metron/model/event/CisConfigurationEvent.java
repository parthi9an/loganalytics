package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.BaseModel;
import com.metron.model.ConfigurationEvent;

public class CisConfigurationEvent extends CisEvent {

    protected BaseModel configurationevent;

    public CisConfigurationEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Configuration event) - config_name
        configurationevent = new ConfigurationEvent(this.getMetricValueAttributes(), this.getGraph());
       
        this.updateAssociations(configurationevent);
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("ConfigurationEvent");
    }
}
