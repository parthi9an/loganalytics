package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Session extends BaseModel {

    public Session(String sessionId, String parentId, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, sessionId, parentId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Session");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("sessionId", sessionId);
            props.put("parentId", parentId);
            this.setProperties(props);
            this.save();
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String sessionId, String parentId) {
        return OrientUtils.getVertex(graph, "select *  from Session where sessionId = '"
                + sessionId + "' and parentId ='" + parentId + "'");
    }

}
