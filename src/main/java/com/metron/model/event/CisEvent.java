package com.metron.model.event;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metron.AppConfig;
import com.metron.model.BaseModel;
import com.metron.model.Pattern;
import com.metron.model.RawMetricEvent;
import com.metron.model.TimeWindow;
import com.metron.orientdb.OrientRest;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public abstract class CisEvent extends BaseModel{
        
    protected RawMetricEvent rawMetricEvent;
    
    protected Pattern pattern;

    protected Logger log = LoggerFactory.getLogger(CisEvent.class);

    private Map<String, Object> attributes = null;
    private Map<String, Object> metricvalueattributes = null;

    private JSONObject ciseventData = null;
    
    private JSONObject cismetricValueData = null;

    public CisEvent() {
        this.attributes = new HashMap<String, Object>();
    }

    public CisEvent(JSONObject eventData) {

        this.attributes = new HashMap<String, Object>();
        this.ciseventData = eventData;
        Iterator<?> keys = ciseventData.keys();
        try {
            while (keys.hasNext()) {
                String key = (String) keys.next();
                attributes.put(key, ciseventData.get(key).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public CisEvent(JSONObject eventData, JSONObject metricValueData) {
        
        this.attributes = new HashMap<String, Object>();
        this.metricvalueattributes = new HashMap<String, Object>();
        this.ciseventData = eventData;
        this.cismetricValueData = metricValueData;
        persistJson(ciseventData,attributes);
        persistJson(cismetricValueData,metricvalueattributes);
    }
    
    private void persistJson(JSONObject jsondata, Map<String, Object> persistTo) {
        Iterator<?> keys = jsondata.keys();
        try {
            while (keys.hasNext()) {
                String key = (String) keys.next();
                persistTo.put(key, jsondata.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * Create a vertex (RawMetricEvent) for sessionid
     */
    public void saveCisEvent() {
        
        //String metric_type = this.getStringAttr("metric_type");
        //String metric_timestamp = this.getStringAttr("metric_timestamp");
        String metric_session_id = this.getStringAttr("metric_session_id");
        //rawMetricEvent = new RawMetricEvent(metric_type,metric_timestamp,metric_session_id,this.getGraph());
        //rawMetricEvent.setProperties(new HashMap<String, Object>(this.getAttributes()));
        rawMetricEvent = new RawMetricEvent(metric_session_id,this.getGraph());
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("metric_session_id", this.getStringAttr("metric_session_id"));
        rawMetricEvent.setProperties(props);
        rawMetricEvent.save();
     
    }
    
    /**
     * Retrieve the edges which are connected to different events at
     * particular session
     * @return JSONArray Contains the Edge id's 
     */
    public JSONArray getPreviousMetricEvent() {
        
        StringBuffer query = new StringBuffer();
        query.append("select outE('Metric_Event') as edge from CisEvents where metric_session_id =" + this.getStringAttr("metric_session_id"));
        String result = new OrientRest().doSql(query.toString());
        
        JSONObject resultObject;
        JSONArray ja,edgeObject = null;
        try {
            resultObject = new JSONObject(result);
            ja = resultObject.getJSONArray("result");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject child = ja.getJSONObject(i);
                edgeObject = child.getJSONArray("edge");                    
               }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return edgeObject;    
    }
    
    public abstract void process() throws ClassNotFoundException, SQLException;

    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public Map<String, Object> getMetricValueAttributes() {
        return metricvalueattributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public String getStringAttr(String key) {
        return (this.attributes.containsKey(key)) ? this.attributes.get(key).toString() : null;
    }
    
    public String getMetricValueAttr(String key) {
        return (this.metricvalueattributes.containsKey(key)) ? this.metricvalueattributes.get(key).toString() : null;
    }

    public TimeWindow getTimeWindow(DURATION duration) {
        Date date = Utils.parseEventDate((String) this.getAttribute("timestamp"));
        return new TimeWindow(date, duration, this.getGraph());
    }
    
    public TimeWindow getEventTimeWindow(DURATION duration) {
        //Date date = Utils.parseEventDate(Long.parseLong((String)this.getAttribute("metric_timestamp")));
        Date date = Utils.parseEventDate((Long)this.getAttribute("metric_timestamp"));
        return new TimeWindow(date, duration, this.getGraph());
    }

    @Override
    public String toString() {
        return new JSONObject(this.attributes).toString();
    }
    
    /**
     * updates the association count of the pattern 
     * (Events occurred before 5 min to till this point @session considered as pattern)
     */
    public void updatePatterns() {
       
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
        pattern = new Pattern(patterType,this.getGraph());
        
        } catch (JSONException e) {
            
            e.printStackTrace();
        }
    }
    
    /**
     * Create an edge b/w RawMetricEvent (contains session info) & the pattern 
     */
    public void associatePatternRawMetricEvent() {
        rawMetricEvent.addEdge(pattern, "session_pattern");
    }



}
