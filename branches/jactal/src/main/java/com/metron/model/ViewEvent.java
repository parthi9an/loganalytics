package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientRest;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ViewEvent extends BaseModel {

    public ViewEvent(Map<String, Object> metricValueAttributes, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, metricValueAttributes);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ViewEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(metricValueAttributes);
            this.setProperties(props);
            this.save();
        }
    }

    public ViewEvent() {}

    public OrientVertex find(OrientBaseGraph graph, Map<String, Object> metricValueAttributes) {
        StringBuilder sql = new StringBuilder("select * from ViewEvent where ");

        String query = this.constructQuery(sql, metricValueAttributes);

        OrientVertex viewevent = OrientUtils.getVertex(graph, query);
        return viewevent;
    }

    public String getOpenedViewDetails(Map<String, Object> metricValueAttributes,
            Map<String, Object> attributes) {
        
        StringBuffer query = new StringBuffer();
        query.append("select * from Metric_Event where type = 'view' and in.event = 'open' and in.name = '"
                + metricValueAttributes.get("name")
                + "' and out.session_id = '"+ attributes.get("session_id")
                + "' and out.domain_id = '"+ attributes.get("domain_id")
                + "' and out.source = '"+ attributes.get("source")
                + "' and out.server_id = '"+ attributes.get("server_id")
                + "' order by timestamp desc");
        String result = new OrientRest().doSql(query.toString());
        return result;
    }
}
