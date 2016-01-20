package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class DomainEvent extends BaseModel {
    
    public DomainEvent(String domain_type,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, domain_type);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:DomainEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("domain_type", domain_type);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String domain_type) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from DomainEvent where domain_type = '" + domain_type
                        +  "'");
        return actionevent;
    }

}
