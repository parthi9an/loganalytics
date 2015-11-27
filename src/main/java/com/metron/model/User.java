package com.metron.model;

import com.metron.model.event.BaseModel;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author satheesh
 */

public class User extends BaseModel{
    
    public User(String userName, OrientBaseGraph graph) {
        this.vertex = find(graph, userName);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:User");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String userName) {
        return OrientUtils.getVertex(graph, "select *  from User where name = '"
                + userName + "'");
    }
}
