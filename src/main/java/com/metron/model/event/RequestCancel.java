package com.metron.model.event;

import java.util.Date;
import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;

public class RequestCancel extends RequestEvent {

    public RequestCancel(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
          
        super.process();
        this.updateRequest();
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
        // create an association with timeWindow
        this.associateTimeWindow();

    }
    private void associateTimeWindow() {

        DURATION duration = DURATION.ONEMIN;
        this.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        this.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());
        
        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        this.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());
        
        // ONEDAY Window
        duration = DURATION.ONEDAY;
        this.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());
        
    }
    private void updateRequest() {

        // retrieve request object for requestId - create if does not exist.
        // populate requestId, startTime, status (started)
        String requestId = (String) this.getAttribute("requestId");
       // Request request = this.getRequest();
        
        
        HashMap<String, Object> props = new HashMap<String, Object>();
        if (request.vertex.getProperty("startTime") != null) {
            Date startTime = request.vertex.getProperty("startTime");
            props.put("delta", Utils.getDateDiffInMIllisec(startTime,
                    Utils.parseEventDate(this.getAttribute("timestamp").toString())));
        }
        props.put("requestId", requestId);
        props.put("status", this.getAttribute("status"));
        props.put(
                "endTime",
                OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(this.getAttribute(
                        "timestamp").toString())));
        
        props.put("bytesIn", this.getAttribute("bytesIn"));
        props.put("bytesOut", this.getAttribute("bytesOut"));
        props.put("rowsAffected", this.getAttribute("rowsAffected"));

        request.setProperties(props);
        request.save();
        //this.vertex = request.vertex;
    }

}
