package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ErrorEvent extends BaseModel {
    
    public ErrorEvent(String type, String message, String trace,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, type, message, trace);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ErrorEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("error_type", type);
            props.put("error_message", message);
            props.put("error_trace", trace);
            props.put("error_trace_checksum", org.apache.commons.codec.digest.DigestUtils.md5Hex(trace));
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String type, String message, String trace) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from ErrorEvent where error_type = '" + type
                        + "' and error_message='" + message + "' and error_trace='" + trace + "'");
        return actionevent;
    }

}
