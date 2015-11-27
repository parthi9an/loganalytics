package com.metron.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.metron.common.TypeFactory;
import com.metron.orientdb.OrientDBGraphManager;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class Schema {

    private static Schema _instance;
    OrientBaseGraph graph;
    OSchemaProxy schema;

    private Map<String, List<EdgeDef>> inEdgeDefs;
    private Map<String, List<EdgeDef>> outEdgeDefs;

    public static Schema getInstance() {
        if (_instance == null) {
            _instance = new Schema();
            _instance.init();
        }
        return _instance;
    }

    public OrientVertexType getVertexDef(String name) {
        return this.graph.getVertexType(name);
    }

    public List<EdgeDef> getOutEdges(String vertexName) {
        List<EdgeDef> edges = this.outEdgeDefs.get(vertexName);
        if (edges == null) {
            edges = new ArrayList<EdgeDef>();

        }
        this.outEdgeDefs.put(vertexName, edges);
        return edges;
    }

    public List<EdgeDef> getInEdges(String vertexName) {
        List<EdgeDef> edges = this.inEdgeDefs.get(vertexName);
        if (edges == null) {
            edges = new ArrayList<EdgeDef>();

        }
        this.inEdgeDefs.put(vertexName, edges);
        return edges;
    }

    private void associateEdge(String fromVertex, String toVertex, String edgeName, String fromKey, String toKey) {

        EdgeDef edge = new EdgeDef(edgeName, fromVertex, toVertex, fromKey, toKey);

        List<EdgeDef> edges = this.outEdgeDefs.get(fromVertex);
        if (edges == null) {
            edges = new ArrayList<EdgeDef>();
        }

        edges.add(edge);
        this.outEdgeDefs.put(fromVertex, edges);

        List<EdgeDef> inEdges = this.inEdgeDefs.get(toVertex);
        if (inEdges == null) {
            inEdges = new ArrayList<EdgeDef>();
        }
        inEdges.add(edge);
        this.inEdgeDefs.put(toVertex, inEdges);

        System.out.println("Edge assocaition completed for " + edgeName);
    }

    private void init() {

        this.graph = OrientDBGraphManager.getInstance().getNonTx();
        this.schema = graph.getRawGraph().getMetadata().getSchema();

        inEdgeDefs = new HashMap<String, List<EdgeDef>>();
        outEdgeDefs = new HashMap<String, List<EdgeDef>>();

        this.createSchema();
        this.loadEdgeDefs();

        // this.loadEdgeDefs();

    }

    private void loadEdgeDefs() {

        OrientEdgeType edgeDef = null;

        if (!schema.existsClass("PatientAdmissionEdge")) {
            edgeDef = graph.createEdgeType("PatientAdmissionEdge");
        }

        if (!schema.existsClass("PatientDiagnosisEdge")) {
            edgeDef = graph.createEdgeType("PatientDiagnosisEdge");
        }

        if (!schema.existsClass("AdmissionDiagnosisEdge")) {
            edgeDef = graph.createEdgeType("AdmissionDiagnosisEdge");

        }

        if (!schema.existsClass("AdmissionLabDataEdge")) {
            edgeDef = graph.createEdgeType("AdmissionLabDataEdge");

        }

        this.associateEdge("Patient", "Admission", "PatientAdmissionEdge", "patientId", "patientId");
        this.associateEdge("Patient", "Diagnosis", "PatientDiagnosisEdge", "patientId", "patientId");
        this.associateEdge("Admission", "Diagnosis", "AdmissionDiagnosisEdge", "admissionId", "admissionId");
        this.associateEdge("Admission", "LabData", "AdmissionLabDataEdge", "admissionId", "admissionId");

    }

    /**
     * Create Vertex
     */
    public void createSchema() {

        // String path = ApplicationContext.getInstance().getServletContext().getRealPath("/WEB-INF/classes/conf/vertices");

        // String path = "C:/projects/cisco/loy_jactal/jactal/src/main/resources/conf/vertices";
        String path = "D:/eworkspace/jactal/src/main/resources/conf/vertices";

        File[] files = new File(path).listFiles();

        for (File file : files) {

            try {
                String className = file.getName().replace(".conf", "");

                OrientVertexType vertexDef = null;

                BufferedReader br = new BufferedReader(new FileReader(path + "/" + className + ".conf"));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject clazz = new JSONObject(sb.toString());

                className = clazz.get("name").toString();

                if (!schema.existsClass(className)) {

                    vertexDef = graph.createVertexType(className);

                    /**
                     * Properties
                     */
                    JSONArray props = (JSONArray) clazz.get("properties");
                    int psize = props.length();
                    for (int j = 0; j < psize; j++) {
                        JSONObject prop = (JSONObject) props.get(j);
                        vertexDef.createProperty(prop.get("name").toString(),
                                TypeFactory.getOTypeByName(prop.get("type").toString()));
                    }

                    /**
                     * Index
                     */
                    JSONObject index = (JSONObject) clazz.get("index");

                    JSONArray indexFields = (JSONArray) index.get("fields");
                    String[] indexFieldString = new String[indexFields.length()];
                    for (int j = 0; j < indexFields.length(); j++) {
                        indexFieldString[j] = indexFields.get(j).toString();
                    }
                    String indexName, indexType;
                    indexName = index.get("name").toString();
                    indexType = index.get("type").toString();

                    vertexDef.createIndex(indexName, TypeFactory.getIndexTypeByName(indexType), indexFieldString);

                    System.out.println("Vertex Created " + className);

                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        } // For loop end
    }

}
