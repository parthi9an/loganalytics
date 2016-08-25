package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

import com.metron.model.BaseModel;
import com.metron.model.WindowScrollEvent;

public class CisWindowScrollEvent extends CisEvent {
    
    protected BaseModel windowscrollevent;

    public CisWindowScrollEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    }

    public CisWindowScrollEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType, JSONObject context) {
        super(eventData,metricData,contextType,context);
    }

    public CisWindowScrollEvent(JSONObject eventData, JSONObject metricData, JSONObject contextType,
            JSONObject context, JSONObject dialogContextType, JSONObject dialogContext) {
        super(eventData,metricData,contextType,context,dialogContextType,dialogContext);
    }

    @Override
    public void process() throws SQLException {

        this.saveCisEvent(); 
        
        if (! this.getContextattributes().isEmpty()) {
            this.insertcontextType();
        }
        // save metric event attributes (i.e window scroll event) - orientation, direction, view
        windowscrollevent = new WindowScrollEvent(this.getMetricValueAttributes(), this.getGraph());
        
        this.updateAssociations(windowscrollevent);
        
        //Save data to Relational DB (Postgres)
        this.persistToPostgres("WindowScrollEvent");
    }
}
