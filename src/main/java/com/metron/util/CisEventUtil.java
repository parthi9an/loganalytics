package com.metron.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.metron.orientdb.OrientDBGraphManager;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisEventUtil {

    public JSONObject getdetails(String rid) throws JSONException {
        //String sql = "select * from " + rid;
        //String data = new com.metron.orientdb.OrientRest().doSql(sql);
        
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        OrientVertex eventvertex = graph.getVertex(rid);
        //Map<String, Object> props =  eventvertex.getProperties();
        //JSONObject properties = new JSONObject(props);
        
        JSONObject properties = new JSONObject();
        
        if(eventvertex.getLabel().compareTo("ActionEvent") == 0){
            properties.put("action_key", eventvertex.getProperty("action_key"));
            properties.put("action_command", eventvertex.getProperty("action_command"));
            properties.put("action_view", eventvertex.getProperty("action_view"));
        }else if (eventvertex.getLabel().compareTo("KeyBoardEvent") == 0){
            properties.put("key_command", eventvertex.getProperty("key_command"));
            properties.put("key_target", eventvertex.getProperty("key_target"));
        }else if (eventvertex.getLabel().compareTo("ViewEvent") == 0){
            properties.put("view_name", eventvertex.getProperty("view_name"));
            properties.put("view_event_type", eventvertex.getProperty("view_event_type"));
        }else if (eventvertex.getLabel().compareTo("DomainEvent") == 0){
            properties.put("domain_type", eventvertex.getProperty("domain_type"));
        }else if (eventvertex.getLabel().compareTo("FieldEvent") == 0){
            properties.put("field_name", eventvertex.getProperty("field_name"));
            properties.put("field_parent", eventvertex.getProperty("field_parent"));
        }else if (eventvertex.getLabel().compareTo("ErrorEvent") == 0){
            properties.put("error_type", eventvertex.getProperty("error_type"));
            properties.put("error_message", eventvertex.getProperty("error_message"));
            properties.put("error_trace", eventvertex.getProperty("error_trace"));
        }else if (eventvertex.getLabel().compareTo("ConfigurationEvent") == 0){
            properties.put("config_name", eventvertex.getProperty("config_name"));
        }else if (eventvertex.getLabel().compareTo("WindowEvent") == 0){
            properties.put("window_length", eventvertex.getProperty("window_length"));
            properties.put("window_height", eventvertex.getProperty("window_height"));
            properties.put("window_view", eventvertex.getProperty("window_view"));
        }else if (eventvertex.getLabel().compareTo("EnvironmentEvent") == 0){
            properties.put("env_os", eventvertex.getProperty("env_os"));
            properties.put("env_screen_length", eventvertex.getProperty("env_screen_length"));
            properties.put("env_screen_height", eventvertex.getProperty("env_screen_height"));
            properties.put("env_app_length", eventvertex.getProperty("env_app_length"));
            properties.put("env_app_height", eventvertex.getProperty("env_app_height"));
            properties.put("env_browser_type", eventvertex.getProperty("env_browser_type"));
            properties.put("env_browser_version", eventvertex.getProperty("env_browser_version"));
            properties.put("env_cpu_name", eventvertex.getProperty("env_cpu_name"));
            properties.put("env_cpu_clock", eventvertex.getProperty("env_cpu_clock"));
            properties.put("env_cpu_cores", eventvertex.getProperty("env_cpu_cores"));
            properties.put("env_mem", eventvertex.getProperty("env_mem"));
        }
        
        
        return properties;
    }

    public String getEventClass(String pattern) {
        
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        
        String[] rid=pattern.split("_");
        StringBuilder paternclass = new StringBuilder();
        for(int i=0;i<rid.length;i++)
        {
            paternclass.append(graph.getVertex(rid[i]).getLabel()).append("_");
        }
        
        String patterClass = paternclass.toString().substring(0, paternclass.toString().length()-1);
        
        return patterClass;
    }

}
