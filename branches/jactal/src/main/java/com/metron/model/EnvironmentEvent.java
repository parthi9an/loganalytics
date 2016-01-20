package com.metron.model;

import java.util.HashMap;

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

    public OrientVertex find(OrientBaseGraph graph, String os, String screen_length, String screen_height) {
        OrientVertex envevent = OrientUtils.getVertex(graph,
                "select *  from EnvironmentEvent where env_os = '" + os
                        + "' and env_screen_length='" + screen_length + "' and env_screen_height='" + screen_height + "'");
        return envevent;
    }

}
