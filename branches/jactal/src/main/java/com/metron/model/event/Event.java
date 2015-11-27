package com.metron.model.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metron.model.Domain;
import com.metron.model.EventMappings;
import com.metron.model.Host;
import com.metron.model.User;
import com.metron.util.Utils;
import com.metron.util.TimeWindowUtil.DURATION;

public abstract class Event extends BaseModel {

    protected RawEvent rawEvent;

    protected Session session;
    
    protected Request request;
    
    protected Transaction transaction;
    
    protected Host host;

    private Domain domain;

    private User user;

    protected Logger log = LoggerFactory.getLogger(Event.class);

    private Map<String, Object> attributes = null;
    private Map<String, Integer> mapping;

    private String[] eventData = null;

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
        String eventId = (this.getAttribute("eventId") != null) ? this.getAttribute(
                "eventId").toString() : null;
        this.rawEvent = new RawEvent(eventId, this.getGraph());
        this.rawEvent.setProperties(new HashMap<String, Object>(this.getAttributes()));
        this.rawEvent.save();
    }
    protected void associateRawEventToHost() {
        this.rawEvent.addEdge(getHost(), "Event_Host");
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

    public User getUser() {

        String userName = (String) this.getAttribute("userName");
        if (user == null) {
            user = new User(userName, this.getGraph());
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("name", userName);
            user.setProperties(props);
            user.save();
        }
        return user;
    }

    public Domain getDomain() {

        String domainName = (String) this.getAttribute("domainName");
        if (domain == null) {
            domain = new Domain(domainName, this.getGraph());
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("name", domainName);
            domain.setProperties(props);
            domain.save();
        }
        return domain;
    }

    public Host getHost() {

        String hostName = (String) this.getAttribute("hostname");
        if (host == null) {
            host = new Host(hostName, this.getGraph());
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("hostname", hostName);
            host.setProperties(props);
            host.save();
        }
        return host;
    }

    public void updateHost() {
        HashMap<String, Object> props = null;
        if (host != null) {
            props = new HashMap<String, Object>();
            props.put("OS", this.getAttribute("Operating_System"));
            props.put("numOfProcessors", this.getAttribute("Number_of_processors"));
            props.put("totalMemory", this.getAttribute("Total_Memory"));
            host.setProperties(props);
            host.save();
        }

    }

    public TimeWindow getTimeWindow(DURATION duration) {
        Date date = Utils.parseEventDate((String) this.getAttribute("timestamp"));
        return new TimeWindow(date, duration, this.getGraph());
    }

    @Override
    public String toString() {
        return new JSONObject(this.attributes).toString();
    }

}
