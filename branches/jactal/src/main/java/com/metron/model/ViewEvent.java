package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ViewEvent extends BaseModel{
    
    public ViewEvent(String name, String event_type,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, name, event_type);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ViewEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("view_name", name);
            props.put("view_event_type", event_type);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String name, String event_type) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from ViewEvent where view_name = '" + name
                        + "' and view_event_type='" + event_type + "'");
        return actionevent;
    }

}
