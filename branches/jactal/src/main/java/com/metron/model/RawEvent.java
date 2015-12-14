package com.metron.model;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class RawEvent extends BaseModel {
    
    public RawEvent(String eventId, Object hostname, OrientBaseGraph graph) {
        super(graph);
        if(eventId != null){
            this.vertex = find(graph, eventId, hostname);
        }
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Event");
        }
    }
    public OrientVertex find(OrientBaseGraph graph, String eventId, Object hostname) {
        return OrientUtils.getVertex(graph, "select from Event where eventId = '"
                + eventId + "' and OUT('Event_Host')[0].hostname='"+ hostname.toString()+"'");
    }
}
