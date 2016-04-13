package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.ConfigurationEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisConfigurationEvent extends CisEvent {

    protected ConfigurationEvent configurationevent;

    public CisConfigurationEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Configuration event) - config_name
        configurationevent = new ConfigurationEvent(this.getMetricValueAttributes(), this.getGraph());
       
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("ConfigurationEvent");
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(configurationevent);
        this.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        
    }
}
