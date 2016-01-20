package com.metron.model.event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.ActionEvent;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisActionEvent extends Event {
    
    protected ActionEvent actionevent;

    public CisActionEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisActionEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Action event) - action_key, action_command, action_view
        actionevent = new ActionEvent(this.getMetricValueAttr("action_key"), this.getMetricValueAttr("action_command"), this.getMetricValueAttr("action_view"), this.getGraph());
        
        this.updateAssociations();
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent();
        this.associateTimeWindow();
        this.associateExistingEvents();
    }

    /**
     * At the time of inserting a new event @session id 
     * create an edge b/w existing events and the current event with difference in 
     * timestamp as property
     */
    private void associateExistingEvents() {
        try{
        JSONArray edgeObject = this.getPreviousMetricEvent();
        for(int j = 0; j < edgeObject.length(); j++){
            OrientEdge edge = this.getGraph().getEdge(edgeObject.get(j));
            //Retrieve the timestamp when the preEvent has occurred
            String preEventTimestamp = edge.getProperty("metric_timestamp");
            //Retrieve the preEvent vertex information
            OrientVertex preEventvertex = this.getGraph().getVertex(edge.getProperty("in"));
            String currenttimestamp = this.getStringAttr("metric_timestamp");
            long diff = Utils.getDateDiffInMIllisec(Utils.parseEventDate(Long.parseLong(preEventTimestamp)),Utils.parseEventDate(Long.parseLong(currenttimestamp)));
            Object[] props = new Object[]{"delta",diff};
            if(diff != 0){
                OrientVertex currentevent = actionevent.find(this.getGraph(), this.getMetricValueAttr("action_key"), this.getMetricValueAttr("action_command"), this.getMetricValueAttr("action_view"));
                preEventvertex.addEdge("Correlation",currentevent,props);
            }    
        }}catch(JSONException e){
            e.printStackTrace();
        }
        
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

    /**
     * Creating an Edge b/w RawMetricEvent (contains session info) & ActionEvent with timestamp , metric type as properties
     */
    private void associateRawMetricEvent() {
        Object[] props = new Object[]{"metric_timestamp",this.getStringAttr("metric_timestamp"),"metric_type",this.getStringAttr("metric_type")};
        rawMetricEvent.addEdge(actionevent, "Metric_Action",props);
    }

}
