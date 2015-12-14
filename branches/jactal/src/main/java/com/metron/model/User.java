package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author satheesh
 */

public class User extends BaseModel {

    public User(Object userName, OrientBaseGraph graph) {
        super(graph);
        if (userName != null) {
            this.vertex = find(graph, userName);
            if (vertex == null) {
                this.vertex = graph.addVertex("class:User");
                HashMap<String, Object> props = new HashMap<String, Object>();
                props.put("name", userName);
                this.setProperties(props);
                this.save();
            }
        }
    }
    public OrientVertex find(OrientBaseGraph graph, Object userName) {
        return OrientUtils.getVertex(graph, "select *  from User where name = '" + userName.toString() + "'");
    }
}
