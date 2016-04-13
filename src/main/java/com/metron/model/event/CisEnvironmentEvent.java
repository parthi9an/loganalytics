package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.EnvironmentEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisEnvironmentEvent extends CisEvent {

    protected EnvironmentEvent envevent;

    public CisEnvironmentEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Environment event) - env_os, env_screen_length, env_screen_height
        envevent = new EnvironmentEvent(this.getMetricValueAttributes(),this.getGraph());
        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("EnvironmentEvent");
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(envevent);
        this.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        
    }
}
