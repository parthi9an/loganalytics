package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.ActionEvent;
import com.metron.util.TimeWindowUtil.DURATION;

public class CisActionEvent extends CisEvent {
    
    protected ActionEvent actionevent;
    
    public CisActionEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    public CisActionEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
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
        
        // save metric event attributes (i.e Action event) - action_key, action_command, action_view
        actionevent = new ActionEvent(this.getMetricValueAttributes(), this.getGraph());
                        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("ActionEvent");
            
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent(actionevent);
        this.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        actionevent.addEdge(this.getEventTimeWindow(duration), "ActionEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        actionevent.addEdge(this.getEventTimeWindow(duration), "ActionEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        actionevent.addEdge(this.getEventTimeWindow(duration), "ActionEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        actionevent.addEdge(this.getEventTimeWindow(duration), "ActionEvent_" + duration.getTable());

        
    }
}
