package com.metron.model.event;

import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.EnvironmentEvent;
import com.metron.model.PersistEvent;
import com.metron.util.Utils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisEnvironmentEvent extends CisEvent {

    protected EnvironmentEvent envevent;

    public CisEnvironmentEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisEnvironmentEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Environment event) - env_os, env_screen_length, env_screen_height
        //envevent = new EnvironmentEvent(this.getMetricValueAttr("env_os"), this.getMetricValueAttr("env_screen_length"), this.getMetricValueAttr("env_screen_height"), this.getGraph());
        envevent = new EnvironmentEvent(this.getMetricValueAttributes(),this.getGraph());
        //this.saveEnvironment();
        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)    
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"EnvironmentEvent");
       
    }

    private void saveEnvironment() {
        
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("env_app_length", this.getMetricValueAttr("env_app_length"));
        props.put("env_app_height", this.getMetricValueAttr("env_app_height"));
        props.put("env_browser_type", this.getMetricValueAttr("env_browser_type"));
        props.put("env_browser_version", this.getMetricValueAttr("env_browser_version"));
        props.put("env_cpu_name", this.getMetricValueAttr("env_cpu_name"));
        props.put("env_cpu_clock", this.getMetricValueAttr("env_cpu_clock"));
        props.put("env_cpu_cores", this.getMetricValueAttr("env_cpu_cores"));
        props.put("env_mem", this.getMetricValueAttr("env_mem"));
        
        envevent.setProperties(props);
        envevent.save();
        
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent();
        this.associateTimeWindow();
      //this.associateExistingEvents();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
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
                OrientVertex currentevent = envevent.find(this.getGraph(), this.getMetricValueAttr("env_os"), this.getMetricValueAttr("env_screen_length"), this.getMetricValueAttr("env_screen_height"));
                currentevent.addEdge("Correlation",preEventvertex,props);
            }    
        }}catch(JSONException e){
            e.printStackTrace();
        }
        
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        envevent.addEdge(this.getEventTimeWindow(duration), "EnvironmentEvent_" + duration.getTable());

        
    }

    /**
     * Creating an Edge b/w RawMetricEvent (contains session info) & EnvironmentEvent with timestamp , metric type as properties
     */
    private void associateRawMetricEvent() {
        Object[] props = new Object[]{"metric_timestamp",this.getStringAttr("metric_timestamp"),"metric_type",this.getStringAttr("metric_type")};
        rawMetricEvent.addEdge(envevent, "Metric_Event",props);
    }



}
