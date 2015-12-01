package com.metron.service;

import java.util.HashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import com.metron.jms.JmsManager;
import com.metron.orientdb.OrientDBGraphManager;
import com.metron.service.interfaces.IJmsService;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class JmsService implements IJmsService,Runnable {

	public JSONObject consumeMsg() throws JMSException, JSONException {
		
		Session session = JmsManager.getInstance().getSession();

		String queuename = JmsManager.getInstance().queue;
		
		JSONObject result = new JSONObject();
		
		Destination destination = session.createQueue(queuename);
		
		MessageConsumer consumer = session.createConsumer(destination);
        int i = 1;
        while (true) {
            //Message message = consumer.receive(1000);
        	Message message = consumer.receive();
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Received: " + text);
                result.put("message" + i, text);
                this.saveEvents(text);
              
            } else {
                System.out.println("Received: " + message);
                result.put("message" + i, message);
                //break;
            }
            i++;
        }
        
        /*result.put("status", "Success");
        return result;*/

	}

    private void saveEvents(String msg) {
        
        JSONObject jo = null;
        try {
            jo = new JSONObject(msg);
        } catch (JSONException e) {
            
            e.printStackTrace();
        }
        
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        OrientVertex vertex = graph.addVertex("class:CisEvents");
        HashMap<String, Object> propsWarning = new HashMap<String, Object>();
        try {
            propsWarning.put("eventNumber", jo.get("eventnumber") );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            propsWarning.put("eventType", jo.get("eventtype"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        vertex.setProperties(propsWarning);
        vertex.save();
        
    }
    
    public void run() {
        JSONObject result = new JSONObject();
        try {
            this.consumeMsg();
        
        } catch (Exception e) {
            e.printStackTrace();
            try {
                result.put("status", "Failed");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        
    }

}
