package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class RawMetricEvent extends BaseModel {
    
    /*public RawMetricEvent(String metric_type, String metric_timestamp, String metric_session_id,
            OrientBaseGraph graph) {
        super(graph);
        if (metric_type != null && metric_timestamp != null && metric_session_id != null) {
            this.vertex = find(graph, metric_type,metric_timestamp,metric_session_id);
        }
        if (vertex == null) {
            this.vertex = graph.addVertex("class:CisEvents");
        }
    }*/
    
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
    
    /*public OrientVertex find(OrientBaseGraph graph, String metric_type, String metric_timestamp, String metric_session_id) {
        return OrientUtils.getVertex(graph, "select from CisEvents where metric_type = '" + metric_type
                + "' and metric_timestamp ='" + metric_timestamp + "' and metric_session_id='" + metric_session_id + "'");
        return OrientUtils.getVertex(graph, "select from CisEvents where metric_timestamp = '" + metric_timestamp
                + "' and metric_session_id='" + metric_session_id + "'");
    }*/
    
    public OrientVertex find(OrientBaseGraph graph, String metric_session_id) {
        return OrientUtils.getVertex(graph, "select from CisEvents where metric_session_id = '" + metric_session_id
                + "'");
    }

}
