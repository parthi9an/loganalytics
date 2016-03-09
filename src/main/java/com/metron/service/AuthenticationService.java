package com.metron.service;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationService {

    public JSONObject authenticate(String currUsr, String currPswd) {

        JSONObject result = new JSONObject();
        try {
            if (currUsr.compareTo("admin") == 0 && currPswd.compareTo("admin") == 0
                    || currUsr.compareTo("guest") == 0 && currPswd.compareTo("guest") == 0) {

                result.put("status", "Success");
                result.put("uName", currUsr);
                
            } else {
                result.put("status", "Failed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
