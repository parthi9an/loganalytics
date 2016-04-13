package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ContextType extends BaseModel {

    public OrientVertex contexttype;
    
    public ContextType(Map<String, Object> contextType, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, contextType);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ContextType");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(contextType);
            this.setProperties(props);
            this.save();
        }
    }

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> contextType) {
        StringBuilder sql = new StringBuilder("select * from ContextType where ");

        String query = this.constructQuery(sql, contextType);

        contexttype = OrientUtils.getVertex(graph, query);
        return contexttype;
    }

}
