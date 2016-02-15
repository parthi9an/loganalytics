package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.KeyboardEvent;
import com.metron.model.PersistEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisKeyboardEvent extends CisEvent {

    protected KeyboardEvent keyboardevent;

    public CisKeyboardEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Action event) - key_command, key_target
        keyboardevent = new KeyboardEvent(this.getMetricValueAttributes(),this.getGraph());
        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"KeyboardEvent");
        
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(keyboardevent);
        this.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }
    
    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        keyboardevent.addEdge(this.getEventTimeWindow(duration), "KeyboardEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        keyboardevent.addEdge(this.getEventTimeWindow(duration), "KeyboardEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        keyboardevent.addEdge(this.getEventTimeWindow(duration), "KeyboardEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        keyboardevent.addEdge(this.getEventTimeWindow(duration), "KeyboardEvent_" + duration.getTable());

        
    }
}
