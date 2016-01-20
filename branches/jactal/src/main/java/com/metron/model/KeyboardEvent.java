package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class KeyboardEvent extends BaseModel {
    
    public KeyboardEvent(String command, String target,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, command, target);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:KeyBoardEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("key_command", command);
            props.put("key_target", target);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String command, String target) {
        OrientVertex keyboardevent = OrientUtils.getVertex(graph,
                "select *  from KeyBoardEvent where key_command = '" + command
                        + "' and key_target='" + target + "'");
        return keyboardevent;
    }

}
