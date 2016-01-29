package com.metron.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class EnvironmentEvent extends BaseModel{
    
    public EnvironmentEvent(String os, String screen_length, String screen_height,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, os, screen_length, screen_height);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:EnvironmentEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("env_os", os);
            props.put("env_screen_length", screen_length);
            props.put("env_screen_height", screen_height);
            this.setProperties(props);
            this.save();
        }
    }
    
    public EnvironmentEvent(Map<String, Object> metricValueAttributes,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, metricValueAttributes);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:EnvironmentEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(metricValueAttributes);
            this.setProperties(props);
            this.save();
        }
    }

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> metricValueAttributes) {
        
        StringBuilder sql = new StringBuilder("select * from EnvironmentEvent where ");
        
        for (Iterator<Entry<String, Object>> iter = metricValueAttributes.entrySet().iterator(); iter.hasNext();) {
            Entry<String, Object> pair = iter.next();
            sql.append(pair.getKey());
            sql.append("= '");
            sql.append(pair.getValue());
            sql.append("'");

            if (iter.hasNext()) {
                sql.append(" and ");
            }
        }
        
        OrientVertex envevent = OrientUtils.getVertex(graph,sql.toString());
        return envevent;
    }

    public OrientVertex find(OrientBaseGraph graph, String os, String screen_length, String screen_height) {
        OrientVertex envevent = OrientUtils.getVertex(graph,
                "select *  from EnvironmentEvent where env_os = '" + os
                        + "' and env_screen_length='" + screen_length + "' and env_screen_height='" + screen_height + "'");
        return envevent;
    }
    
    

}
