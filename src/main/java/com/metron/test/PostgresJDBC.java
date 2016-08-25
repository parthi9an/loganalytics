package com.metron.test;

import java.sql.DriverManager;

public class PostgresJDBC {
    
    public static void main(String args[]) {
        
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                    "postgres", "postgres");
            System.out.println("Opened database successfully");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
