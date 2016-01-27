package com.metron.model.event;

import org.json.JSONObject;

public class CisUiEvent extends CisEvent{
    
    public CisUiEvent(JSONObject eventData) {
        super(eventData);
    }

    public void process() {
        /*OrientBaseGraph graph = this.getGraph();
        OrientVertex vertex = graph.addVertex("class:CisEvents");
        this.saveCisEvent(vertex); */     
    }

}
