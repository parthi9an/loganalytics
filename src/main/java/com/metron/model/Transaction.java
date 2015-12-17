package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Transaction extends BaseModel {

    public Transaction(String transactionId, String parentId, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, transactionId, parentId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Transaction");
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String transactionId, String parentId) {
        return OrientUtils.getVertex(graph, "select *  from Transaction where transactionId = '"
                + transactionId + "' and parentId='" + parentId + "'");
    }
}
