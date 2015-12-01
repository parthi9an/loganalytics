package com.metron.model;

import java.util.HashMap;

import com.metron.model.event.BaseModel;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author satheesh
 */

public class Host extends BaseModel {

    public Host(String hostName) {
        OrientBaseGraph graph = this.getGraph();
        this.vertex = find(graph, hostName);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Host");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("hostname", hostName);
            this.setProperties(props);
            this.save();
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String hostName) {
        return OrientUtils.getVertex(graph, "select *  from Host where hostname = '" + hostName
                + "'");
    }

}
