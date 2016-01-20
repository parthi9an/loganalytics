package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class WindowEvent extends BaseModel{
    
    public WindowEvent(String length, String height, String view,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, length, height, view);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:WindowEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("window_length", length);
            props.put("window_height", height);
            props.put("window_view", view);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String length, String height, String view) {
        OrientVertex windowevent = OrientUtils.getVertex(graph,
                "select *  from WindowEvent where window_length = '" + length
                        + "' and window_height='" + height + "' and window_view='" + view + "'");
        return windowevent;
    }

}
