package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.WindowScrollEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisWindowScrollEvent extends CisEvent {
    
    protected WindowScrollEvent windowscrollevent;

    public CisWindowScrollEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    }

    public CisWindowScrollEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType, JSONObject context) {
        super(eventData,metricData,contextType,context);
    }

    @Override
    public void process() throws SQLException {

        this.saveCisEvent(); 
        
        if (this.getContextattributes() != null) {
            this.getcontextType();
        }
        // save metric event attributes (i.e window scroll event) - orientation, direction, view
        windowscrollevent = new WindowScrollEvent(this.getMetricValueAttributes(), this.getGraph());
        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("WindowScrollEvent");
    }
    
    private void updateAssociations() {

        this.associateRawMetricEvent(windowscrollevent);
        this.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        windowscrollevent.addEdge(this.getEventTimeWindow(duration), "WindowScrollEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        windowscrollevent.addEdge(this.getEventTimeWindow(duration), "WindowScrollEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        windowscrollevent.addEdge(this.getEventTimeWindow(duration), "WindowScrollEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        windowscrollevent.addEdge(this.getEventTimeWindow(duration), "WindowScrollEvent_" + duration.getTable());

        
    }

}
