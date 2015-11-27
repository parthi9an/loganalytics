package com.metron.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metron.AppConfig;
import com.metron.model.EventFactory;
import com.metron.model.event.Event;
import com.metron.orientdb.OrientDBGraphManager;
import com.metron.orientdb.OrientUtils;
import com.metron.orientdb.DatabaseService;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public class EventTest {

    // java -classpath ".;lib/*;classes" com.metron.test.EventTest

    // java -classpath ".;target/jactal-1.0/WEB-INF/lib/*;target/classes"
    // com.metron.test.EventTest

    /**
     * 
     */
    // --------------------------------------------------------------------------------------------------//
    public void parseEvent() {

        System.out.println("Starting...");

        String[] logFiles = {"log/cs_server_status.log"};

        for (int i = 0; i < logFiles.length; i++) {
            try {
                File file = new File(logFiles[i]);
                if (!file.exists()) {
                    return;
                }
                String line = null;

                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    Event event = EventFactory.getInstance().parseLine(line, logFiles[i], null);
                    if (event != null) {
                        event.process();
                    }
                }
                br.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void mappingTest() {

    }

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        Logger log = LoggerFactory.getLogger(EventTest.class);
        AppConfig.getInstance();
        DatabaseService.setUp();
        new EventTest().parseEvent();
//        System.exit(0);
        OrientBaseGraph graph = OrientDBGraphManager.getInstance().getNonTx();
        Iterable<Vertex> vertices = OrientUtils.getVertices(graph,
                "select * From Request where OUT('Request_Session').size() = 2");

        for (Vertex v : vertices) {
            System.out.println("FIRST --------------- >");
            Iterable<Edge> edges = v.getEdges(Direction.BOTH, "Request_Session");
            int i = 0;
            for (Edge e : edges) {
                if (i == 1) {
                    e.remove();
                }
                i++;
            }

            edges = v.getEdges(Direction.BOTH, "Request_User");
            i = 0;
            for (Edge e : edges) {
                if (i == 1) {
                    e.remove();
                }
                i++;
            }

            edges = v.getEdges(Direction.BOTH, "Request_Host");
            i = 0;
            for (Edge e : edges) {
                if (i == 1) {
                    e.remove();
                }
                i++;
            }
            System.out.println("LAST --------------- >");
        }
    }

    /*
     * //@formatter:off private void refl() {
     * 
     * try {
     * 
     * Class<?> clazz = event.getClass();
     * 
     * while (!clazz.getName().equals("java.lang.Object")) {
     * 
     * System.out.println(clazz.getName()); Field[] fields =
     * clazz.getDeclaredFields(); Object instance = clazz.newInstance(); for
     * (Field field : fields) { if (field.isAnnotationPresent(EventField.class))
     * { System.out.println("field name = " + field.getName());
     * field.setAccessible(true); field.set(instance, 1); } }
     * 
     * clazz = clazz.getSuperclass();
     * 
     * } } catch (Exception ex) { ex.printStackTrace(); } } //@formatter:off
     */

}
