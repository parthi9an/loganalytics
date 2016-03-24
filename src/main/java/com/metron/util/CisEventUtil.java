package com.metron.util;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.CisEventMappings;
import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisEventUtil {

    public JSONObject getdetails(String rid) throws JSONException {
        // String sql = "select * from " + rid;
        // String data = new com.metron.orientdb.OrientRest().doSql(sql);

        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        OrientVertex eventvertex = graph.getVertex(rid);
        Map<String, Object> props = eventvertex.getProperties();

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
        }

        properties = getEventDetails(keys, props);

        graph.shutdown();
        return properties;
    }

    private JSONObject getEventDetails(List<String> keys, Map<String, Object> props) throws JSONException {

        JSONObject properties = new JSONObject();
        for (String key : keys) {
            if (props.containsKey(key)) {
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
                    .append(eventvertex.getProperty("view"));
        } else if (eventvertex.getLabel().compareTo("EnvironmentEvent") == 0) {
            paternclassdetails.append(eventvertex.getLabel()).append(";")
                    .append(eventvertex.getProperty("os"));
        }

        graph.shutdown();
        return paternclassdetails.toString();
    }

}
