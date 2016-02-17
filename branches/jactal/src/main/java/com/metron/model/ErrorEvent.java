package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ErrorEvent extends BaseModel {
    
    public ErrorEvent(Map<String, Object> metricValueAttributes,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, metricValueAttributes);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ErrorEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(metricValueAttributes);
            props.put("error_trace_checksum", org.apache.commons.codec.digest.DigestUtils.md5Hex(metricValueAttributes.get("trace").toString()));
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, Map<String, Object> metricValueAttributes) {
        StringBuilder sql = new StringBuilder("select * from ErrorEvent where ");

        String query = this.constructQuery(sql, metricValueAttributes);

        OrientVertex errorevent = OrientUtils.getVertex(graph, query);
        return errorevent;
    }

}
