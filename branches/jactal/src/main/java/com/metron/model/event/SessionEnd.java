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

        // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        session.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        session.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        session.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        session.addEdge(this.getTimeWindow(duration), "Session_" + duration.getTable());

    }

    private void updateSession() {

        String sessionId = this.getStringAttr("sessionId");

        HashMap<String, Object> props = new HashMap<String, Object>();
        if (session.vertex.getProperty("startTime") != null) {
            Date startTime = session.vertex.getProperty("startTime");
            props.put(
                    "delta",
                    Utils.getDateDiffInMIllisec(startTime,
                            Utils.parseEventDate(this.getStringAttr("timestamp"))));
            log.debug("session & parentid" + session.vertex.getProperty("sessionId") + "\t"
                    + session.vertex.getProperty("parentId"));
            log.debug("start & end Timestamp" + startTime + "\t" + this.getStringAttr("timestamp"));
        }

        props.put("sessionId", sessionId);
        props.put("endTime", OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(this
                .getStringAttr("timestamp"))));
        try {
            session.setProperties(props);
            session.save();
        } catch (Exception e) {
            log.error("SessionEnd : Error while saving the session");
            e.printStackTrace();
        }
    }
}
