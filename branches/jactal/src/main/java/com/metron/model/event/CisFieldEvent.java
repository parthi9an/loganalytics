package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.FieldEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisFieldEvent extends CisEvent {

    protected FieldEvent fieldevent;

    public CisFieldEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    public CisFieldEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context) {
        super(eventData,metricData,contextType,context);
    }
    
    public CisFieldEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context, JSONObject dialogContextType, JSONObject dialogContext) {
        super(eventData,metricData,contextType,context,dialogContextType,dialogContext);
    }

    @Override
    public void process() throws SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent();
        
        if (! this.getContextattributes().isEmpty()) {
            this.insertcontextType();
        }
        
        // save metric event attributes (i.e field event) - field_name, field_parent
        fieldevent = new FieldEvent(this.getMetricValueAttributes(), this.getGraph());
        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("FieldEvent");
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(fieldevent);
        this.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }
    
    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        fieldevent.addEdge(this.getEventTimeWindow(duration), "FieldEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        fieldevent.addEdge(this.getEventTimeWindow(duration), "FieldEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        fieldevent.addEdge(this.getEventTimeWindow(duration), "FieldEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        fieldevent.addEdge(this.getEventTimeWindow(duration), "FieldEvent_" + duration.getTable());

        
    }
}
