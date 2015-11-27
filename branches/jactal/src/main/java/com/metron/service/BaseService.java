package com.metron.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metron.controller.QueryWhereBuffer;
import com.metron.orientdb.OrientRest;
import com.metron.orientdb.OrientUtils;
import com.metron.orientdb.RestUtils;
import com.metron.service.interfaces.IBaseService;
import com.metron.util.Utils;

public class BaseService implements IBaseService {
    
    
    @Override
    public JSONObject getEvents(String timestamp, String hostId, String getPage) {
        JSONObject result = new JSONObject();
        StringBuffer query = new StringBuffer();
        QueryWhereBuffer whereClause = new QueryWhereBuffer();
        String order = "ASC";
        try {

            if (timestamp != null) {
                Date date = Utils.parseEventDate(timestamp);
                if (getPage == null || getPage.equals("next")) {
                    whereClause.append("timestamp >= '" + timestamp + "' AND timestamp <= '"
                            + changeDateBySeconds(date, +30) + "'");
                } else if (getPage.equals("prev")) {
                    whereClause.append("timestamp <= '" + timestamp + "' AND timestamp >= '"
                            + changeDateBySeconds(date, -30) + "'");
                    order = "DESC";
                }
            }

            if (hostId != null) {
                whereClause.append("OUTE('Event_Host')[0].in='#" + hostId + "'");
            }

            query.append("select from Event"
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : "") + " Order by timestamp "+ order);

            String data = new OrientRest().doSql(query.toString());
            JSONArray events = (JSONArray) RestUtils.convertunFormatedToFormatedJson(
                    data.toString(), null, true);
            if(getPage != null && getPage.equals("prev")){
                ArrayList<JSONObject> revEvents = new ArrayList<JSONObject>();
                for(int i=0;i<events.length();i++){
                    revEvents.add(events.getJSONObject(i));
                }
                Collections.reverse(revEvents);
                events = new JSONArray(revEvents);                
            }
            result.put("results", events);
            result.put("totalrecords", getCount("select count(*) as count from Event"
                    + ((!whereClause.toString().equals(""))
                            ? " Where " + whereClause.toString()
                            : "")));
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Long getCount(String sql) {
        String data = new com.metron.orientdb.OrientRest().doSql(sql);
        long count = 0;
        try {
            JSONObject jsondata = new JSONObject(data.toString());
            JSONArray resultArr = jsondata.getJSONArray("result");
            count = resultArr.getJSONObject(0).getLong("count");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return count;
    }

    @Override
    public Long getTSFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis() / 1000;
    }

    @Override
    public int getWindow(Long last) {
        int window = 1;
        if (last == null || last < 1441) {
            return window;
        }
        if (last < 10081) {
            window = 5;
        } else if (last < 43201) {
            window = 60;
        } else if (last < 525601) {
            window = 1440;
        }

        return window;
    }

    @Override
    public String getStartDateFromLast(Long last) {
        Long diff = (last * 60 * 1000);
        Long lastTimeStamp = System.currentTimeMillis() - diff;
        Date date = new Date(lastTimeStamp);
        return OrientUtils.convertDatetoorientDbDate(date);
    }
    
    @Override
    public Long getTsFromLast(Long last) {
        Long diff = (last * 60 * 1000);
        Long lastTimeStamp = System.currentTimeMillis() - diff;
        return lastTimeStamp / 1000;

    }
    @Override
    public String changeDateBySeconds(Date date, int seconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        long time = c.getTimeInMillis();
        long modifiedTime = time;
        modifiedTime = time + (seconds * 1000);
        // modifiedTime = time - (60 * 1000);
        return OrientUtils.convertDatetoorientDbDate(new Date(modifiedTime));

    }

    @Override
    public JSONArray sortByTimestamp(JSONArray inputArray) {
        
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();

        for (int i = 0; i < inputArray.length(); i++) {
            try {
                jsonValues.add(inputArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                Date valA = new Date();
                Date valB = new Date();

                try {
                    valA = Utils.parseEventDate((String) a.get("timestamp"));
                    valB = Utils.parseEventDate((String) b.get("timestamp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < inputArray.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

}
