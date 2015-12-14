package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Transaction extends BaseModel {

    public Transaction(String transactionId, String hostname, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, transactionId, hostname);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Transaction");
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String transactionId, String hostname) {
        return OrientUtils.getVertex(graph, "select *  from Transaction where transactionId = '"
                + transactionId + "' and OUT('Transaction_Host')[0].hostname='" + hostname + "'");
    }
}
