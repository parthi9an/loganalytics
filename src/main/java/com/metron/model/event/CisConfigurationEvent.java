package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.ConfigurationEvent;
import com.metron.model.PersistEvent;
import com.metron.util.Utils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisConfigurationEvent extends CisEvent {

    protected ConfigurationEvent configurationevent;

    public CisConfigurationEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisConfigurationEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Configuration event) - config_name
        configurationevent = new ConfigurationEvent(this.getMetricValueAttr("config_name"), this.getGraph());
        
      //Save data to Relational DB (Postgres)
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"ConfigurationEvent");
        
        this.updateAssociations();
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
                    OrientVertex currentevent = configurationevent.find(this.getGraph(), this.getMetricValueAttr("config_name"));
                    currentevent.addEdge("Correlation",preEventvertex,props);
                }    
            }}catch(JSONException e){
                e.printStackTrace();
            }        
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        configurationevent.addEdge(this.getEventTimeWindow(duration), "ConfigurationEvent_" + duration.getTable());

        
    }

    private void associateRawMetricEvent() {
        Object[] props = new Object[]{"metric_timestamp",this.getStringAttr("metric_timestamp"),"metric_type",this.getStringAttr("metric_type")};
        rawMetricEvent.addEdge(configurationevent, "Metric_Event",props);
    }


}
