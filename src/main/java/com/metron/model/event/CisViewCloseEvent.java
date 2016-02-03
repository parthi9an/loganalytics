package com.metron.model.event;

import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONObject;

import com.metron.model.ViewEvent;

public class CisViewCloseEvent extends CisViewEvent{

    public CisViewCloseEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData, metricData);
    }
   
    public void process() throws ClassNotFoundException, SQLException {
        viewevent = new ViewEvent(this.getMetricValueAttr("view_name"), this.getMetricValueAttr("view_event_type"), this.getStringAttr("metric_session_id"), this.getGraph());
        this.updateview();
    }

    private void updateview() {
        
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("view_event_type", this.getMetricValueAttr("view_event_type"));
        props.put("view_event_close_timestamp", this.getAttribute("metric_timestamp"));
        
        viewevent.setProperties(props);
        viewevent.save();
    }

}
