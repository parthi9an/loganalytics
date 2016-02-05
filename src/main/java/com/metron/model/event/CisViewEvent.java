package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.PersistEvent;
import com.metron.model.ViewEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisViewEvent extends CisEvent {

    protected ViewEvent viewevent;

    public CisViewEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisViewEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e View event) - view_name, view_event_type
        viewevent = new ViewEvent(this.getMetricValueAttributes(), this.getGraph());
               
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"ViewEvent");
         
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(viewevent);
        this.associateTimeWindow();
      //this.associateExistingEvents();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }
    
    protected void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        viewevent.addEdge(this.getEventTimeWindow(duration), "ViewEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        viewevent.addEdge(this.getEventTimeWindow(duration), "ViewEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        viewevent.addEdge(this.getEventTimeWindow(duration), "ViewEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        viewevent.addEdge(this.getEventTimeWindow(duration), "ViewEvent_" + duration.getTable());

        
    }
}
