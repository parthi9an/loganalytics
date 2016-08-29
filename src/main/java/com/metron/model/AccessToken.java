package com.metron.model;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class AccessToken extends BaseModel {

    public AccessToken(String uName, String loginTime, String accessToken, OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph, uName,loginTime, accessToken);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:AccessToken");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("user_name", uName);
            props.put("login_time", loginTime);
            props.put("access_token", accessToken);
            this.setProperties(props);
            this.save();
        }
    }

    public AccessToken() {}

    private OrientVertex find(OrientBaseGraph graph, String uName,String loginTime,
            String accessToken) {
        OrientVertex token = OrientUtils.getVertex(graph,
                "select *  from AccessToken where user_name = '" + uName + "' and login_time='" + loginTime
                + "' and access_token='" + accessToken + "'");
        return token;
    }

    public boolean isValidToken(String uName, String accessToken) {
        
        String token = new com.metron.orientdb.OrientRest().doSql("select *  from AccessToken where user_name = '" + uName
                + "' and access_token = '" + accessToken + "'");
        try {
            if(new JSONObject(token).getJSONArray("result").length() > 0)
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int insertData(String accessToken, String loginTime, String currUsr) throws JSONException, ClientProtocolException, IOException {
        
        JSONObject accessTokenData = new JSONObject();
        accessTokenData.put("access_token", accessToken);
        accessTokenData.put("login_time", loginTime);
        accessTokenData.put("user_name", currUsr);
        return new com.metron.orientdb.OrientRest()
                .postSql("insert into accesstoken content " + accessTokenData);
    }

}
