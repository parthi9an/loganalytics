package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.PersistEvent;
import com.metron.model.ViewEvent;
import com.metron.orientdb.OrientRest;
import com.metron.util.Utils;

public class CisViewCloseEvent extends CisViewEvent {
    
    long viewActiveTime;

    public CisViewCloseEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData, metricData);
    }

    public void process() throws ClassNotFoundException, SQLException {

        this.saveCisEvent();

        viewevent = new ViewEvent(this.getMetricValueAttr("view_name"),
                this.getMetricValueAttr("view_event_type"), this.getGraph());

        this.updateCloseViewDetails();
        
        this.updateAssociations();

        new PersistEvent().save(this.getAttributes(), this.getMetricValueAttributes(), "ViewEvent");

    }

    private void updateAssociations() {

        this.associateRawMetricEvent();
        super.associateTimeWindow();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
    }

    /**
     * Fetch the recent view opened @session id and calculate the active view time 
     * (i.e difference b/w open & close view timestamp)
     */
    private void updateCloseViewDetails() {

        StringBuffer query = new StringBuffer();
        query.append("select * from Metric_Event where metric_type = 'type_view' and in.view_name = '"
                + this.getMetricValueAttr("view_name")
                + "' and out.metric_session_id = '"
                + this.getStringAttr("metric_session_id")
                + "' order by metric_timestamp desc");
        String result = new OrientRest().doSql(query.toString());
        long viewOpentimestamp = 0;
        try {
            JSONObject jsondata = new JSONObject(result.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            viewOpentimestamp = resultArr.getJSONObject(0).getLong("metric_timestamp");
            String viewclosetimestamp = this.getStringAttr("metric_timestamp");
            viewActiveTime = Utils.getDateDiffInMIllisec(Utils.parseEventDate(viewOpentimestamp),Utils.parseEventDate(Long.parseLong(viewclosetimestamp)));
        }catch (JSONException e1) {
            e1.printStackTrace();
        }
        
        
    }

    private void associateRawMetricEvent() {
        Object[] props = new Object[]{"metric_timestamp", this.getStringAttr("metric_timestamp"),
                "metric_type", this.getStringAttr("metric_type"),"viewActiveTime",viewActiveTime};
        rawMetricEvent.addEdge(viewevent, "Metric_Event", props);
    }

}
