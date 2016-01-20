package com.metron.model.event;

import org.json.JSONObject;

import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class CisUiEvent extends Event{
    
    public CisUiEvent(JSONObject eventData) {
        super(eventData);
    }

    public void process() {
        /*OrientBaseGraph graph = this.getGraph();
        OrientVertex vertex = graph.addVertex("class:CisEvents");
        this.saveCisEvent(vertex); */     
    }

}
