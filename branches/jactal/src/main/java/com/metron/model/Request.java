package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Request extends BaseModel {
  
    public Request(Object requestId, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, requestId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Request");
        }
    }
    
    public static OrientVertex find(OrientBaseGraph graph, Object requestId) {
        OrientVertex request = OrientUtils.getVertex(graph,
                "select *  from Request where requestId = '" + requestId.toString() + "'");
        return request;
    }
    
    public Request getRequest() {
        return this;
    }
}
