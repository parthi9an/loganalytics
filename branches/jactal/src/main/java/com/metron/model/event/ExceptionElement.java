package com.metron.model.event;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ExceptionElement extends BaseModel {
    
    public ExceptionElement(String element) {
        OrientBaseGraph graph = this.getGraph();
        this.vertex = find(graph, element);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ExceptionElement");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String element) {
        return OrientUtils.getVertex(graph, "select *  from ExceptionElement where value = '"
                + element + "'");
    }
}
