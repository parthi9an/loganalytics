package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ViewEvent extends BaseModel {

    public ViewEvent(String name, String event_type, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, name, event_type);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ViewEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("view_name", name);
            props.put("view_event_type", event_type);
            this.setProperties(props);
            this.save();
        }
    }

    public OrientVertex find(OrientBaseGraph graph, String name, String event_type) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from ViewEvent where view_name = '" + name + "' and view_event_type='"
                        + event_type + "'");
        return actionevent;
    }

    /*public OrientVertex find(OrientBaseGraph graph, String name, String SessionId) {
        
         * OrientVertex actionevent = OrientUtils.getVertex(graph,
         * "select inV() from Metric_Event where metric_type='type_view' and in.view_name = '"
         * + name + "'and out.metric_session_id = '" + SessionId + "'");
         
        OrientVertex actionevent = OrientUtils
                .getVertex(
                        graph,
                        "select from (select expand(out('Metric_Event')[@class='ViewEvent']) from CisEvents where metric_session_id = '"
                                + SessionId + "') where view_name = '" + name + "'and view_event_type = 'view_open'" );
        return actionevent;
    }*/

}
