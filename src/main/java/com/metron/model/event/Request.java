package com.metron.model.event;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Request extends BaseModel {
  
    public Request(String requestId) {
        OrientBaseGraph graph = this.getGraph();
        this.vertex = find(graph, requestId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Request");
        }
    }
    
    public static OrientVertex find(OrientBaseGraph graph, String requestId) {
        OrientVertex request = OrientUtils.getVertex(graph,
                "select *  from Request where requestId = '" + requestId + "'");
        return request;
    }
    
    public Request getRequest() {
        return this;
    }
}
