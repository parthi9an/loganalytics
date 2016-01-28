package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.DomainEvent;
import com.metron.model.PersistEvent;
import com.metron.util.Utils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisDomainEvent extends CisEvent {

    protected DomainEvent domainevent;

    public CisDomainEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisDomainEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Action event) - domain_type
        domainevent = new DomainEvent(this.getMetricValueAttr("domain_type"), this.getGraph());
        
      //Save data to Relational DB (Postgres)   
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"DomainEvent");
        
        this.updateAssociations();
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
                OrientVertex currentevent = domainevent.find(this.getGraph(), this.getMetricValueAttr("domain_type"));
                currentevent.addEdge("Correlation",preEventvertex,props);
            }    
        }}catch(JSONException e){
            e.printStackTrace();
        }
        
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent();
        this.associateTimeWindow();
      //this.associateExistingEvents();
        this.updatePatterns();
        this.associatePatternRawMetricEvent();
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        domainevent.addEdge(this.getEventTimeWindow(duration), "DomainEvent_" + duration.getTable());

        
    }

    /**
     * Creating an Edge b/w RawMetricEvent (contains session info) & DomainEvent with timestamp , metric type as properties
     */
    private void associateRawMetricEvent() {
        Object[] props = new Object[]{"metric_timestamp",this.getStringAttr("metric_timestamp"),"metric_type",this.getStringAttr("metric_type")};
        rawMetricEvent.addEdge(domainevent, "Metric_Event",props);  
    }
}
