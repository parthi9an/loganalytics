package com.metron.model;

import java.util.HashMap;

import com.metron.model.event.BaseModel;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
@author satheesh
 */

public class Domain extends BaseModel{

    public Domain(String domainName) {
        OrientBaseGraph graph = this.getGraph();
        this.vertex = find(graph, domainName);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Domain");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("name", domainName);
            this.setProperties(props);
            this.save();
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String domainName) {
        return OrientUtils.getVertex(graph, "select *  from Domain where name = '"
                + domainName + "'");
    }
}
