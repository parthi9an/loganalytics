package com.metron.model.event;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class RawEvent extends BaseModel {
    
    public RawEvent(String eventId, OrientBaseGraph graph) {
        if(eventId != null){
            this.vertex = find(graph, eventId);
        }
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Event");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String eventId) {
        return OrientUtils.getVertex(graph, "select from Event where eventId = '"
                + eventId + "'");
    }
}
