package com.metron.orientdb;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.metron.model.CisEventMappings;


public class DatabasesClearService {

    public static void main(String[] args) {
               
        List<String> postgresTables = CisEventMappings.getInstance().getEventMapping("PostgresTables");
        List<String> orientDBClasses = CisEventMappings.getInstance().getEventMapping("OrientDBClasses");
        DatabaseService orientservice = new DatabaseService();
        PostgresdbService postgresservice = new PostgresdbService();
        
        Iterator<String> postgresitr = postgresTables.iterator();
        Iterator<String> orientitr = orientDBClasses.iterator();
        
        while(postgresitr.hasNext()){
            postgresservice.deleteTablesContent(postgresitr.next());
        }
        while(orientitr.hasNext()){
            try {
                orientservice.deleteTablesContent(orientitr.next());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }      
        
    }

}
