package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author satheesh
 */

public class Host extends BaseModel {

    public Host(String hostName, OrientBaseGraph graph) {
        super(graph);
        if (hostName != null) {
            this.vertex = find(graph, hostName);
            if (vertex == null) {
                this.vertex = graph.addVertex("class:Host");
                HashMap<String, Object> props = new HashMap<String, Object>();
                props.put("hostname", hostName);
                this.setProperties(props);
                this.save();
            }
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String hostName) {
        return OrientUtils.getVertex(graph,
                "select *  from Host where hostname = '" + hostName + "'");
    }
    
    public void update(HashMap<String, Object> props){
        if(vertex != null) {
            this.setProperties(props);
            this.save();            
        }
    }

}
