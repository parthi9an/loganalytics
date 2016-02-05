package com.metron.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
        this.vertex = find(graph, metricValueAttributes);
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

        for (Iterator<Entry<String, Object>> iter = metricValueAttributes.entrySet().iterator(); iter
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

        OrientVertex actionevent = OrientUtils.getVertex(graph, sql.toString());
        return actionevent;
    }

    public OrientVertex find(OrientBaseGraph graph, String key, String command, String view) {
        OrientVertex actionevent = OrientUtils.getVertex(graph,
                "select *  from ActionEvent where action_key = '" + key + "' and action_command='"
                        + command + "' and action_view='" + view + "'");
        return actionevent;
    }

}
