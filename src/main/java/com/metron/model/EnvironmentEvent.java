package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class EnvironmentEvent extends BaseModel{
    
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
        
        String query = this.constructQuery(sql, metricValueAttributes);
        
        OrientVertex envevent = OrientUtils.getVertex(graph,query);
        return envevent;
    }
}
