package com.metron.model.event;

public class RequestEvent extends Event {

    public RequestEvent(String[] eventData) {
        super(eventData);
    }

    @Override
    public void process() {
        this.saveRawEvent(); // save the raw event with
        // eventid, timestamp
        this.associateRawEventToHost();

        request = new Request(this.getAttribute("requestId").toString(), this.getGraph());
        session = new Session(this.getAttribute("sessionId").toString(), this.getGraph());
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
        this.addEdge(session, "Request_Session");
    }
    protected void associateRawEvent() {
        this.addEdge(this.rawEvent, "Request_Event");
    }

    protected void associateUser() {

        this.addEdge(this.getUser(), "Request_User");
    }

    protected void associateDomain() {

        this.addEdge(this.getDomain(), "Request_Domain");
    }

    protected void associateHost() {

        this.addEdge(this.getHost(), "Request_Host");
    }

    // public Request getRequest() {
    // String requestId = (String) this.getAttribute("requestId");
    // if (request == null) {
    // request = new Request(requestId, this.getGraph());
    // }
    // return request;
    // }

}
