package com.metron.service.interfaces;

import javax.jms.JMSException;

import org.json.JSONException;
import org.json.JSONObject;

public interface IJmsService {
	
	public JSONObject consumeMsg() throws JMSException, JSONException;

}
