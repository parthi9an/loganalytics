package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Request extends BaseModel {

    public Request(String requestId, String hostname, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, requestId, hostname);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Request");
        }
    }

    public static OrientVertex find(OrientBaseGraph graph, String requestId, String hostname) {
        OrientVertex request = OrientUtils.getVertex(graph,
                "select *  from Request where requestId = '" + requestId
                        + "' and OUT('Request_Host')[0].hostname='" + hostname + "'");
        return request;
    }

    public Request getRequest() {
        return this;
    }
}
