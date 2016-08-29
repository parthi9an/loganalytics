package com.metron.test;

import java.util.HashMap;

import com.metron.orientdb.OrientDBGraphManager;
import com.metron.orientdb.OrientUtils;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class OrientDBTest {
    private OrientBaseGraph baseGraph = null;
    
    private void retry(HashMap<String, Object> propsHost) {
        // TODO Auto-generated method stub
        OrientBaseGraph graph = this.getGraph();
        OrientVertex host = OrientUtils.getVertex(graph, "select from Host where @rid=#18:5");
        host.setProperties(propsHost);
        //host.save();       
       
    }
    public OrientBaseGraph getGraph() {
        if (this.baseGraph == null) {
            this.baseGraph = OrientDBGraphManager.getInstance().getNonTx();
        }

        return this.baseGraph;
    }
    public static void main(String args[]){
        
        OrientGraphFactory factory1 = OrientDBGraphManager.getInstance().getFactory();
        OrientBaseGraph graph = factory1.getNoTx();
     //   graph.command(new OCommandSQL("select from host")).execute();
        graph.shutdown(false);
        
        factory1.close();
        
        HashMap<String, Object> propsHost = new HashMap<String, Object>();
        propsHost.put("OS", "TestOS");
        propsHost.put("numOfProcessors", 4);
        propsHost.put("totalMemory", 3000);
        OrientDBTest odbt = new OrientDBTest();
        //odbt.retry(propsHost);     
    }
}
