package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Transaction extends BaseModel {

    public Transaction(Object transactionId, Object hostname, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, transactionId, hostname);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Transaction");
        }
    }

    public OrientVertex find(OrientBaseGraph graph, Object transactionId, Object hostname) {
        return OrientUtils.getVertex(graph, "select *  from Transaction where transactionId = '"
                + transactionId.toString() + "' and OUT('Transaction_Host')[0].hostname='"
                + hostname.toString() + "'");
    }
}
