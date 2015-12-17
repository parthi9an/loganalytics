package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Session extends BaseModel {

    public Session(String sessionId, String parentId, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, sessionId, parentId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Session");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String sessionId, String parentId) {
        return OrientUtils.getVertex(graph, "select *  from Session where sessionId = '"
                + sessionId + "' and parentId ='" + parentId + "'");
    }

}
