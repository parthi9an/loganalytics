package com.metron.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.metron.orientdb.OrientDBGraphManager;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class Odb {

    private static Odb _instance;

    public static Odb getInstance() {
        if (_instance == null) {
            _instance = new Odb();
            _instance.init();
        }
        return _instance;
    }

    private void init() {
    }

    public void loadData(JSONObject event) {


        String entityType;
        try {
            entityType = event.getString("entityType");
            // use the entityType and the logical id together to load and
            // update, else insert
            // (note that if it doesnt have a keyField, then it is an isert.

            OrientVertexType v = Schema.getInstance().getVertexDef(entityType);
            OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
            event.remove("entityType");

            Map<String, Object> eventMap = new HashMap<String, Object>();
            Iterator<String> it = event.keys();
            while (it.hasNext()) {
                String key = it.next();
                eventMap.put(key, event.get(key));
            }

            // if the vertex already exists, update it - else create it
            OIndex<?> index = v.getClassIndex("INDX_UNIQUE" + entityType);
            Object[] fieldNames = index.getDefinition().getFields().toArray();
            String[] fieldValues = new String[fieldNames.length];
            for (int i = 0; i < fieldNames.length; i++) {
				fieldValues[i] = eventMap.get(fieldNames[i]).toString();
			}

            OrientVertex vertex = this.getVertex(entityType, fieldNames, fieldValues, false);
            if (vertex == null) {
                vertex = graph.addVertex("class:" + entityType, eventMap);
                System.out.println("Created Vertex:"+entityType);
            } else {
                vertex.setProperties(eventMap);
                System.out.println("Updated Vertex:"+entityType);

            }

            // add any relevant edges
            List<EdgeDef> inEdges = Schema.getInstance().getInEdges(entityType);
            for (EdgeDef edge : inEdges) {
                String edgeKey = edge.toKey;
                String value = eventMap.get(edgeKey).toString();
                OrientVertex fromVertex = this.getVertex(edge.in, new String[]{edge.fromKey}, new String[]{value}, true);
                Iterable edges = fromVertex.getEdges(Direction.BOTH, edge.name);
                if (!edges.iterator().hasNext()) {
                	fromVertex.addEdge(edge.name, vertex);
                    System.out.println("Added Edge From:"+edge.in + " To:"+entityType);
                }
            }

            List<EdgeDef> outEdges = Schema.getInstance().getOutEdges(entityType);
            for (EdgeDef edge : outEdges) {
                String edgeKey = edge.fromKey;
                String value = eventMap.get(edgeKey).toString();
                OrientVertex toVertex = this.getVertex(edge.out, new String[]{edge.toKey}, new String[]{value}, true);
                vertex.addEdge(edge.name, toVertex);
                System.out.println("Added Edge From:"+entityType + " To:"+edge.in);
            }



        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private OrientVertex getVertex(String name, Object[] fromKey, Object[] value, boolean createIfNotExists) {
        StringBuffer whereClause = new StringBuffer();
        for (int i = 0; i < fromKey.length; i++) {
        	whereClause.append((i == 0)?" ": " and ").append(fromKey[i].toString()).append("='").append(value[i].toString()).append("'");
        }
        
    	Iterable<Vertex> vertices = Schema.getInstance().graph.command(
                new OCommandSQL("select * from " + name + " where " + whereClause.toString())).execute();
        Iterator<Vertex> iter = vertices.iterator();
        if (!iter.hasNext() && createIfNotExists) {
            HashMap m = new HashMap<String, Object>();
            for (int i = 0; i < fromKey.length; i++) {
				m.put(fromKey[i], value[i]);
			}
            return Schema.getInstance().graph.addVertex("class:" + name, m);
        } else {
            while (iter.hasNext()) {
                Vertex v = iter.next();
                if (iter.hasNext()) {
                    // error
                    return null;
                }
                return (OrientVertex) v;
            }
        }

        return null;
    }

}
