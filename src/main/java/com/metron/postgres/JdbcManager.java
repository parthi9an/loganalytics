package com.metron.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.metron.AppConfig;

public class JdbcManager {
    
    private static JdbcManager _instance = null;
    public String userName = null;
    public String password = null;
    public String host = null;
    public String db = null;
    
    JdbcManager(){
        userName = AppConfig.getInstance().getString("postgres.username");
        password = AppConfig.getInstance().getString("postgres.password");
        host = AppConfig.getInstance().getString("postgres.host");
        db = AppConfig.getInstance().getString("postgres.db");
    }

    public static JdbcManager getInstance() {
        if (_instance == null) {
            _instance = new JdbcManager();
        }
        return _instance;
    }
    
    private static Connection connection;
    
    public Connection getConnection() throws SQLException, ClassNotFoundException  
    {     
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://"+host+":5432/"+db,
                userName, password);
        return connection;  
    }
    
private static Statement statement;
    
    public Statement getStatement() throws ClassNotFoundException, SQLException 
    {     
        statement = JdbcManager.getInstance().getConnection().createStatement();
        return statement;  
    }
}
