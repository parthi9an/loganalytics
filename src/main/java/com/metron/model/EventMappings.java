package com.metron.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import com.metron.common.ApplicationContext;

/**
 * This mappings file specified for each event type - a set of column
 * number/attribute name associations.
 * 
 * @author Sanjay
 * 
 */

// TASK: 1
public class EventMappings {

    private static EventMappings instance;
    private Map<String, Map<String, Integer>> mappings;

    public static EventMappings getInstance() {
        if (instance == null) {
            instance = new EventMappings();
            instance.load();
        }
        return instance;
    }

    private EventMappings() {

    }

    public Map<String, Integer> getEventMapping(String eventName) {
        Map<String, Integer> e = new HashMap<String, Integer>();
        e.putAll(mappings.get("Default"));
        e.putAll(mappings.get(eventName));
        return e;
    }

    public int getDefaultColumnSize() {
        return mappings.get("Default").size();
    }

    public void load() {
        mappings = new HashMap<String, Map<String, Integer>>();
        try {

            InputStream stream = ApplicationContext.getInstance().getResourceStream(
                    "conf/event-mappings.json");
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuffer sb = new StringBuffer();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject obj = new JSONObject(sb.toString());
            Iterator<String> it1 = obj.keys();

            while (it1.hasNext()) {
                String key1 = it1.next();
                JSONObject map = obj.getJSONObject(key1);

                Map<String, Integer> data = new HashMap<String, Integer>();
                Iterator<String> it2 = map.keys();

                while (it2.hasNext()) {
                    String key2 = it2.next();
                    data.put(key2, map.getInt(key2));
                }

                this.mappings.put(key1, data);
            }

            br.close();

            // System.out.println(new JSONObject(this.mappings).toString(1));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
