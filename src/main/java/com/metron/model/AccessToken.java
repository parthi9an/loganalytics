package com.metron.model;

import java.util.HashMap;

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

    public boolean isValidToken(OrientBaseGraph graph, String uName, String accessToken) {
        OrientVertex token = OrientUtils.getVertex(graph,
                "select *  from AccessToken where user_name = '" + uName
                + "' and access_token='" + accessToken + "'");
        if(token != null)
            return true;
        return false;
    }

}
