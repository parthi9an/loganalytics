package com.metron.model.event;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.ActionEvent;
import com.metron.model.PersistEvent;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisActionEvent extends CisEvent {
    
    protected ActionEvent actionevent;
    
    public CisActionEvent(JSONObject eventData) {
        super(eventData);
    }
    
    public CisActionEvent(JSONObject eventData, JSONObject metricData) {
        super(eventData,metricData);
    
    }

    @Override
    public void process() throws ClassNotFoundException, SQLException {
        
        // save generic metric attribues - metric_type, metric_timestamp, metric_session_id
        this.saveCisEvent(); 
        
        // save metric event attributes (i.e Action event) - action_key, action_command, action_view
        actionevent = new ActionEvent(this.getMetricValueAttr("action_key"), this.getMetricValueAttr("action_command"), this.getMetricValueAttr("action_view"), this.getGraph());
                
        //Save data to Relational DB (Postgres)        
        new PersistEvent().save(this.getAttributes(),this.getMetricValueAttributes(),"ActionEvent");
                
        this.updateAssociations();
        
    }

    /**
     * updates the association count of the pattern 
     * (Events occurred till this point @session considered as pattern)
     */
    /*private void updatePatterns() {
       
        try {
        JSONArray edgeObject = this.getPreviousMetricEvent();
        StringBuilder patern = new StringBuilder();        
        for(int j = 0; j < edgeObject.length(); j++){            
            OrientEdge edge = this.getGraph().getEdge(edgeObject.get(j));            
            patern.append(edge.getProperty("in")).append("_");          
        }
        String patterType = patern.toString().substring(0, patern.toString().length()-1);
        pattern = new Pattern(patterType,this.getGraph());
        
        } catch (JSONException e) {
            
            e.printStackTrace();
        }
    }*/

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
            //Excluding session_pattern edge as it doesn't represent events.
            //if(edge.getLabel().compareToIgnoreCase("session_pattern") != 0){
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
    
    /**
     * Create an edge b/w RawMetricEvent (contains session info) & the pattern 
     */
    /*private void associatePatternRawMetricEvent() {
        rawMetricEvent.addEdge(pattern, "session_pattern");
    }*/

}
