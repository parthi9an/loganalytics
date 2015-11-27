package com.metron.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.util.NodeUtil;

/**
 * @author satheesh
 */

public class Node {

    private String seperator = "-";

    private HashMap<String, ArrayList<Node>> relations = new HashMap<String, ArrayList<Node>>();

    private HashMap<String, Object> properties = new HashMap<String, Object>();

    public Node(JSONObject json) {

        Iterator<String> keys = json.keys();

        ArrayList<String> sortedKeys = new ArrayList<String>();

        while (keys.hasNext()) {
            String key = keys.next();
            sortedKeys.add(key);
        }

        Collections.sort(sortedKeys, new comp());
        for (int i = 0; i < sortedKeys.size(); i++) {
            String key = sortedKeys.get(i);
            try {
                if (key.contains(seperator)) {
                    String[] keyl = key.split(seperator);
                    int lev = keyl.length;
                    JSONArray resultArr = json.getJSONArray(key);
                    // HACK : find an better way to identify if it is an child
                    // of other
                    if (lev != 2) {
                        System.out.println("ADDED TO unprocessed " + key + "LENGTH"
                                + resultArr.length());
                        this.appendToCorrespondingChild(key, resultArr);
                        continue;
                    }

                    String rkey = keyl[lev - 1];
                    for (int j = 0; j < resultArr.length(); j++) {
                        String jo = resultArr.getString(j);
                        Node node = new Node(new JSONObject(jo));
                        this.addChild(rkey, node);
                    }
                    System.out.println("ADDED TO processed " + key + "LENGTH" + resultArr.length());

                } else {
                    if (json.get(key) instanceof JSONArray) {
                        JSONArray ja = json.getJSONArray(key);
                        boolean isJson = false;
                        for (int j = 0; j < ja.length(); j++) {
                            if (ja.get(j) instanceof JSONObject) {
                                Node innerNode = new Node(ja.getJSONObject(j));
                                this.addChild(key, innerNode);
                                isJson = true;
                            }

                            if (isJson) {
                                continue;
                            }

                        }
                    }
                    Object value = json.get(key);

                    if (key.equals("id") || key.equals("@rid")) {
                        value = json.getString(key).replace("#", "");
                    }

                    if (!json.has("id") && key.equals("@rid")) {
                        key = "id";
                    }
                    this.properties.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject(properties);

        Set<String> unwantedKeys = NodeUtil.getUnwantedKeys();

        for (String s : unwantedKeys) {
            jo.remove(s);
        }

        Iterator it = relations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            // System.out.println(pair.getKey() + " = " + pair.getValue());
            ArrayList<Node> nodes = (ArrayList<Node>) pair.getValue();
            JSONArray nodesArr = new JSONArray();
            try {
                for (int i = 0; i < nodes.size(); i++) {
                    JSONObject joa = nodes.get(i).toJson();
                    nodesArr.put(joa);
                }
                jo.put((String) pair.getKey(), nodesArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jo;
    }

    private void appendToCorrespondingChild(String key, JSONArray arr) {
        // maping with the corresponding data

        HashMap<String, Node> postCommentsMap = new HashMap<String, Node>();
        String[] keyl = key.split(seperator);
        int partsLength = keyl.length;
        String parentKey = keyl[partsLength - 2];
        String curentKey = keyl[partsLength - 1];
        String inKey = "in_" + curentKey;
        String outKey = "out_" + curentKey;
        for (int j = 0; j < arr.length(); j++) {
            try {
                JSONObject jo = arr.getJSONObject(j);
                if (!jo.has(inKey)) {
                    System.out.println("ERROR -------" + inKey);
                }
                Node node = new Node(jo);
                JSONArray inArray = jo.getJSONArray(inKey);

                for (int i = 0; i < inArray.length(); i++) {
                    String edgeKey = inArray.getString(i);
                    postCommentsMap.put(edgeKey, node);
                    // System.out.println(" - EDGE KEY" + edgeKey);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // System.out.println("------------------" + parentKey);
        if (!this.relations.containsKey(parentKey)) {
            return;
        }
        ArrayList<Node> nodes = this.relations.get(parentKey);

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            // System.out.println(node.getProperties().get(outKey));
            Object outks = node.getProperties().get(outKey);
            JSONArray outKeys = null;
            if (outks == null || outks.toString() == "null") {
                continue;
            }
            outKeys = (JSONArray) outks;

            for (int j = 0; j < outKeys.length(); j++) {
                String outk;
                try {
                    outk = outKeys.getString(j);
                    if (postCommentsMap.containsKey(outk)) {
                        Node commentNode = postCommentsMap.get(outk);
                        node.addChild(curentKey, commentNode);
                    } else {
                        System.out.println("NOT FOUND");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public void addChild(String key, Node node) {
        if (!this.relations.containsKey(key)) {
            ArrayList<Node> nodes = new ArrayList<Node>();
            this.relations.put(key, nodes);
        }
        ArrayList<Node> nodes = this.relations.get(key);
        nodes.add(node);
    }

    class comp implements Comparator<String> {
        public int compare(String o1, String o2) {
            return Integer.compare(o1.length(), o2.length());
        }
    }
}
