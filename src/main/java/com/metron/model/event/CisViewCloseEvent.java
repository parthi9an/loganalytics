package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.PersistEvent;
import com.metron.model.ViewEvent;
import com.metron.util.Utils;

public class CisViewCloseEvent extends CisViewEvent {
    
    long viewActiveTime;

    public CisViewCloseEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData, metricData);
    }

    public void process() throws ClassNotFoundException, SQLException {

        this.saveCisEvent();

        viewevent = new ViewEvent(this.getMetricValueAttributes(), this.getGraph());

        this.updateCloseViewDetails();
        
        this.updateAssociations();

        new PersistEvent().save(this.getAttributes(), this.getMetricValueAttributes(), "ViewEvent");

    }

    private void updateAssociations() {

        this.associateRawMetricEvent();
        super.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
        this.associateDomainRawMetricEvent();
    }

    /**
     * Fetch the recent view opened @session id and calculate the active view time 
     * (i.e difference b/w open & close view timestamp)
     */
    private void updateCloseViewDetails() {

        String result = new ViewEvent().getOpenedViewDetails(this.getMetricValueAttributes(),this.getAttributes());
        long viewOpentimestamp = 0;
        try {
            JSONObject jsondata = new JSONObject(result.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            viewOpentimestamp = resultArr.getJSONObject(0).getLong(this.mappingEventkeys.get("timestamp"));
            String viewclosetimestamp = this.getStringAttr(this.mappingEventkeys.get("timestamp"));
            viewActiveTime = Utils.getDateDiffInMIllisec(Utils.parseEventDate(viewOpentimestamp),Utils.parseEventDate(Long.parseLong(viewclosetimestamp)));
        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        
        
    }

    private void associateRawMetricEvent() {
        Object[] props = new Object[]{this.mappingEventkeys.get("timestamp"), this.getStringAttr(this.mappingEventkeys.get("timestamp")),
                this.mappingEventkeys.get("type"), this.getStringAttr(this.mappingEventkeys.get("type")),"viewActiveTime",viewActiveTime};
        rawMetricEvent.addEdge(viewevent, "Metric_Event", props);
    }

}
