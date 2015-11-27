package com.metron.orientdb;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.model.Node;

/**
 * @author satheesh
 */

public class RestUtils {

    public static Object convertunFormatedToFormatedJson(String data, String parentKey,
            boolean returnAsArray) {
        ArrayList<Node> nodes = new ArrayList<com.metron.model.Node>();
        try {
            JSONObject datajson = new JSONObject(data.toString());
            JSONArray resultArr = datajson.getJSONArray("result");
            for (int i = 0; i < resultArr.length(); i++) {
                JSONObject jo = resultArr.getJSONObject(i);
                Node node = new Node(jo);
                nodes.add(node);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        // if result has only one result and if user does not specify the output
        // Type it will return jsonObject
        if (nodes.size() == 0) {
            if (returnAsArray) {
                return new JSONArray();
            } else {
                return new JSONObject();
            }
        }
        if (!returnAsArray && nodes.size() == 1) {
            if (parentKey != null) {
                JSONObject top = new JSONObject();
                try {
                    top.put(parentKey, nodes.get(0).toJson());
                    return top;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                return nodes.get(0).toJson();
            }
        }
        JSONArray result = new JSONArray();
        if (parentKey != null) {
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put(parentKey, nodes.get(i).toJson());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result.put(jo);
            }
        } else {
            for (int i = 0; i < nodes.size(); i++) {
                result.put(nodes.get(i).toJson());
            }
        }

        return result;
    }

}
