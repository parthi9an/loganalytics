package com.metron.orientdb;

import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class DatabaseServiceCie {

    /*
     * create all the necessary classes
     * and do all necessary changes in the database
     */
    public static void setUp() {

        OrientDBGraphManager.getInstance().createDB();
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        // By Default the Date Format is yyyy-MM-dd HH:mm:ss we have changed to yyyy-MM-dd HH:mm:ss.SSS
        graph.command(new OCommandSQL("ALTER DATABASE DATETIMEFORMAT yyyy-MM-dd HH:mm:ss.SSS")).execute();
        // Allow strictSql
        graph.command(new OCommandSQL("ALTER DATABASE CUSTOM strictSql=false")).execute();
        OSchemaProxy schema = graph.getRawGraph().getMetadata().getSchema();
        
        OrientVertexType vType = null;
        OrientEdgeType eType = null;
        try {
            if (!schema.existsClass("CisEvents")) {
                vType = graph.createVertexType("CisEvents");
                vType.createProperty("source", OType.STRING);
                vType.createProperty("server_id", OType.STRING);
                vType.createProperty("user_id", OType.STRING);
                vType.createProperty("session_id", OType.STRING);
                vType.createProperty("version", OType.STRING);
                vType.createIndex("CisEvents.source", "NOTUNIQUE", "source");
                vType.createIndex("CisEvents.server_id", "NOTUNIQUE", "server_id");
                vType.createIndex("CisEvents.user_id", "NOTUNIQUE", "user_id");
                vType.createIndex("CisEvents.session_id", "NOTUNIQUE", "session_id");
                
            }
            
            if (!schema.existsClass("ActionEvent")) {
                vType = graph.createVertexType("ActionEvent");
                vType.createProperty("key", OType.STRING);
                vType.createProperty("command", OType.STRING);
                vType.createIndex("ActionEvent.key", "NOTUNIQUE", "key");
                vType.createIndex("ActionEvent.command", "NOTUNIQUE", "command");
            }
            
            if (!schema.existsClass("KeyBoardEvent")) {
                vType = graph.createVertexType("KeyBoardEvent");
                vType.createProperty("command", OType.STRING);
                vType.createIndex("KeyBoardEvent.command", "NOTUNIQUE", "command");
            }
            
            if (!schema.existsClass("ViewEvent")) {
                vType = graph.createVertexType("ViewEvent");
                vType.createProperty("name", OType.STRING);
                vType.createProperty("event", OType.STRING);
                vType.createIndex("ViewEvent.name", "NOTUNIQUE", "name");
                vType.createIndex("ViewEvent.event", "FULLTEXT", "event");
            }
            
            if (!schema.existsClass("DomainEvent")) {
                vType = graph.createVertexType("DomainEvent");
                vType.createProperty("domain_type", OType.STRING);
                vType.createIndex("DomainEvent.type", "NOTUNIQUE", "domain_type");
            }
            
            if (!schema.existsClass("FieldEvent")) {
                vType = graph.createVertexType("FieldEvent");
                vType.createProperty("field", OType.STRING);
                vType.createIndex("FieldEvent.field", "NOTUNIQUE", "field");
            }
            
            if (!schema.existsClass("ErrorEvent")) {
                vType = graph.createVertexType("ErrorEvent");
                vType.createProperty("err_type", OType.STRING);
                vType.createProperty("message", OType.STRING);
                vType.createProperty("trace", OType.STRING);
                vType.createIndex("ErrorEvent.err_type", "NOTUNIQUE", "err_type");
            }
            
            if (!schema.existsClass("ConfigurationEvent")) {
                vType = graph.createVertexType("ConfigurationEvent");
                vType.createProperty("name", OType.STRING);
                vType.createIndex("ConfigurationEvent.name", "NOTUNIQUE", "name");
            }
            
            if (!schema.existsClass("WindowEvent")) {
                vType = graph.createVertexType("WindowEvent");
                vType.createProperty("length", OType.INTEGER);
                vType.createProperty("height", OType.INTEGER);
                vType.createIndex("WindowEvent.length", "NOTUNIQUE", "length");
            }
            
            if (!schema.existsClass("WindowScrollEvent")) {
                vType = graph.createVertexType("WindowScrollEvent");
                vType.createProperty("orientation", OType.STRING);
                vType.createProperty("direction", OType.STRING);
                vType.createIndex("WindowScrollEvent.orientation", "NOTUNIQUE", "orientation");
                vType.createIndex("WindowScrollEvent.direction", "NOTUNIQUE", "direction");
            }
            
            if (!schema.existsClass("ViewContext")) {
                vType = graph.createVertexType("ViewContext");
                vType.createProperty("view", OType.STRING);
                vType.createProperty("element", OType.STRING);
                vType.createIndex("ViewContext.view", "NOTUNIQUE", "view");
            }
            
            if (!schema.existsClass("ContextType")) {
                vType = graph.createVertexType("ContextType");
                vType.createProperty("type", OType.STRING);
                vType.createIndex("ContextType.type", "NOTUNIQUE", "type");
            }
            
            if (!schema.existsClass("EnvironmentEvent")) {
                vType = graph.createVertexType("EnvironmentEvent");
                vType.createProperty("os", OType.STRING);
                vType.createProperty("screen_x", OType.INTEGER);
                vType.createProperty("screen_y", OType.INTEGER);
                vType.createProperty("app_x", OType.INTEGER);
                vType.createProperty("app_y", OType.INTEGER);
                vType.createProperty("browser_type", OType.STRING);
                vType.createProperty("browser_version", OType.STRING);
                vType.createProperty("cpu_type", OType.STRING);
                vType.createProperty("cpu_clock", OType.DOUBLE);
                vType.createProperty("cpu_cores", OType.INTEGER);
                vType.createProperty("mem", OType.INTEGER);
                vType.createIndex("EnvironmentEvent.os", "NOTUNIQUE", "os");
            }
            
            if (!schema.existsClass("Pattern")) {
                vType = graph.createVertexType("Pattern");
                vType.createProperty("pattern_type", OType.STRING);
                vType.createProperty("association_count", OType.INTEGER);
            }
            
            if (!schema.existsClass("ErrorPattern")) {
                vType = graph.createVertexType("ErrorPattern");
                vType.createProperty("pattern_type", OType.STRING);
                vType.createProperty("association_count", OType.INTEGER);
                vType.createProperty("error_trace_checksum", OType.STRING);
                vType.createIndex("ErrorPattern.errortracechecksum", "FULLTEXT", "error_trace_checksum");
            }
            
            if (!schema.existsClass("FilterCriteria")) {
                vType = graph.createVertexType("FilterCriteria");
                vType.createProperty("uName", OType.STRING);
                vType.createProperty("userId", OType.STRING);
                vType.createProperty("sessionId", OType.STRING);
                vType.createProperty("source", OType.STRING);
                vType.createProperty("version", OType.STRING);
                vType.createProperty("serverId", OType.STRING);
                vType.createProperty("filtername", OType.STRING);
                vType.createProperty("fromDate", OType.STRING);
                vType.createProperty("toDate", OType.STRING);
                vType.createProperty("timestamp", OType.STRING);
                vType.createIndex("FilterCriteria.uName", "NOTUNIQUE", "uName");
                vType.createIndex("FilterCriteria.filtername", "NOTUNIQUE", "filtername");
                vType.createIndex("FilterCriteria.timestamp", "NOTUNIQUE", "timestamp");
            }
            
            if (!schema.existsClass("Metric_Event")) {
                eType = graph.createEdgeType("Metric_Event");
                eType.createProperty("type", OType.STRING);
                eType.createProperty("timestamp", OType.STRING);
                // Use FullText index in query through CONTAINSTEXT operator
                eType.createIndex("MetricEvent.type", "FULLTEXT", "type");
                eType.createIndex("MetricEvent.timestamp", "NOTUNIQUE", "timestamp");
            }
            
            if (!schema.existsClass("Session_Domain")) {
                eType = graph.createEdgeType("Session_Domain");
                eType.createProperty("timestamp", OType.STRING);
                eType.createIndex("SessionDomain.timestamp", "NOTUNIQUE", "timestamp");
            }
            
            if (!schema.existsClass("Session_Pattern")) {
                eType = graph.createEdgeType("Session_Pattern");
                eType.createProperty("timestamp", OType.STRING);
                eType.createIndex("SessionPattern.timestamp", "NOTUNIQUE", "timestamp");
            }
            
            if (!schema.existsClass("Session_ErrorPattern")) {
                eType = graph.createEdgeType("Session_ErrorPattern");
                eType.createProperty("timestamp", OType.STRING);
                eType.createIndex("SessionErrorPattern.timestamp", "NOTUNIQUE", "timestamp");
            }
            
            if (!schema.existsClass("AccessToken")) {
                vType = graph.createVertexType("AccessToken");
                vType.createProperty("user_name", OType.STRING);
                vType.createProperty("login_time", OType.STRING);
                vType.createProperty("access_token", OType.STRING);
                vType.createIndex("AccessToken.userName", "NOTUNIQUE", "user_name");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        graph.commit();
        graph.shutdown();

    }
    public static void main(String[] args) {
        setUp();   
    }
}
