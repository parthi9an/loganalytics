package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class FilterCritera extends BaseModel{

    public FilterCritera(HashMap<String, Object> filterProps,
            OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, filterProps);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:FilterCriteria");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(filterProps);
            this.setProperties(props);
            this.save();
        }
    }

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> filterProps) {
        StringBuilder sql = new StringBuilder("select * from FilterCriteria where ");

        String query = this.constructQuery(sql, filterProps);

        OrientVertex filter = OrientUtils.getVertex(graph, query);
        return filter;
    }
}
