package com.metron.model;

import java.util.HashMap;

import com.metron.model.event.BaseModel;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author satheesh
 */

public class User extends BaseModel{
    
    public User(String userName) {
        OrientBaseGraph graph = this.getGraph();
        this.vertex = find(graph, userName);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:User");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("userName", userName);
            this.setProperties(props);
            this.save();
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String userName) {
        return OrientUtils.getVertex(graph, "select *  from User where name = '"
                + userName + "'");
    }
}
