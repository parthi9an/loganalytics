package com.metron.util;

import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.CisEventMappings;
import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisEventUtil {

    public JSONObject getdetails(String rid) throws JSONException {
        
        String sql = "select * from " + rid;
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        JSONObject result = new JSONObject(data.toString());
        JSONArray resultArr = result.getJSONArray("result");
        JSONObject props = new JSONObject();
        for(int j = 0; j < resultArr.length(); j++){
            props = resultArr.getJSONObject(j);
        }

        List<String> keys = null;
        JSONObject properties = new JSONObject();
        String eventName = props.get("@class").toString();

        if (eventName.compareTo("ActionEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("ActionEvent");
        } else if (eventName.compareTo("KeyBoardEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("KeyBoardEvent");
        } else if (eventName.compareTo("ViewEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("ViewEvent");
        } else if (eventName.compareTo("DomainEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("DomainEvent");
        } else if (eventName.compareTo("FieldEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("FieldEvent");
        } else if (eventName.compareTo("ErrorEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("ErrorEvent");
        } else if (eventName.compareTo("ConfigurationEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("ConfigurationEvent");
        } else if (eventName.compareTo("WindowEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("WindowEvent");
        } else if (eventName.compareTo("EnvironmentEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("EnvironmentEvent");
        } else if (eventName.compareTo("WindowScrollEvent") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("WindowScrollEvent");
        } else if (eventName.compareTo("ContextType") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("ContextType");
        } else if (eventName.compareTo("ViewContext") == 0) {
            keys = CisEventMappings.getInstance().getEventMapping("ViewContext");
        }

        properties = getEventDetails(keys, props);

        return properties;
    }
    
    private JSONObject getEventDetails(List<String> keys, JSONObject props) throws JSONException {

        JSONObject properties = new JSONObject();
        
        Iterator<String> iterator = keys.iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            if (props.has(key)) {
                if(key.compareToIgnoreCase("context") == 0)
                    properties.put(key, getdetails(props.get(key).toString()));
                else
                    properties.put(key, props.get(key));
            }
        }
        
        return properties;
    }

    public String getEventClass(String pattern) {

        String[] rid = pattern.split("_");
        StringBuilder paternclass = new StringBuilder();
        for (int i = 0; i < rid.length; i++) {
            // To Retrieve the Event Name
            // paternclass.append(graph.getVertex(rid[i]).getLabel()).append("_");

            // To retrieve Event Name along with one attribute
            paternclass.append(getBriefDetails(rid[i])).append("_");
        }

        String patterClass = paternclass.toString().substring(0,
                paternclass.toString().length() - 1);

        return patterClass;
    }

    private String getBriefDetails(String rid) {

        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        OrientVertex eventvertex = graph.getVertex(rid);
        StringBuilder paternclassdetails = new StringBuilder();

        if (eventvertex.getLabel().compareTo("ActionEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("key"));
        } else if (eventvertex.getLabel().compareTo("KeyBoardEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("command"));
        } else if (eventvertex.getLabel().compareTo("ViewEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("name"));
        } else if (eventvertex.getLabel().compareTo("DomainEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("domain_type"));
        } else if (eventvertex.getLabel().compareTo("FieldEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("field"));
        } else if (eventvertex.getLabel().compareTo("ErrorEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("err_type"));
        } else if (eventvertex.getLabel().compareTo("ConfigurationEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("name"));
        } else if (eventvertex.getLabel().compareTo("WindowEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("length"));
        } else if (eventvertex.getLabel().compareTo("EnvironmentEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("os"));
        } else if (eventvertex.getLabel().compareTo("WindowScrollEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
            .append(eventvertex.getProperty("orientation"));
        } 

        graph.shutdown();
        return paternclassdetails.toString();
    }

}
