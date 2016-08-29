package com.metron.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.metron.common.ApplicationContext;

public class CisEventMappings {

    private static CisEventMappings instance;
    private Map<String, List<String>> mappings;

    public static CisEventMappings getInstance() {
        if (instance == null) {
            instance = new CisEventMappings();
            instance.load();
        }
        return instance;
    }

    private CisEventMappings() {

    }

    @SuppressWarnings("unchecked")
    public void load() {
        mappings = new HashMap<String, List<String>>();
        try {

            InputStream stream = ApplicationContext.getInstance().getResourceStream(
                    "conf/cisevent-mappings.json");
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuffer sb = new StringBuffer();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject obj = new JSONObject(sb.toString());
            Iterator<String> it1 = obj.keys();
            while (it1.hasNext()) {
                String eventname = it1.next();
                JSONArray jsonarray = obj.getJSONArray(eventname);
                List<String> keys = new ArrayList<String>();
                for (int i=0; i<jsonarray.length(); i++) {
                    keys.add( jsonarray.getString(i) );
                }
                mappings.put(eventname, keys);
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<String> getEventMapping(String eventname) {
        
        List<String> eventkeys = mappings.get(eventname);
        
        return eventkeys;
    }

}
