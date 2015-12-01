package com.metron.model.event;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Transaction extends  BaseModel {
    
    public Transaction(String transactionId) {
        OrientBaseGraph graph = this.getGraph();
        this.vertex = find(graph, transactionId);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Transaction");
        }
    }
    
    public OrientVertex find(OrientBaseGraph graph, String transactionId) {
        return OrientUtils.getVertex(graph,
                "select *  from Transaction where transactionId = '" + transactionId + "'");
    }
}
