package com.metron.model.event;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Session extends BaseModel{
    
    public Session(String sessionId, OrientBaseGraph graph) {
        this.vertex = find(graph, sessionId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Session");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String sessionId) {
        return OrientUtils.getVertex(graph, "select *  from Session where sessionId = '" + sessionId
                + "'");
    }

}