package com.metron.service;

import java.sql.SQLException;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.EventFactory;
import com.metron.model.event.CisEvent;
import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public class JmsService implements MessageListener {

    /**
     * consume events from jms queue (ActiveMq)
     */
    public void onMessage(Message m) {
        TextMessage message = (TextMessage) m;
        try {
            System.out.println(message.getText());
            this.saveEvent(message.getText());
            //message.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the message/event in orientDB
     *
     * @param  message/event to save in orientDB
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    private void saveEvent(String msg) throws JSONException, SQLException {

        JSONObject jo = new JSONObject(msg);

        CisEvent event = EventFactory.getInstance().parseCISEvent(jo);
        if (event != null) {
            OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
            event.setGraph(graph);
            event.process();
            event.getGraph().shutdown();
        }

    }

}
