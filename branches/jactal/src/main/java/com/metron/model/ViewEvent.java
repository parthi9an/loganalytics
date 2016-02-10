package com.metron.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.metron.orientdb.OrientRest;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ViewEvent extends BaseModel {

    public ViewEvent(Map<String, Object> metricValueAttributes, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, metricValueAttributes);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ViewEvent");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.putAll(metricValueAttributes);
            this.setProperties(props);
            this.save();
        }
    }

    public ViewEvent() {}

    public OrientVertex find(OrientBaseGraph graph, Map<String, Object> metricValueAttributes) {
        StringBuilder sql = new StringBuilder("select * from ViewEvent where ");

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

        OrientVertex viewevent = OrientUtils.getVertex(graph, sql.toString());
        return viewevent;
    }

    public String getOpenedViewDetails(Map<String, Object> metricValueAttributes,
            Map<String, Object> attributes) {
        
        StringBuffer query = new StringBuffer();
        query.append("select * from Metric_Event where type = 'view' and in.view_name = '"
                + metricValueAttributes.get("view_name")
                + "' and out.session_id = '"+ attributes.get("session_id")
                + "' and out.domain_id = '"+ attributes.get("domain_id")
                + "' and out.source = '"+ attributes.get("source")
                + "' and out.server_id = '"+ attributes.get("server_id")
                + "' order by timestamp desc");
        String result = new OrientRest().doSql(query.toString());
        return result;
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
