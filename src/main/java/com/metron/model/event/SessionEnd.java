package com.metron.model.event;

import java.util.Date;
import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;

public class SessionEnd extends SessionEvent {

    public SessionEnd(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
        super.process();
        this.updateSession();
        this.setVertex(session.vertex);
        this.updateAssociations();

    }

    private void updateAssociations() {

        // create the association with the Raw Event
        this.associateRawEvent();
        // create the association with the TimeWindow
        this.associateTimeWindow();
        // create the association with the User
        this.associateUser();
        // create association with domain
        this.associateDomain();
        // create association with host
        this.associateHost();
    }

    private void associateTimeWindow() {

        DURATION duration = DURATION.ONEMIN;
        this.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        this.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        this.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        this.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

    }

    private void updateSession() {

        String sessionId = (String) this.getAttribute("sessionId");
       // Session session = new Session(sessionId, this.getGraph());

        HashMap<String, Object> props = new HashMap<String, Object>();
        if (session.vertex.getProperty("startTime") != null) {
            Date startTime = session.vertex.getProperty("startTime");
            props.put("delta", Utils.getDateDiffInMIllisec(startTime,
                    Utils.parseEventDate(this.getAttribute("timestamp").toString())));
        }
        
        props.put("sessionId", sessionId);
        props.put(
                "endTime",
                OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(this.getAttribute(
                        "timestamp").toString())));
        session.setProperties(props);
        session.save();
       // this.vertex = session.vertex;
    }
}
