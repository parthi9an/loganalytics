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
import com.metron.model.CisEventKeyMappings;
import com.metron.model.ContextType;
import com.metron.model.DomainEvent;
import com.metron.model.Pattern;
import com.metron.model.PersistEvent;
import com.metron.model.RawMetricEvent;
import com.metron.model.TimeWindow;
import com.metron.model.ViewContext;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public abstract class CisEvent extends BaseModel {

    protected RawMetricEvent rawMetricEvent;

    protected Pattern pattern;

    protected DomainEvent domainevent;

    protected Logger log = LoggerFactory.getLogger(CisEvent.class);

    private Map<String, Object> attributes = null;
    private Map<String, Object> metricvalueattributes = null;
    private Map<String, Object> contexttype = null;
    private Map<String, Object> contextattributes = null;
    private Map<String, Object> dialogcontexttype = null;
    private Map<String, Object> dialogcontextattributes = null;
    protected Map<String, String> mappingEventkeys;
    
    protected boolean insertToPostgres;

    public CisEvent() {
        this.attributes = new HashMap<String, Object>();
    }

    public CisEvent(JSONObject eventData, JSONObject metricValueData) {

        this.mappingEventkeys = new HashMap<String, String>();
        mappingEventkeys = CisEventKeyMappings.getInstance().getEventMapping("RawEvent");
        insertToPostgres = Boolean.parseBoolean(AppConfig.getInstance().getString("postgres.dump"));

        this.attributes = new HashMap<String, Object>();
        this.metricvalueattributes = new HashMap<String, Object>();
        parseJson(eventData, attributes);
        parseJson(metricValueData, metricvalueattributes);
    }
    
    public CisEvent(JSONObject eventData, JSONObject metricValueData, JSONObject contextType, JSONObject context) {

        this(eventData,metricValueData);
        this.contexttype = new HashMap<String, Object>();
        this.contextattributes = new HashMap<String, Object>();
        parseJson(contextType, contexttype);
        parseJson(context, contextattributes);
    }

    public CisEvent(JSONObject eventData, JSONObject metricValueData, JSONObject contexttype,
            JSONObject context, JSONObject dialogContextType, JSONObject dialogContext) {
        
        this(eventData,metricValueData,contexttype,context);
        this.dialogcontexttype = new HashMap<String, Object>();
        this.dialogcontextattributes = new HashMap<String, Object>();
        parseJson(dialogContextType, dialogcontexttype);
        parseJson(dialogContext, dialogcontextattributes);
    }

    private void parseJson(JSONObject jsondata, Map<String, Object> persistTo) {
        if (jsondata != null) {
            Iterator<?> keys = jsondata.keys();
            try {
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (jsondata.get(key).getClass().getSimpleName()
                            .compareToIgnoreCase("JSONArray") == 0
                            || jsondata.get(key).getClass().getSimpleName()
                                    .compareToIgnoreCase("JSONObject") == 0) {
                        // since orientdb throwing OSerializationException while
                        // storing Json
                        persistTo.put(key, jsondata.get(key).toString());
                    } else
                        persistTo.put(key, jsondata.get(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a vertex (RawMetricEvent) for sessionid
     */
    public void saveCisEvent() {

        rawMetricEvent = new RawMetricEvent(this.getAttributes(), this.getGraph());
        domainevent = new DomainEvent(this.getAttribute(mappingEventkeys.get("domain_type"))
                .toString(), this.getGraph());

    }

    /**
     * Retrieve the edges which are connected to different events at particular
     * session
     * 
     * @return JSONArray Contains the Edge id's
     */
    public JSONArray getPreviousMetricEvent() {

        String result = new RawMetricEvent().getPreviousMetricEvent(this.getAttributes());

        JSONObject resultObject;
        JSONArray ja, edgeObject = null;
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
    
    public void insertcontextType() {
        
        if(! dialogcontexttype.isEmpty()){
            
            ViewContext dialogviewcontext = new ViewContext(this.getDialogcontextattributes(), this.getGraph());
            
            this.getDialogcontexttype().put("source", dialogviewcontext.vertex.getId());
            
            ContextType dialogcontexttype = new ContextType(this.getDialogcontexttype(), this.getGraph());
            
            this.getContextattributes().put("context", dialogcontexttype.vertex.getId());
        }
        
        // save view context
        ViewContext viewcontext = new ViewContext(this.getContextattributes(), this.getGraph());

        this.getContextType().put("context", viewcontext.vertex.getId());
        // save contexttype along with viewcontext vertex
        ContextType contexttype = new ContextType(this.getContextType(), this.getGraph());

        // save context type vertex id into associates event vertex
        this.getMetricValueAttributes().put("context", contexttype.vertex.getId());
    }
    
    public void persistToPostgres(String tableName) throws SQLException {
        
        if (insertToPostgres) {
            if (this.getDialogcontextattributes() != null && this.getContextattributes() != null) {
                if (!this.getDialogcontextattributes().isEmpty()
                        && !this.getContextattributes().isEmpty()) {
                    this.getDialogcontexttype().remove("source");
                    this.getContextattributes().remove("context");
                    this.getContextType().remove("context");
                    this.getMetricValueAttributes().remove("context");
                    new PersistEvent().save(this.getAttributes(), this.getMetricValueAttributes(),
                            this.getContextType(), this.getContextattributes(),
                            this.getDialogcontexttype(), this.getDialogcontextattributes(),
                            tableName);
                } else if (!this.getContextattributes().isEmpty()) {
                    this.getContextType().remove("context");
                    this.getMetricValueAttributes().remove("context");
                    new PersistEvent().save(this.getAttributes(), this.getMetricValueAttributes(),
                            this.getContextType(), this.getContextattributes(), tableName);
                }
            } else
                new PersistEvent().save(this.getAttributes(), this.getMetricValueAttributes(),
                        tableName);
        }
    }

    public abstract void process() throws SQLException;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Map<String, Object> getMetricValueAttributes() {
        return metricvalueattributes;
    }

    public Map<String, Object> getContextType() {
        return contexttype;
    }
    
    public Map<String, Object> getContextattributes() {
        return contextattributes;
    }
    
    public Map<String, Object> getDialogcontexttype() {
        return dialogcontexttype;
    }

    public Map<String, Object> getDialogcontextattributes() {
        return dialogcontextattributes;
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
        return (this.metricvalueattributes.containsKey(key)) ? this.metricvalueattributes.get(key)
                .toString() : null;
    }

    public TimeWindow getTimeWindow(DURATION duration) {
        Date date = Utils.parseEventDate((String) this.getAttribute(mappingEventkeys.get("timestamp")));
        return new TimeWindow(date, duration, this.getGraph());
    }

    public TimeWindow getEventTimeWindow(DURATION duration) {
        // Date date =
        // Utils.parseEventDate(Long.parseLong((String)this.getAttribute("metric_timestamp")));
        Date date = Utils.parseEventDate((Long) this.getAttribute(mappingEventkeys.get("timestamp")));
        return new TimeWindow(date, duration, this.getGraph());
    }

    @Override
    public String toString() {
        return new JSONObject(this.attributes).toString();
    }

    /**
     * updates the association count of the pattern (Events occurred before 5
     * min to till this point @session considered as pattern)
     */
    public void updatePatterns() {

        try {
            JSONArray edgeObject = this.getPreviousMetricEvent();
            StringBuilder patern = new StringBuilder();
            long timewindow = AppConfig.getInstance().getInt("event.timewindow");
            for (int j = 0; j < edgeObject.length(); j++) {
                OrientEdge edge = this.getGraph().getEdge(edgeObject.get(j));

                    // Previous Event Timestamp
                    String preEventTimestamp = edge.getProperty(mappingEventkeys.get("timestamp"));
                    String currenttimestamp = this.getStringAttr(mappingEventkeys.get("timestamp"));
                    // long diff =
                    // Utils.getDateDiffInSec(Utils.parseEventDate(Long.parseLong(preEventTimestamp)),Utils.parseEventDate(Long.parseLong(currenttimestamp)));
                    long diff1 = Utils.getDateDiffInMIllisec(
                            Utils.parseEventDate(Long.parseLong(preEventTimestamp)),
                            Utils.parseEventDate(Long.parseLong(currenttimestamp)));

                    if (diff1 < timewindow)
                        patern.append(((OrientVertex) edge.getProperty("in")).getId()).append("_");
            }
            String patterType = patern.substring(0, patern.length() - 1);
            pattern = new Pattern(patterType, this.getGraph());

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    /**
     * Create an edge b/w RawMetricEvent (contains session info) & the pattern
     */
    public void associatePatternRawMetricEvent() {
        Object[] props = new Object[]{mappingEventkeys.get("timestamp"),
                this.getStringAttr(mappingEventkeys.get("timestamp"))};
        rawMetricEvent.addEdge(pattern, "Session_Pattern",props);
    }

    /**
     * Create an edge b/w RawMetricEvent (contains session info) & the Domain
     * Type
     */
    public void associateDomainRawMetricEvent() {
        Object[] props = new Object[]{mappingEventkeys.get("timestamp"),
                this.getStringAttr(mappingEventkeys.get("timestamp"))};
        rawMetricEvent.addEdge(domainevent, "Session_Domain",props);
    }

    /**
     * Creating an Edge b/w RawMetricEvent (contains session info) & ActionEvent
     * with timestamp , metric type as properties
     */
    public void associateRawMetricEvent(BaseModel event) {
        // Object[] props = new
        // Object[]{"timestamp",this.getStringAttr("timestamp"),"type",this.getStringAttr("type")};
        Object[] props = new Object[]{mappingEventkeys.get("timestamp"),
                this.getStringAttr(mappingEventkeys.get("timestamp")),
                mappingEventkeys.get("type"), this.getStringAttr(mappingEventkeys.get("type"))};
        rawMetricEvent.addEdge(event, "Metric_Event", props);
    }

}
