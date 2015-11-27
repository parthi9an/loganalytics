package com.metron.model.event;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.metron.util.Utils;

public class RequestStart extends RequestEvent {

    public RequestStart(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
        super.process();
        this.saveRequest();
        this.setVertex(request.vertex);
        this.updateAssociations();

    }

    private void updateAssociations() {

        // create associations with the user, domain, host, session, rawevent,
        // timewindow.
        // create the association with the Raw Event
        this.associateRawEvent();
        // create the association with the Session
        this.associateSession();
        // create the association with the TimeWindow
        this.associateUser();
        // create association with domain
        this.associateDomain();
        // create association with host
        this.associateHost();

    }

    private void saveRequest() {
        
        // retrieve request object for requestId - create if does not exist.
        // populate requestId, startTime, status (started)
        
        String requestId = (String) this.getAttribute("requestId");
     //   Request request = this.getRequest(); 
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("requestId", requestId);
        props.put(
                "startTime",
                OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(this.getAttribute(
                        "timestamp").toString())));
        props.put("status", this.getAttribute("status"));
        props.put("sqlQuery", this.getAttribute("sqlQuery"));

        request.setProperties(props);
        request.save();
       // this.vertex = request.vertex;
    }

}
