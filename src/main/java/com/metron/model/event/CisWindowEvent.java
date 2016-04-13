package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.WindowEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisWindowEvent extends CisEvent {

    protected WindowEvent windowevent;

    public CisWindowEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    public CisWindowEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context) {
        super(eventData,metricData,contextType,context);
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        if (this.getContextattributes() != null) {
            this.getcontextType();
        }
        
        // save metric event attributes (i.e window event) - window_length, window_height, window_view
        windowevent = new WindowEvent(this.getMetricValueAttributes(), this.getGraph());
         
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("WindowEvent");
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(windowevent);
        this.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }
    
    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        windowevent.addEdge(this.getEventTimeWindow(duration), "WindowEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        windowevent.addEdge(this.getEventTimeWindow(duration), "WindowEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        windowevent.addEdge(this.getEventTimeWindow(duration), "WindowEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        windowevent.addEdge(this.getEventTimeWindow(duration), "WindowEvent_" + duration.getTable());

        
    }
}
