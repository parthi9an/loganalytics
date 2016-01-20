package com.metron.model.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metron.model.BaseModel;
import com.metron.model.EventMappings;
import com.metron.model.Host;
import com.metron.model.RawEvent;
import com.metron.model.RawMetricEvent;
import com.metron.model.TimeWindow;
import com.metron.orientdb.OrientRest;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;

public abstract class Event extends BaseModel {

    protected RawEvent rawEvent;
    
    protected RawMetricEvent rawMetricEvent;

    protected Host host;

    protected Logger log = LoggerFactory.getLogger(Event.class);

    private Map<String, Object> attributes = null;
    private Map<String, Object> metricvalueattributes = null;
    private Map<String, Integer> mapping;

    private String[] eventData = null;

    private JSONObject ciseventData = null;
    
    private JSONObject cismetricValueData = null;

    public Event() {
        this.attributes = new HashMap<String, Object>();
    }
    public Event(String[] eventData) {

        this.attributes = new HashMap<String, Object>();
        this.eventData = eventData;
        this.mapping = new HashMap<String, Integer>();
        String eventName = this.getClass().getSimpleName();
        if (this.getClass().equals(RequestStart.class)) {
            if (eventData.length == 20) {
                eventName += "_t1";
            } else {
                eventName += "_t2";
            }
        }
        mapping = EventMappings.getInstance().getEventMapping(eventName);

    }

    public Event(JSONObject eventData) {

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

    public Event(JSONObject eventData, JSONObject metricValueData) {
        
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
                persistTo.put(key, jsondata.get(key).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }        
    }
    public void parse() {
        if (this.eventData.length < EventMappings.getInstance().getDefaultColumnSize()) {
            return;
        }

        setAttribute("rawData", StringUtils.join(eventData, "    "));

        if (!this.isValid()) {
            return;
        }

        Iterator<Entry<String, Integer>> itr = mapping.entrySet().iterator();

        /**
         * Using map populating data into appropriate event
         */

        while (itr.hasNext()) {
            Entry<String, Integer> data = itr.next();
            this.populateData(data.getKey(), data.getValue());
        }

        // if eventName is StartRequest then instantiate a StartRequest event
        // and return it.

    }

    protected void populateData(String columnName, int colNo) {
        if (colNo < eventData.length) {
            setAttribute(columnName, eventData[colNo]);
        }
    }

    protected void saveRawEvent() {
        String eventId = this.getStringAttr("eventId");
        rawEvent = new RawEvent(eventId, this.getGraph());
        // System.out.println("Event attributes : " +
        // this.attributes.toString());
        rawEvent.setProperties(new HashMap<String, Object>(this.getAttributes()));
        rawEvent.save();
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
        query.append("select outE() as edge from CisEvents where metric_session_id =" + this.getStringAttr("metric_session_id"));
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
    
    protected void associateRawEventToHost() {
        rawEvent.addEdge(host, "Event_Host");
    }

    public Map<String, Object> getAttributes() {
        return attributes;
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

    public String[] getRawData() {
        return this.eventData;
    }

    public Object getRawData(int columnNo) {
        return this.eventData[columnNo];
    }

    public Map<String, Integer> getMapping() {
        return this.mapping;
    }

    public Object getEventValue(String columnName) {
        return this.eventData[this.getMapping().get(columnName)];
    }

    public abstract void process();

    public boolean isValid() {
        return this.eventData.length > 9;
    }

    public TimeWindow getTimeWindow(DURATION duration) {
        Date date = Utils.parseEventDate((String) this.getAttribute("timestamp"));
        return new TimeWindow(date, duration, this.getGraph());
    }
    
    public TimeWindow getEventTimeWindow(DURATION duration) {
        Date date = Utils.parseEventDate(Long.parseLong((String)this.getAttribute("metric_timestamp")));
        return new TimeWindow(date, duration, this.getGraph());
    }

    @Override
    public String toString() {
        return new JSONObject(this.attributes).toString();
    }
    public void setTimeStamp(String tsInfo) {
        // TODO Auto-generated method stub
    }
    public void setHost(String hostName) {
        // TODO Auto-generated method stub

    }

}
