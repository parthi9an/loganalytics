package com.metron.model.event;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.metron.util.Utils;

public class SessionStart extends SessionEvent {

    public SessionStart(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
        super.process();       
        this.saveSession();
        this.updateAssociations();

    }

    private void updateAssociations() {

        // create the association with the Raw Event
        this.associateRawEvent();
        // create the association with the TimeWindow
        this.associateUser();
        // create association with domain
        this.associateDomain();
        // create association with host
        this.associateHost();
    }

    private void saveSession() {

        // retrieve request object for requestId - create if does not exist.
        // populate requestId, startTime, status (started)
        String sessionId = this.getStringAttr("sessionId");

        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("sessionId", sessionId);
        props.put("parentId", this.getAttribute("parentId"));
        props.put(
                "startTime",
                OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(this.getStringAttr(
                        "timestamp"))));
        session.setProperties(props);
        session.save();
    }

}
