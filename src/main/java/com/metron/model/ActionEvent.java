package com.metron.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ActionEvent extends BaseModel {

    public ActionEvent(String key, String command, String view, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, key, command, view);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ActionEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("action_key", key);
            props.put("action_command", command);
            props.put("action_view", view);
            this.setProperties(props);
            this.save();
        }
    }

    public ActionEvent(Map<String, Object> metricValueAttributes, OrientBaseGraph graph) {
        super(graph);
        List<String> keys = CisEventMappings.getInstance().getEventMapping(this.getClass().getSimpleName());
        String key = null;
        Map<String, Object> mva = new HashMap<String, Object>();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            key = itr.next();
            mva.put(key, (metricValueAttributes.get(key) != null ? metricValueAttributes.get(key) : null));
        }
        this.vertex = find(graph, mva);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ActionEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(metricValueAttributes);
            this.setProperties(props);
            this.save();
        }
    }

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> metricValueAttributes) {
        StringBuilder sql = new StringBuilder("select * from ActionEvent where ");

        String query = this.constructQuery(sql, metricValueAttributes);

        OrientVertex actionevent = OrientUtils.getVertex(graph, query);
        return actionevent;
    }

    public OrientVertex find(OrientBaseGraph graph, String key, String command, String view) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from ActionEvent where action_key = '" + key + "' and action_command='"
                        + command + "' and action_view='" + view + "'");
        return actionevent;
    }

}
