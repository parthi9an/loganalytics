package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class RawMetricEvent extends BaseModel {
    
    public RawMetricEvent(String metric_session_id,
            OrientBaseGraph graph) {
        super(graph);
        if (metric_session_id != null) {
            this.vertex = find(graph, metric_session_id);
        }
        if (vertex == null) {
            this.vertex = graph.addVertex("class:CisEvents");
        }
    }
    
    public RawMetricEvent(Map<String, Object> rawAttributes, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, rawAttributes);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:CisEvents");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("domain_id",rawAttributes.get("domain_id"));
            props.put("session_id",rawAttributes.get("session_id"));
            props.put("source",rawAttributes.get("source"));
            props.put("server_id",rawAttributes.get("server_id"));
            this.setProperties(props);
            this.save();
        }
    }

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> rawAttributes) {
        /*StringBuilder sql = new StringBuilder("select * from CisEvents where ");

        for (Iterator<Entry<String, Object>> iter = rawAttributes.entrySet().iterator(); iter
                .hasNext();) {
            Entry<String, Object> pair = iter.next();
            sql.append(pair.getKey());
            sql.append("= '");
            sql.append(pair.getValue());
            sql.append("'");

            if (iter.hasNext()) {
                sql.append(" and ");
            }
        }

        OrientVertex rawevent = OrientUtils.getVertex(graph, sql.toString());
        return rawevent;*/
        return OrientUtils.getVertex(graph, "select from CisEvents where session_id = '" + rawAttributes.get("session_id")
                +"' and domain_id ='"+ rawAttributes.get("domain_id") + "' and source ='"+ rawAttributes.get("source")+
                "' and server_id ='"+ rawAttributes.get("server_id") + "'");
    }

    public OrientVertex find(OrientBaseGraph graph, String metric_session_id) {
        return OrientUtils.getVertex(graph, "select from CisEvents where metric_session_id = '" + metric_session_id
                + "'");
    }

}
