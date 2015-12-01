package com.metron.model.event;

import com.metron.model.Domain;
import com.metron.model.Host;
import com.metron.model.User;


public class SessionEvent extends Event {
    
    protected Session session;
    protected Domain domain;
    protected User user;
    
    public SessionEvent(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
        session = new Session(this.getAttribute("sessionId").toString());
        host = new Host(this.getAttribute("hostname").toString());
        domain = new Domain(this.getAttribute("domainName").toString());
        user = new User(this.getAttribute("userName").toString());
        this.saveRawEvent(); // save the raw event with
        // eventid, timestamp
        this.associateRawEventToHost();
    }

    @Override
    public boolean isValid() {
        return super.isValid()
                && this.getEventValue("severity").toString().toLowerCase().equals("info");
    }

    protected void associateRawEvent() {
        session.addEdge(rawEvent, "Session_Event");
    }
    
    protected void associateUser() {

        session.addEdge(user, "Session_User");
    }

    protected void associateDomain() {

        session.addEdge(domain, "Session_Domain");
    }

    protected void associateHost() {

        session.addEdge(host, "Session_Host");
    }
    
}
