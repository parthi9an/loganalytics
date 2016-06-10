package com.metron.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.metron.AppConfig;
import com.metron.orientdb.OrientDBGraphManager;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.orientechnologies.orient.server.distributed.ODistributedException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class BaseModel {

    public OrientVertex vertex;

    private OrientBaseGraph baseGraph;

    private int maxRetries = AppConfig.getInstance().getInt("db.maxRetry");

    private HashMap<String, Object> properties = null;

    public BaseModel() {

    }

    public BaseModel(OrientBaseGraph baseGraph) {
        this.baseGraph = baseGraph;
    }

    public void setGraph(OrientBaseGraph graph) {
        this.baseGraph = graph;
    }

    public OrientBaseGraph getGraph() {
        if (this.baseGraph == null) {
            this.baseGraph = OrientDBGraphManager.getInstance().getNonTx();
        }

        return this.baseGraph;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public void save() {

        try {
            vertex.setProperties(properties);
            vertex.save();
            // restore default maxRetries limit after successful
            maxRetries = AppConfig.getInstance().getInt("db.maxRetry");
        } catch (OConcurrentModificationException e) {
            e.printStackTrace();
            if (maxRetries > 0) {
                System.out.println("OConcurrentModificationException: retry " + maxRetries);
                this.vertex = baseGraph.getVertex(vertex.getId());
                maxRetries--;
                save();
            }
        } catch (ODistributedException e) {
            e.printStackTrace();
            if (maxRetries > 0) {
                System.out.println("ODistributedException: retry " + maxRetries);
                this.vertex = baseGraph.getVertex(vertex.getId());
                maxRetries--;
                save();
            }
        } catch(ORecordDuplicatedException e){
            System.out.println("ORecordDuplicatedException: delete vertex");
            vertex.remove();            
            e.printStackTrace();
        }
    }

    public void addEdge(BaseModel toVertex, String label) {
        if (toVertex.vertex == null) {
            return;
        }
        Iterable<Edge> edges = this.vertex.getEdges(toVertex.vertex, Direction.OUT, label);
        // OrientEdge resultEdge = null;
        int size = 0;
        for (Edge edge : edges) {
            size++;
        }
        if (size > 0) {
            return;
        }
        try {
            
         // to avoid version mismatch problem: get the latest
            this.vertex = baseGraph.getVertex(this.vertex.getId());
            toVertex.vertex = baseGraph.getVertex(toVertex.vertex.getId());
            // add edge
            this.vertex.addEdge(label, toVertex.vertex);
            /*baseGraph.command(
                    new OCommandSQL("create edge " + label + " from " + this.vertex.getId()
                            + " to " + toVertex.vertex.getId())).execute();*/
            // restore default maxRetries limit after successful
            maxRetries = AppConfig.getInstance().getInt("db.maxRetry");
        } catch (OConcurrentModificationException e) {
            e.printStackTrace();
            if (maxRetries > 0) {
                System.out.println("OConcurrentModificationException in " + label
                        + " : Edge retry remains " + (maxRetries - 1));
                //to avoid version mismatch problem: get the latest
                this.vertex = baseGraph.getVertex(this.vertex.getId());
                toVertex.vertex = baseGraph.getVertex(toVertex.vertex.getId());
                maxRetries--;
                addEdge(toVertex, label);
            }
        } catch (ODistributedException e) {
            e.printStackTrace();
            if (maxRetries > 0) {
                System.out.println("ODistributedException in " + label
                        + " : Edge retry remains " + (maxRetries - 1));
                //to avoid version mismatch problem: get the latest
                this.vertex = baseGraph.getVertex(this.vertex.getId());
                toVertex.vertex = baseGraph.getVertex(toVertex.vertex.getId());
                maxRetries--;
                addEdge(toVertex, label);
            }
        }

    }
   
    public void addEdge(BaseModel toVertex, String label, Object[] props) {
        if (toVertex.vertex == null) {
            return;
        }
        if (label.equalsIgnoreCase("Session_Domain")){
            Iterable<Edge> edges = this.vertex.getEdges(toVertex.vertex, Direction.OUT, label);
            int size = 0;
            for (Edge edge : edges) {
                size++;
            }
            if (size > 0) {
                return;
            }
        }
        try {
            // to avoid version mismatch problem: get the latest
            this.vertex = baseGraph.getVertex(this.vertex.getId());
            toVertex.vertex = baseGraph.getVertex(toVertex.vertex.getId());
             // add edge
            this.vertex.addEdge(label, toVertex.vertex, props);
            // restore default maxRetries limit after successful
            maxRetries = AppConfig.getInstance().getInt("db.maxRetry");
        } catch (OConcurrentModificationException e) {
            e.printStackTrace();
            if (maxRetries > 0) {
                System.out.println("OConcurrentModificationException in " + label
                        + " : Edge retry remains " + (maxRetries - 1));
                addEdge(toVertex, label, props);
                maxRetries--;
            }
        } catch (ODistributedException e) {
            e.printStackTrace();
            if (maxRetries > 0) {
                System.out.println("ODistributedException in " + label
                        + " : Edge retry remains " + (maxRetries - 1));
                maxRetries--;
                addEdge(toVertex, label, props);
            }
        } 
    }
    
    /*public void addEdge(BaseModel toVertex, String label, String propKey, String propVal) {
        if (toVertex.vertex == null) {
            return;
        }
        try {
            // add edge
            // this.vertex.addEdge(label, toVertex.vertex, props);
            baseGraph.command(
                    new OCommandSQL("create edge " + label + " from " + this.vertex.getId()
                            + " to " + toVertex.vertex.getId() + " set " + propKey + "='" + propVal
                            + "'")).execute();
            // restore default maxRetries limit after successful
            maxRetries = AppConfig.getInstance().getInt("db.maxRetry");
        } catch (OConcurrentModificationException e) {
            e.printStackTrace();
            if (maxRetries > 0) {
                System.out.println("OConcurrentModificationException in " + label
                        + " : Edge retry remains " + (maxRetries - 1));
                // to avoid version mismatch problem: get the latest
                this.vertex = baseGraph.getVertex(this.vertex.getId());
                toVertex.vertex = baseGraph.getVertex(toVertex.vertex.getId());
                maxRetries--;
                addEdge(toVertex, label, propKey, propVal);
            }
        }
    }*/
    
    public String constructQuery(StringBuilder sql,Map<String, Object> map){
        
        for (Iterator<Entry<String, Object>> iter = map.entrySet().iterator(); iter
                .hasNext();) {
            Entry<String, Object> pair = iter.next();
            sql.append(pair.getKey());
            if(pair.getValue() != null){
                sql.append("= '");
                sql.append(pair.getValue());
                sql.append("'");
            }else{
                sql.append(" IS ");
                sql.append(pair.getValue());
            }
            if (iter.hasNext()) {
                sql.append(" and ");
            }
        }
        
        return sql.toString();
    }
}
