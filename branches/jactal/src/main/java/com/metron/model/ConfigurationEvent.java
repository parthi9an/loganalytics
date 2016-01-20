package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ConfigurationEvent extends BaseModel{
    
    public ConfigurationEvent(String name,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, name);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ConfigurationEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("config_name", name);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String name) {
        OrientVertex configevent = OrientUtils.getVertex(graph,
                "select *  from ConfigurationEvent where config_name = '" + name
                        + "'");
        return configevent;
    }

}
