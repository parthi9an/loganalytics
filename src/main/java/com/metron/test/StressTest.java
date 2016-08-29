package com.metron.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class StressTest implements Runnable {

    private String threadName;

    StressTest(String name) {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    StressTest() {
    }
    
    static List<String> lines;

    private static final String GET_URL = "http://localhost:8083/eventsender/cisEventSender?msg={metric_type:type_action,timestamp:1449277703994,session_id:382D34A70C,value:{action_name:move,source_command:toolbar,source_view:composite_view}}";

    public static void main(String[] args) throws IOException {

        //Reading from file  and randomly sending the requests
        BufferedReader reader = new BufferedReader(new FileReader("C:/Users/Saneesh/Downloads/cisco/cie/sampledata_stresstest.txt"));
        //BufferedReader reader = new BufferedReader(new FileReader("C:/Users/Saneesh/Desktop/new.txt"));
        String line = reader.readLine();
        lines = new ArrayList<String>();
        while (line != null) {
             lines.add(line);
             line = reader.readLine();
        }     
        
        (new Thread(new StressTest("Thread-1"))).start();
        (new Thread(new StressTest("Thread-2"))).start();
        (new Thread(new StressTest("Thread-3"))).start();
        (new Thread(new StressTest("Thread-4"))).start();
        
    }

    private static void sendGET() throws IOException {
        
        Random r = new Random();
        String randomLine = lines.get(r.nextInt(lines.size()));
        //URL obj = new URL(GET_URL);
        URL obj = new URL(randomLine);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            // System.out.println(response.toString());
        } else {
            System.out.println("request not worked");
        }

    }

    @Override
    public void run() {
        for (int i = 2000; i > 0; i--) {
            System.out.println("Thread: " + threadName + ", " + i);
            try {
                sendGET();
                //send();
                // System.out.println("DONE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void send() throws IOException {
        URL url = new URL(GET_URL);
        URLConnection uc = url.openConnection();
        String userpass = "admin@composite" + ":" + "admin";
        String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
        uc.setRequestProperty ("Authorization", basicAuth);
        InputStream in = uc.getInputStream();
        String myString = IOUtils.toString(in);
        System.out.println(myString);
        
        
    }

}
