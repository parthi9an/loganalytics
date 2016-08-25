package com.metron.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import com.metron.common.ApplicationContext;

public class CisEventKeyMappings {
    
    private static CisEventKeyMappings instance;
    private Map<String, Map<String, String>> mappings;

    public static CisEventKeyMappings getInstance() {
        if (instance == null) {
            instance = new CisEventKeyMappings();
            instance.load();
        }
        return instance;
    }

    private CisEventKeyMappings() {

    }

    @SuppressWarnings("unchecked")
    public void load() {
        mappings = new HashMap<String, Map<String, String>>();
        try {

            InputStream stream = ApplicationContext.getInstance().getResourceStream(
                    "conf/ciseventkey-mappings.json");
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

                Map<String, String> data = new HashMap<String, String>();
                Iterator<String> it2 = map.keys();

                while (it2.hasNext()) {
                    String key2 = it2.next();
                    data.put(key2, map.getString(key2));
                }

                this.mappings.put(key1, data);
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> getEventMapping(String eventname) {
        
        Map<String, String> eventkeys = mappings.get(eventname);
        
        return eventkeys;
    }


}
