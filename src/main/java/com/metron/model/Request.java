package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Request extends BaseModel {

    public Request(String requestId, String parentId, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, requestId, parentId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Request");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("requestId", requestId);
            props.put("parentId", parentId);
            this.setProperties(props);
            this.save();
        }
    }

    public static OrientVertex find(OrientBaseGraph graph, String requestId, String parentId) {
        OrientVertex request = OrientUtils.getVertex(graph,
                "select *  from Request where requestId = '" + requestId
                        + "' and parentId='" + parentId + "'");
        return request;
    }

    public Request getRequest() {
        return this;
    }
}
