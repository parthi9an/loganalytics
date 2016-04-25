package com.metron.model;

import java.util.HashMap;
import java.util.Map;

import com.metron.controller.QueryWhereBuffer;
import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class FilterCritera extends BaseModel {

    public FilterCritera(HashMap<String, Object> filterProps, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, filterProps);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:FilterCriteria");
            this.setProperties(filterProps);
            this.save();
        }
    }

    public FilterCritera(OrientBaseGraph graph) {
        super(graph);
    }

    public OrientVertex filterExists(Object filtername, Object uName) {

        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        whereClause.append("uName = '" + uName + "'");
        whereClause.append("filtername = '" + filtername + "'");
        query.append("select * from FilterCriteria order by timestamp desc"
                + ((!whereClause.toString().equals("")) ? " Where " + whereClause.toString() : ""));
        OrientVertex filter = OrientUtils.getVertex(this.getGraph(), query.toString());
        return filter;
    }

    private OrientVertex find(OrientBaseGraph graph, Map<String, Object> filterProps) {
        StringBuilder sql = new StringBuilder("select * from FilterCriteria where ");
        String query = this.constructQuery(sql, filterProps);
        OrientVertex filter = OrientUtils.getVertex(graph, query);
        return filter;
    }
}
