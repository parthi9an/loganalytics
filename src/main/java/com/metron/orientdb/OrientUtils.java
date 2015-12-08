package com.metron.orientdb;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author satheesh
 */

public class OrientUtils {

    public static OrientVertex getVertex(OrientBaseGraph graph, String query) {
        for (Vertex v : (Iterable<Vertex>) graph.command(new OCommandSQL(query)).execute()) {
            return (OrientVertex) v;
        }
        return null;
    }

    public static Iterable<Vertex> getVertices(OrientBaseGraph graph, String query) {
        return graph.command(new OCommandSQL(query)).execute();
    }

    public static int getCount(OrientBaseGraph graph, String tableName, String selection,
            String[] selectionArgs) {
        int count = 0;
        return count;
    }

    public static String convertDatetoorientDbDate(Date date) {
        // alter database DATETIMEFORMAT yyyy-MM-dd HH:mm:ss:SSS
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss.SSS");
        return sdf.format(date);
    }

}
