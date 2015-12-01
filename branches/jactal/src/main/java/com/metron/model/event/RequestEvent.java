package com.metron.model.event;

import com.metron.model.Domain;
import com.metron.model.Host;
import com.metron.model.User;

public class RequestEvent extends Event {
    
    protected Session session;
    protected Request request;
    protected Domain domain;
    protected User user;
    
    public RequestEvent(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
        
        request = new Request(this.getAttribute("requestId").toString());
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
                && (this.getEventValue("severity").toString().toLowerCase().equals("info") || this
                        .getEventValue("severity").toString().toLowerCase().equals("error"));
    }

    protected void associateSession() {
//        this.addEdge(new Session(this.getAttribute("sessionId").toString(), this.getGraph()),
//                "Request_Session");
        request.addEdge(session, "Request_Session");
    }
    protected void associateRawEvent() {
        request.addEdge(rawEvent, "Request_Event");
    }

    protected void associateUser() {

        request.addEdge(user, "Request_User");
    }

    protected void associateDomain() {

        request.addEdge(domain, "Request_Domain");
    }

    protected void associateHost() {

        request.addEdge(host, "Request_Host");
    }

    // public Request getRequest() {
    // String requestId = (String) this.getAttribute("requestId");
    // if (request == null) {
    // request = new Request(requestId, this.getGraph());
    // }
    // return request;
    // }

}
