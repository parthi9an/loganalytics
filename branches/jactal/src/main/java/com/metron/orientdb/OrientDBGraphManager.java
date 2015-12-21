package com.metron.orientdb;

import com.metron.AppConfig;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class OrientDBGraphManager {

    private static OrientDBGraphManager _instance = null;
    public String userName = null;
    public String password = null;
    public String host = null;
    public String db = null;

    public static OrientDBGraphManager getInstance() {
        if (_instance == null) {
            _instance = new OrientDBGraphManager();
        }
        return _instance;
    }

    OrientDBGraphManager() {
        userName = AppConfig.getInstance().getString("db.userName");
        password = AppConfig.getInstance().getString("db.password");
        host = AppConfig.getInstance().getString("db.host");
        db = AppConfig.getInstance().getString("db.name");
    }
    public void createDB(){
        boolean dbExists = false;
        try {
            dbExists = new OServerAdmin("remote:" + host + "/" + db).connect(userName, password).existsDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!dbExists){
            try {
                new OServerAdmin("remote:" + host + "/" + db).connect(userName, password).createDatabase("plocal");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static OrientGraphFactory graphFactory;

    public OrientGraphFactory getFactory() {

        //OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(1000);
        // OGlobalConfiguration.RID_BAG_SBTREEBONSAI_TO_EMBEDDED_THRESHOLD.setValue(1000);
        if (graphFactory == null) {
            graphFactory =  new OrientGraphFactory("remote:" + host + "/" + db, userName, password)
                    .setupPool(0, 1000);
//            graphFactory.declareIntent(new OIntentMassiveInsert());
            // OGlobalConfiguration.NETWORK_BINARY_DNS_LOADBALANCING_ENABLED.setValue(true);
        }
        // OGlobalConfiguration.dumpConfiguration(System.out);

        return graphFactory;
    }

    public synchronized OrientGraphNoTx getNonTx() {

        OrientGraphNoTx graphNonTx = null;
        int i = 0;
        while (graphNonTx == null) {
            try {
                graphNonTx = getFactory().getNoTx();
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
            if (i == 10) {
                break;
            }
        }

        return graphNonTx;

    }

}
