package com.metron.test;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.metron.model.Odb;


public class Test {

    public void createPatient() throws JSONException {
        
        JSONObject event = new JSONObject();
        event.put("patientId", "3A3C2AFB-FFFA-4E69-B4E6-73C1245D5D14");
        event.put("maritalStatus", "married");
        event.put("gender", "male");
        event.put("dateOfBirth", "1975-04-09 05:25:00");
        event.put("language", "English");
        event.put("race", "White");
        event.put("povertyIndex", 1);
        event.put("entityType", "Patient");
        Odb odb = Odb.getInstance();
        odb.loadData(event);
    }

    public void createAdmission() throws JSONException {
        JSONObject event = new JSONObject();
        event.put("patientId", "3A3C2AFB-FFFA-4E69-B4E6-73C1245D5D14");
        event.put("admissionId", 525);
        event.put("startDate", "1975-04-09 05:25:00");
        event.put("endDate", "1975-04-09 05:25:00");
        event.put("entityType", "Admission");
        Odb odb = Odb.getInstance();
        odb.loadData(event);
    }
    
    public void submitData() {
    	String json = "{\"type\":\"LabData\",\"patientId\":\"C242E3A4-E785-4DF1-A0E4-3B568DC88F2E\",\"admissionId\":3,\"labName\":\"CBC: MCH\",\"labValue\":\"32.4\",\"labUnits\":\"pg\",\"labDateTime\":\"2010-02-28 23:07:01.490\",\"timestamp\":\"2015-06-19T06:02:54.271Z\",\"entityType\":\"LabData\"}";
    	try {
            JSONObject event = new JSONObject(json);
            Odb odb = Odb.getInstance();
            odb.loadData(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws JSONException {
        // new Test().createPatient();
        new Test().submitData();
        System.out.println("*** Completed ***");
       // new Test().test();
    }

}
