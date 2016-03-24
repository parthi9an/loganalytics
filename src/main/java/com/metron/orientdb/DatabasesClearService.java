package com.metron.orientdb;

import java.util.Iterator;
import java.util.List;

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
            orientservice.deleteTablesContent(orientitr.next());
        }      
        
    }

}
