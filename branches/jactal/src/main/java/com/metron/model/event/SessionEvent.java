package com.metron.model.event;


public class SessionEvent extends Event {
    
    public SessionEvent(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
        this.saveRawEvent(); // save the raw event with
        // eventid, timestamp
        this.associateRawEventToHost();
        
        session = new Session(this.getAttribute("sessionId").toString(), this.getGraph());

    }

    @Override
    public boolean isValid() {
        return super.isValid()
                && this.getEventValue("severity").toString().toLowerCase().equals("info");
    }

    protected void associateRawEvent() {
        this.addEdge(this.rawEvent, "Session_Event");
    }
    
    protected void associateUser() {

        this.addEdge(this.getUser(), "Session_User");
    }

    protected void associateDomain() {

        this.addEdge(this.getDomain(), "Session_Domain");
    }

    protected void associateHost() {

        this.addEdge(this.getHost(), "Session_Host");
    }
    
}
