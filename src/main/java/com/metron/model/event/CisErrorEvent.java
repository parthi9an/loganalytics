package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.AppConfig;
import com.metron.model.ErrorEvent;
import com.metron.model.ErrorPattern;
import com.metron.model.PersistEvent;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisErrorEvent extends CisEvent {

    protected ErrorEvent errorevent;
    
    protected ErrorPattern errorpattern;

    public CisErrorEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisErrorEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Action event) - error_type, error_message, error_trace
        errorevent = new ErrorEvent(this.getMetricValueAttr("error_type"), this.getMetricValueAttr("error_message"), this.getMetricValueAttr("error_trace"), this.getGraph());
        
        this.updateAssociations();
        
        //Save data to Relational DB (Postgres)   
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"ErrorEvent");
        
    }

    private void updateAssociations() {
        
        this.associateRawMetricEvent();
        this.associateTimeWindow();
      //this.associateExistingEvents();
        this.updatePatterns();
        this.updateErrorPatterns();
        this.associatePatternRawMetricEvent();
    }
    
    private void updateErrorPatterns() {
        
        try {
            JSONArray edgeObject = this.getPreviousMetricEvent();
            StringBuilder patern = new StringBuilder(); 
            long timewindow = AppConfig.getInstance().getInt("event.timewindow");
            for(int j = 0; j < edgeObject.length(); j++){            
                OrientEdge edge = this.getGraph().getEdge(edgeObject.get(j));
                
                //Excluding session_pattern edge as it doesn't represent events.
                if(edge.getLabel().compareToIgnoreCase("session_pattern") != 0){
                
                //Previous Event Timestamp
                String preEventTimestamp = edge.getProperty("metric_timestamp");
                String currenttimestamp = this.getStringAttr("metric_timestamp");
                long diff = Utils.getDateDiffInSec(Utils.parseEventDate(Long.parseLong(preEventTimestamp)),Utils.parseEventDate(Long.parseLong(currenttimestamp)));
                long diff1 = Utils.getDateDiffInMIllisec(Utils.parseEventDate(Long.parseLong(preEventTimestamp)),Utils.parseEventDate(Long.parseLong(currenttimestamp)));
                
                if(diff1 < timewindow)
                    patern.append(((OrientVertex)edge.getProperty("in")).getId()).append("_");          
            }}
            String patterType = patern.toString().substring(0, patern.toString().length()-1);
            errorpattern = new ErrorPattern(patterType,this.getMetricValueAttr("error_type"),this.getGraph());
            
            } catch (JSONException e) {
                
                e.printStackTrace();
            }
        
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
                OrientVertex currentevent = errorevent.find(this.getGraph(), this.getMetricValueAttr("error_type"), this.getMetricValueAttr("error_message"), this.getMetricValueAttr("error_trace"));
                currentevent.addEdge("Correlation",preEventvertex,props);
            }    
        }}catch(JSONException e){
            e.printStackTrace();
        }   
    }

    private void associateTimeWindow() {
        
     // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        errorevent.addEdge(this.getEventTimeWindow(duration), "ErrorEvent_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        errorevent.addEdge(this.getEventTimeWindow(duration), "ErrorEvent_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        errorevent.addEdge(this.getEventTimeWindow(duration), "ErrorEvent_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        errorevent.addEdge(this.getEventTimeWindow(duration), "ErrorEvent_" + duration.getTable());

        
    }

    private void associateRawMetricEvent() {
        Object[] props = new Object[]{"metric_timestamp",this.getStringAttr("metric_timestamp"),"metric_type",this.getStringAttr("metric_type")};
        rawMetricEvent.addEdge(errorevent, "Metric_Event",props); 
    }


}
