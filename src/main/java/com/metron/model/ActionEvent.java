package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ActionEvent extends BaseModel {

    public ActionEvent(String key, String command, String view,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, key, command, view);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ActionEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("action_key", key);
            props.put("action_command", command);
            props.put("action_view", view);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String key, String command, String view) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from ActionEvent where action_key = '" + key
                        + "' and action_command='" + command + "' and action_view='" + view + "'");
        return actionevent;
    }

}
