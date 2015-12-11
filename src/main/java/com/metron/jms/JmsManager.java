package com.metron.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.metron.AppConfig;

public class JmsManager {
	
	private static JmsManager _instance = null;
    public String brokerUrl = null;
    public String queue = null;

    public static JmsManager getInstance() {
        if (_instance == null) {
            _instance = new JmsManager();
        }
        return _instance;
    }
    
    JmsManager() {
    	brokerUrl = AppConfig.getInstance().getString("jms.brokerUrl");
    	queue =  AppConfig.getInstance().getString("jms.queue");
    }
    
    private static ActiveMQConnectionFactory connectionfactory;
    
    public ActiveMQConnectionFactory getFactory(){
    	
    	if(connectionfactory == null){
    		connectionfactory = new ActiveMQConnectionFactory(brokerUrl);
    	}
    	return connectionfactory;
    }
    
    private static Connection connection;
    
    public Connection getConnection() throws JMSException{
    	if(connection == null){
    		connection = this.getFactory().createConnection();
    		connection.start();
    	}
    	return connection;
    }
    
    private static Session session;
    public Session getSession() throws JMSException {
    	if(session == null){
    		session = this.getConnection().createSession(false,Session.AUTO_ACKNOWLEDGE);
    	}
    	return session;
    }
    
    public void close() throws JMSException{
        if(connection != null){
            connection.stop();
        }
    }
    
}
