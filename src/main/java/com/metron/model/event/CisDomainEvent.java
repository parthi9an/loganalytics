package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.DomainEvent;
import com.metron.model.PersistEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisDomainEvent extends CisEvent {

    protected DomainEvent domainevent;

    public CisDomainEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisDomainEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Action event) - domain_type
        domainevent = new DomainEvent(this.getMetricValueAttr("type"), this.getGraph());
        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)   
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"DomainEvent");
        
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(domainevent);
        this.associateTimeWindow();
      //this.associateExistingEvents();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        
    }
}
