package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Session extends BaseModel {

    public Session(String sessionId, String hostname, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, sessionId, hostname);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Session");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String sessionId, String hostname) {
        return OrientUtils.getVertex(graph, "select *  from Session where sessionId = '"
                + sessionId + "' and OUT('Session_Host')[0].hostname='" + hostname + "'");
    }

}
