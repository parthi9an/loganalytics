package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONObject;

public class CisViewOpenEvent extends CisViewEvent{

    public CisViewOpenEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData, metricData);
    }
    
    public void process() throws SQLException {
        super.process();
    }    
}
