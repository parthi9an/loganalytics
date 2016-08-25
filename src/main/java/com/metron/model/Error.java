package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class Error extends BaseModel {
    
    public Error(String errorValue, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, errorValue);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Error");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String errorValue) {
        return OrientUtils.getVertex(graph, "select *  from Error where value = '"
                + errorValue + "'");
    }
}
