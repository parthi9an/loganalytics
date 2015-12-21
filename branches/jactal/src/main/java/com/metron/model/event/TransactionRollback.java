package com.metron.model.event;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.Domain;
import com.metron.model.Host;
import com.metron.model.Session;
import com.metron.model.Transaction;
import com.metron.model.User;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;

public class TransactionRollback extends Event {

    protected Transaction transaction;
    protected Session session;
    protected Domain domain;
    protected User user;

    public TransactionRollback(String[] eventData) {
        super(eventData);
    }

    public void setHost(String host) {
        this.setAttribute("hostname", host);
    }

    @Override
    public void process() {
        String parentId = this.getStringAttr("parentId");
        host = new Host(this.getStringAttr("hostname"), this.getGraph());
        domain = new Domain(this.getStringAttr("domainName"), this.getGraph());
        user = new User(this.getStringAttr("userName"), this.getGraph());
        this.saveRawEvent(); 
        this.associateRawEventToHost();
        session = new Session(this.getStringAttr("sessionId"), parentId, this.getGraph());
        transaction = new Transaction(this.getStringAttr("transactionId"), parentId,
                this.getGraph());
        this.saveTransaction();
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
        // create association with Host
        this.associateHost();
        // create an association with timeWindow
        this.associateTimeWindow();

    }

    protected void associateSession() {

        transaction.addEdge(session, "Transaction_session");
    }

    protected void associateRawEvent() {

        transaction.addEdge(rawEvent, "Transaction_Event");
    }

    protected void associateUser() {

        transaction.addEdge(user, "Transaction_User");
    }

    protected void associateDomain() {

        transaction.addEdge(domain, "Transaction_Domain");
    }

    protected void associateHost() {

        transaction.addEdge(host, "Transaction_Host");
    }

    private void associateTimeWindow() {

        // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        transaction.addEdge(this.getTimeWindow(duration), "Transaction_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        transaction.addEdge(this.getTimeWindow(duration), "Transaction_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        transaction.addEdge(this.getTimeWindow(duration), "Transaction_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        transaction.addEdge(this.getTimeWindow(duration), "Transaction_" + duration.getTable());

    }

    private void saveTransaction() {

        String transactionId = this.getStringAttr("transactionId");

        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("transactionId", transactionId);
        props.put("parentId", this.getAttribute("parentId"));
        props.put("status", this.getAttribute("status"));
        props.put("timestamp", Utils.parseEventDate(this.getStringAttr("timestamp")));
        transaction.setProperties(props);
        transaction.save();
    }

    private String getHostName() {
        // get the session for this transaction
        // get the host associted with this session and return the host
        String sql = "select OUT('Session_Host')[0].hostname as hostname from Session where sessionId='"
                + this.getStringAttr("sessionId") + "'";
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        String hostName = null;
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            hostName = resultArr.getJSONObject(0).getString("hostname");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return hostName;
    }

}
