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

//    public static OrientEdge addEdge(OrientVertex fromVertex, OrientVertex toVertex, String label, int maxRetries) {
//        // This is blasting fast
//        // OrientVertex v1 = graphdb.getVertex(new ORecordId("#10:0"));
//        // OrientVertex v2 = graphdb.getVertex(new ORecordId("#11:0"));
//        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
//        Iterable<Edge> edges = fromVertex.getEdges(toVertex, Direction.OUT, label);
//        OrientEdge resultEdge = null;
//        int size = 0;
//        for (Edge edge : edges) {
//            size++;
//        }
//        if (size > 0) {
//            return (OrientEdge) edges.iterator().next();
//
//        }
//        try {
//            resultEdge = (OrientEdge) fromVertex.addEdge(label, toVertex);
//        } catch(OConcurrentModificationException e){
//            e.printStackTrace();
//            if (maxRetries > 1) {
//                System.out.println("OConcurrentModificationException: Edge retry remains " + (maxRetries - 1));
//                addEdge(graph.getVertex(fromVertex.getId()), graph.getVertex(toVertex.getId()), label, (maxRetries - 1));
//            }    
//        }
//        return resultEdge;
//
//    }
//
//    public static OrientEdge addEdge(OrientVertex fromVertex, OrientVertex toVertex, String label,
//            Object[] props, int maxRetries) {
//       // return (OrientEdge) fromVertex.addEdge(label, toVertex, props);
//        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
//        OrientEdge resultEdge = null;
//        try {
//            resultEdge = (OrientEdge) fromVertex.addEdge(label, toVertex, props);
//        } catch(OConcurrentModificationException e){
//            e.printStackTrace();
//            if (maxRetries > 1) {
//                System.out.println("OConcurrentModificationException: Edge retry remains " + (maxRetries - 1));
//                addEdge(graph.getVertex(fromVertex.getId()), graph.getVertex(toVertex.getId()), label, props, (maxRetries - 1));
//            }    
//        }
//        return resultEdge;
//    }

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
