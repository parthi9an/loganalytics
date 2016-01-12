package com.metron.model.event;

import java.util.Date;
import java.util.HashMap;

import com.metron.model.Request;
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

        // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        request.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        request.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        request.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        request.addEdge(this.getTimeWindow(duration), "Request_" + duration.getTable());

    }
    private void updateRequest() {

        // retrieve request object for requestId - create if does not exist.
        // populate requestId, startTime, status (started)
        String requestId = this.getStringAttr("requestId");

        HashMap<String, Object> props = new HashMap<String, Object>();
        
        System.out.println("RequestCancle: props \t starttime is" + request.vertex.getProperty("startTime"));
        
        if(request.vertex.getProperty("startTime") == null){
            String parentId = this.getStringAttr("parentId");
            request = new Request(this.getStringAttr("requestId"), parentId, this.getGraph());
            System.out.println("RequestCancle: props retry \t starttime is" + request.vertex.getProperty("startTime"));
        }
        
        if (request.vertex.getProperty("startTime") != null) {
            Date startTime = request.vertex.getProperty("startTime");
            props.put(
                    "delta",
                    Utils.getDateDiffInMIllisec(startTime,
                            Utils.parseEventDate(this.getStringAttr("timestamp"))));
            System.out.println("request & parentid \t" + request.vertex.getProperty("requestId") + "\t"
                    + request.vertex.getProperty("parentId"));
            System.out.println("start & end Timestamp \t" + startTime + "\t" + this.getStringAttr("timestamp"));
            System.out.println("RequestCancle: delta \t"+ Utils.getDateDiffInMIllisec(startTime,
                    Utils.parseEventDate(this.getStringAttr("timestamp"))));
        }
        props.put("requestId", requestId);
        props.put("status", this.getAttribute("status"));
        props.put("endTime", OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(this
                .getStringAttr("timestamp"))));

        props.put("bytesIn", this.getAttribute("bytesIn"));
        props.put("bytesOut", this.getAttribute("bytesOut"));
        props.put("rowsAffected", this.getAttribute("rowsAffected"));
        try {
            request.setProperties(props);
            request.save();
        } catch (Exception e) {
            System.out.println("RequestCancle : Error while saving the Request");
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
