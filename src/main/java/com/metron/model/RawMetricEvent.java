package com.metron.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.metron.orientdb.OrientRest;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class RawMetricEvent extends BaseModel {
    
    public RawMetricEvent(Map<String, Object> rawAttributes, OrientBaseGraph graph) {
        super(graph);
        List<String> keys = CisEventMappings.getInstance().getEventMapping(this.getClass().getSimpleName());
        String key = null;
        Map<String, Object> rawattri = new HashMap<String, Object>();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            key = itr.next();
            rawattri.put(key, (rawAttributes.get(key) != null ? rawAttributes.get(key) : null));
        }
        this.vertex = find(graph, rawattri);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:CisEvents");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("user_id",rawAttributes.get("user_id"));
            props.put("session_id",rawAttributes.get("session_id"));
            props.put("source",rawAttributes.get("source"));
            props.put("server_id",rawAttributes.get("server_id"));
            if(rawAttributes.containsKey("version"))
                props.put("version",rawAttributes.get("version"));
            this.setProperties(props);
            this.save();
        }
    }

    public RawMetricEvent() {}

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> rawAttributes) {
        
        /*return OrientUtils.getVertex(graph, "select from CisEvents where session_id = '" + rawAttributes.get("session_id")
                +"' and user_id ='"+ rawAttributes.get("user_id") + "' and source ='"+ rawAttributes.get("source")+
                "' and server_id ='"+ rawAttributes.get("server_id") + "' and version ='"+ rawAttributes.get("version") + "'");*/
        StringBuilder sql = new StringBuilder("select * from CisEvents where ");

        String query = this.constructQuery(sql, rawAttributes);

        OrientVertex cisRawEvent = OrientUtils.getVertex(graph, query);
        
        return cisRawEvent;
    }

    public String getPreviousMetricEvent(Map<String, Object> rawAttributes) {
        
        List<String> keys = CisEventMappings.getInstance().getEventMapping(this.getClass().getSimpleName());
        String key = null;
        Map<String, Object> rawattri = new HashMap<String, Object>();
        Iterator<String> itr = keys.iterator();
        while(itr.hasNext()){
            key = itr.next();
            rawattri.put(key, (rawAttributes.get(key) != null ? rawAttributes.get(key) : null));
        }
        StringBuilder sql = new StringBuilder("select outE('Metric_Event') as edge from CisEvents where ");
        /*query.append("select outE('Metric_Event') as edge from CisEvents where session_id = '" + rawAttributes.get("session_id")
                +"' and user_id ='"+ rawAttributes.get("user_id") + "' and source ='"+ rawAttributes.get("source")+
                "' and server_id ='"+ rawAttributes.get("server_id") + "' and version ='"+ rawAttributes.get("version") + "'");
        String result = new OrientRest().doSql(query.toString());*/
        String query = this.constructQuery(sql, rawattri);

        String result = new OrientRest().doSql(query.toString());
        return result;
    }

}
