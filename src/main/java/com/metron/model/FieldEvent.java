package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class FieldEvent extends BaseModel {
    
    public FieldEvent(String name, String field_parent,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, name, field_parent);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:FieldEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("field_name", name);
            props.put("field_parent", field_parent);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String name, String field_parent) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from FieldEvent where field_name = '" + name
                        + "' and field_parent='" + field_parent + "'");
        return actionevent;
    }

}
