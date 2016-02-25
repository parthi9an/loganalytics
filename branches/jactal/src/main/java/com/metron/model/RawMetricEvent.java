package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.orientdb.OrientRest;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class RawMetricEvent extends BaseModel {
    
    public RawMetricEvent(Map<String, Object> rawAttributes, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, rawAttributes);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:CisEvents");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("user_id",rawAttributes.get("user_id"));
            props.put("session_id",rawAttributes.get("session_id"));
            props.put("source",rawAttributes.get("source"));
            props.put("server_id",rawAttributes.get("server_id"));
            this.setProperties(props);
            this.save();
        }
    }

    public RawMetricEvent() {}

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> rawAttributes) {
        
        return OrientUtils.getVertex(graph, "select from CisEvents where session_id = '" + rawAttributes.get("session_id")
                +"' and user_id ='"+ rawAttributes.get("user_id") + "' and source ='"+ rawAttributes.get("source")+
                "' and server_id ='"+ rawAttributes.get("server_id") + "'");
    }

    public String getPreviousMetricEvent(Map<String, Object> rawAttributes) {
        StringBuffer query = new StringBuffer();
        query.append("select outE('Metric_Event') as edge from CisEvents where session_id = '" + rawAttributes.get("session_id")
                +"' and user_id ='"+ rawAttributes.get("user_id") + "' and source ='"+ rawAttributes.get("source")+
                "' and server_id ='"+ rawAttributes.get("server_id") + "'");
        String result = new OrientRest().doSql(query.toString());
        return result;
    }

}
