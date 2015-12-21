package com.metron.model.event;

import com.metron.model.Domain;
import com.metron.model.Host;
import com.metron.model.Request;
import com.metron.model.Session;
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
        String parentId = this.getStringAttr("parentId");
        host = new Host(this.getStringAttr("hostname"), this.getGraph());
        domain = new Domain(this.getStringAttr("domainName"), this.getGraph());
        user = new User(this.getStringAttr("userName"), this.getGraph());
        session = new Session(this.getStringAttr("sessionId"), parentId, this.getGraph());
        request = new Request(this.getStringAttr("requestId"), parentId, this.getGraph());
        this.saveRawEvent(); 
        this.associateRawEventToHost();
        
    }

    @Override
    public boolean isValid() {
        return super.isValid()
                && (this.getEventValue("severity").toString().toLowerCase().equals("info") || this
                        .getEventValue("severity").toString().toLowerCase().equals("error"));
    }

    protected void associateSession() {
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

}
