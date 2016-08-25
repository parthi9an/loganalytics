package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ViewContext extends BaseModel {
    
    public ViewContext(Map<String, Object> contextAttributes, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, contextAttributes);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ViewContext");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(contextAttributes);
            this.setProperties(props);
            this.save();
        }
    }
    
    public OrientVertex find(OrientBaseGraph graph, Map<String, Object> contextAttributes) {
        StringBuilder sql = new StringBuilder("select * from ViewContext where ");

        String query = this.constructQuery(sql, contextAttributes);

        OrientVertex viewcontext = OrientUtils.getVertex(graph, query);
        return viewcontext;
    }
    
}
