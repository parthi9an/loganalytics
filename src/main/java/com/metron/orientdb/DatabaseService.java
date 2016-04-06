package com.metron.orientdb;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

/**
 * @author satheesh
 */

public class DatabaseService {

    /*
     * 
     * create all the neccessary classes
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
            
            if (!schema.existsClass("BaseEvent")) {
                vType = graph.createVertexType("BaseEvent");
                vType.createProperty("raw", OType.STRING);
            }

            if (!schema.existsClass("User")) {
                vType = graph.createVertexType("User");
                vType.createProperty("name", OType.STRING);
                vType.createIndex("User.name", "UNIQUE", "name");
            }

            if (!schema.existsClass("TimeWindow1")) {
                vType = graph.createVertexType("TimeWindow1");
                vType.createProperty("startTime", OType.LONG);
                vType.createProperty("endTime", OType.LONG);
            }

            if (!schema.existsClass("TimeWindow5")) {
                vType = graph.createVertexType("TimeWindow5");
                vType.createProperty("startTime", OType.LONG);
                vType.createProperty("endTime", OType.LONG);
            }

            if (!schema.existsClass("TimeWindow60")) {
                vType = graph.createVertexType("TimeWindow60");
                vType.createProperty("startTime", OType.LONG);
                vType.createProperty("endTime", OType.LONG);
            }
            if (!schema.existsClass("TimeWindow1440")) {
                vType = graph.createVertexType("TimeWindow1440");
                vType.createProperty("startTime", OType.LONG);
                vType.createProperty("endTime", OType.LONG);
            }

            if (!schema.existsClass("Domain")) {
                vType = graph.createVertexType("Domain");
                vType.createProperty("name", OType.STRING);
                vType.createIndex("Domain.name", "UNIQUE", "name");
            }

            if (!schema.existsClass("Host")) {
                vType = graph.createVertexType("Host");
                vType.createProperty("hostname", OType.STRING);
                vType.createProperty("OS", OType.STRING);
                vType.createProperty("numOfProcessors", OType.LONG);
                vType.createProperty("totalMemory", OType.STRING);
                vType.createIndex("Host.hostname", "UNIQUE", "hostname");

            }

            if (!schema.existsClass("HostStatus")) {
                vType = graph.createVertexType("HostStatus","BaseEvent");
                vType.createProperty("hostname", OType.STRING);
                vType.createProperty("timestamp", OType.DATETIME);
                vType.createProperty("totalMemoryUsedPer", OType.INTEGER);
                vType.createProperty("userCacheAccessPer", OType.INTEGER);
                vType.createProperty("userCacheCapacityPer", OType.INTEGER);
                vType.createProperty("repositoryCacheAccessPer", OType.INTEGER);
                vType.createProperty("repositoryCacheCapacityPer", OType.INTEGER);
                vType.createProperty("privilegeCacheAccessPer", OType.INTEGER);
                vType.createProperty("privilegeCacheCapacityPer", OType.INTEGER);
                vType.createProperty("totalSessions", OType.LONG);
                vType.createProperty("totalServerRequests", OType.LONG);
                vType.createProperty("totalDataSourceRequests", OType.LONG);

            }

            if (!schema.existsClass("HostStatus_Host")) {
                eType = graph.createEdgeType("HostStatus_Host");
            }

            if (!schema.existsClass("HostStatus_TimeWindow1")) {
                eType = graph.createEdgeType("HostStatus_TimeWindow1");
            }

            if (!schema.existsClass("HostStatus_TimeWindow5")) {
                eType = graph.createEdgeType("HostStatus_TimeWindow5");
            }

            if (!schema.existsClass("HostStatus_TimeWindow60")) {
                eType = graph.createEdgeType("HostStatus_TimeWindow60");
            }

            if (!schema.existsClass("HostStatus_TimeWindow1440")) {
                eType = graph.createEdgeType("HostStatus_TimeWindow1440");
            }
            
            if (!schema.existsClass("HostStatus_Event")) {
                eType = graph.createEdgeType("HostStatus_Event");                
            }
            
            if (!schema.existsClass("Event")) {
                vType = graph.createVertexType("Event");
                vType.createProperty("timestamp", OType.DATETIME);
                vType.createProperty("severity", OType.STRING);
                vType.createProperty("rawData", OType.STRING);
                vType.createProperty("eventId", OType.STRING);
                vType.createIndex("Event.eventId", "NOTUNIQUE", "eventId");
                vType.createIndex("Event.timestamp", "NOTUNIQUE", "timestamp");
            }
            
            if (!schema.existsClass("Event_Host")) {
                eType = graph.createEdgeType("Event_Host");
            }

            if (!schema.existsClass("Session")) {
                vType = graph.createVertexType("Session","BaseEvent");
                vType.createProperty("sessionId", OType.STRING);
                vType.createProperty("parentId", OType.STRING);
                vType.createProperty("startTime", OType.DATETIME);
                vType.createProperty("endTime", OType.DATETIME);
                vType.createIndex("Session.sessionId", "NOTUNIQUE", "sessionId");
                vType.createIndex("Session.parentId", "NOTUNIQUE", "parentId");
                vType.createIndex("Session.parentId_sessionId", "UNIQUE", "parentId", "sessionId");
            }

            if (!schema.existsClass("Session_User")) {
                eType = graph.createEdgeType("Session_User");
            }
            
            if (!schema.existsClass("Session_Host")) {
                eType = graph.createEdgeType("Session_Host");
            }

            if (!schema.existsClass("Session_Event")) {
                eType = graph.createEdgeType("Session_Event");
            }

            if (!schema.existsClass("Session_Domain")) {
                eType = graph.createEdgeType("Session_Domain");
            }

            if (!schema.existsClass("Session_TimeWindow1")) {
                eType = graph.createEdgeType("Session_TimeWindow1");
            }

            if (!schema.existsClass("Session_TimeWindow5")) {
                eType = graph.createEdgeType("Session_TimeWindow5");
            }

            if (!schema.existsClass("Session_TimeWindow60")) {
                eType = graph.createEdgeType("Session_TimeWindow60");
            }

            if (!schema.existsClass("Session_TimeWindow1440")) {
                eType = graph.createEdgeType("Session_TimeWindow1440");
            }

            if (!schema.existsClass("Error")) {
                vType = graph.createVertexType("Error");
                vType.createProperty("label", OType.STRING);
                vType.createProperty("value", OType.STRING);
                vType.createIndex("Error.value", "NOTUNIQUE", "value");                
            }
            
//            vType =  graph.getVertexType("Request");
//            vType.setSuperClass(graph.getVertexType("BaseEvent"));

            if (!schema.existsClass("Request")) {
                vType = graph.createVertexType("Request","BaseEvent");
                vType.createProperty("requestId", OType.STRING);
                vType.createProperty("parentId", OType.STRING);
                vType.createProperty("startTime", OType.DATETIME);
                vType.createProperty("endTime", OType.DATETIME);
                vType.createProperty("status", OType.STRING);
                vType.createProperty("errorMsg", OType.STRING);
                vType.createProperty("bytesIn", OType.LONG);
                vType.createProperty("bytesOut", OType.LONG);
                vType.createIndex("Request.requestId", "NOTUNIQUE", "requestId");
                vType.createIndex("Request.parentId", "NOTUNIQUE", "parentId");
                vType.createIndex("Request.parentId_requestId", "UNIQUE", "parentId", "requestId");
                
            }

            if (!schema.existsClass("Request_Error")) {
                eType = graph.createEdgeType("Request_Error");
            }

            if (!schema.existsClass("Request_Event")) {
                eType = graph.createEdgeType("Request_Event");
            }

            if (!schema.existsClass("Request_User")) {
                eType = graph.createEdgeType("Request_User");
            }

            if (!schema.existsClass("Request_Session")) {
                eType = graph.createEdgeType("Request_Session");
            }

            if (!schema.existsClass("Request_Domain")) {
                eType = graph.createEdgeType("Request_Domain");
            }

            if (!schema.existsClass("Request_Host")) {
                eType = graph.createEdgeType("Request_Host");
            }

            if (!schema.existsClass("Request_TimeWindow1")) {
                eType = graph.createEdgeType("Request_TimeWindow1");
            }

            if (!schema.existsClass("Request_TimeWindow5")) {
                eType = graph.createEdgeType("Request_TimeWindow5");
            }

            if (!schema.existsClass("Request_TimeWindow60")) {
                eType = graph.createEdgeType("Request_TimeWindow60");
            }

            if (!schema.existsClass("Request_TimeWindow1440")) {
                eType = graph.createEdgeType("Request_TimeWindow1440");
            }

            if (!schema.existsClass("Transaction")) {
                vType = graph.createVertexType("Transaction");
                vType.createProperty("transactionId", OType.STRING);
                vType.createProperty("parentId", OType.STRING);
                vType.createProperty("status", OType.STRING);
                vType.createProperty("timestamp", OType.DATETIME);
                vType.createIndex("Transaction.transactionId", "NOTUNIQUE", "transactionId");
                vType.createIndex("Transaction.parentId", "NOTUNIQUE", "parentId");
                vType.createIndex("Transaction.parentId_transactionId", "UNIQUE", "parentId", "transactionId");
                
            }

            if (!schema.existsClass("Transaction_User")) {
                eType = graph.createEdgeType("Transaction_User");
            }
            
            if (!schema.existsClass("Transaction_Host")) {
                eType = graph.createEdgeType("Transaction_Host");
            }
            
            if (!schema.existsClass("Transaction_Domain")) {
                eType = graph.createEdgeType("Transaction_Domain");
            }

            if (!schema.existsClass("Transaction_Event")) {
                eType = graph.createEdgeType("Transaction_Event");
            }

            if (!schema.existsClass("Transaction_TimeWindow1")) {
                eType = graph.createEdgeType("Transaction_TimeWindow1");
            }

            if (!schema.existsClass("Transaction_TimeWindow5")) {
                eType = graph.createEdgeType("Transaction_TimeWindow5");
            }

            if (!schema.existsClass("Transaction_TimeWindow60")) {
                eType = graph.createEdgeType("Transaction_TimeWindow60");
            }

            if (!schema.existsClass("Transaction_TimeWindow1440")) {
                eType = graph.createEdgeType("Transaction_TimeWindow1440");
            }

            if (!schema.existsClass("Exception")) {
                vType = graph.createVertexType("Exception","BaseEvent");
                vType.createProperty("timestamp", OType.DATETIME);
                vType.createProperty("heading", OType.STRING);
                vType.createProperty("rawData", OType.STRING);
            }

            if (!schema.existsClass("Exception_TimeWindow1")) {
                eType = graph.createEdgeType("Exception_TimeWindow1");
            }

            if (!schema.existsClass("Exception_TimeWindow5")) {
                eType = graph.createEdgeType("Exception_TimeWindow5");
            }

            if (!schema.existsClass("Exception_TimeWindow60")) {
                eType = graph.createEdgeType("Exception_TimeWindow60");
            }

            if (!schema.existsClass("Exception_TimeWindow1440")) {
                eType = graph.createEdgeType("Exception_TimeWindow1440");
            }

            if (!schema.existsClass("Exception_Host")) {
                eType = graph.createEdgeType("Exception_Host");
                eType.createProperty("timestamp", OType.DATETIME);
            }
            
            if (!schema.existsClass("Exception_Event")) {
                eType = graph.createEdgeType("Exception_Event");                
            }

            if (!schema.existsClass("ExceptionElement")) {
                vType = graph.createVertexType("ExceptionElement");
                vType.createProperty("value", OType.STRING);
                vType.createIndex("ExceptionElement.value", "NOTUNIQUE", "value");
            }

            if (!schema.existsClass("Exception_ExceptionElement")) {
                eType = graph.createEdgeType("Exception_ExceptionElement");
            }
            
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
                vType.createProperty("view", OType.STRING);
                vType.createIndex("ActionEvent.key", "NOTUNIQUE", "key");
                vType.createIndex("ActionEvent.command", "NOTUNIQUE", "command");
            }
            
            if (!schema.existsClass("KeyBoardEvent")) {
                vType = graph.createVertexType("KeyBoardEvent");
                vType.createProperty("command", OType.STRING);
                vType.createProperty("target", OType.STRING);
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
                vType.createProperty("parent", OType.STRING);
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
                vType.createProperty("view", OType.STRING);
                vType.createIndex("WindowEvent.length", "NOTUNIQUE", "length");
            }
            
            if (!schema.existsClass("WindowScrollEvent")) {
                vType = graph.createVertexType("WindowScrollEvent");
                vType.createProperty("orientation", OType.STRING);
                vType.createProperty("direction", OType.STRING);
                vType.createProperty("context", OType.STRING);
                vType.createIndex("WindowScrollEvent.context", "NOTUNIQUE", "context");
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
            }
            
            if (!schema.existsClass("Metric_Event")) {
                eType = graph.createEdgeType("Metric_Event");
                eType.createProperty("type", OType.STRING);
                // Use FullText index in query through CONTAINSTEXT operator
                eType.createIndex("MetricEvent.type", "FULLTEXT", "type");
            }
            
            if (!schema.existsClass("Session_Domain")) {
                eType = graph.createEdgeType("Metric_Event");
            }
            
            if (!schema.existsClass("Session_Pattern")) {
                eType = graph.createEdgeType("Session_Pattern");
            }
            
            if (!schema.existsClass("Session_ErrorPattern")) {
                eType = graph.createEdgeType("Session_ErrorPattern");
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
    public void deleteTablesContent(String name) throws ClientProtocolException, IOException {
        
        new com.metron.orientdb.OrientRest().postSql("delete vertex "+ name);
    }
}
