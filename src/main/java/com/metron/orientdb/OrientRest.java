package com.metron.orientdb;

import java.net.URLEncoder;

import com.metron.http.HttpResponseData;
import com.metron.http.WSClient;

/**
 * @author satheesh
 */

public class OrientRest {

    public String doSql(String sql) {

        return doSql(sql, null);
    }

    public String doSql(String sql, Integer limit) {

        String userName = OrientDBGraphManager.getInstance().userName;
        String password = OrientDBGraphManager.getInstance().password;
        String url = _getRestUrl(URLEncoder.encode(sql));

        if (limit != null) {
            url += "/" + limit;
        }
        WSClient client = new WSClient();
        HttpResponseData data = client.get(url, userName, password);

        return data.toString();
    }
    
    public String postSql(String sql) {

        return postSql(sql, null);
    }
    
    public String postSql(String sql, Integer limit) {

        String userName = OrientDBGraphManager.getInstance().userName;
        String password = OrientDBGraphManager.getInstance().password;
        String url = _getRestPostUrl(URLEncoder.encode(sql));

        if (limit != null) {
            url += "/" + limit;
        }
        WSClient client = new WSClient();
        HttpResponseData data = client.post(url, userName, password);

        return data.toString();
    }

    private static String _getRestUrl(String sql) {

        String host = OrientDBGraphManager.getInstance().host;
        String db = OrientDBGraphManager.getInstance().db;
        String url = "http://%s:2480/query/%s/sql/%s";

        return String.format(url, host, db, sql);

    }
    
    private static String _getRestPostUrl(String sql) {

        String host = OrientDBGraphManager.getInstance().host;
        String db = OrientDBGraphManager.getInstance().db;
        String url = "http://%s:2480/command/%s/sql/%s";

        return String.format(url, host, db, sql);

    }
}
