package com.metron.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;

import com.metron.postgres.JdbcManager;

public class PersistEvent {
    
    private Object Sessionid;

    public void save(Map<String, Object> attributes, Map<String, Object> metricValueAttributes,
            String tableName) throws ClassNotFoundException, SQLException {
        
        metricValueAttributes.put("metric_timestamp", new Timestamp((Long)attributes.get("metric_timestamp")));
                
        Sessionid = attributes.get("metric_session_id");
        
        insertdata(metricValueAttributes,tableName);
    }
    
    /**
     * Creates a row in session table if doesn't exist & insert the data 
     * to associated event table.
     * 
     * @param dataMap contains column name's & associated values
     * @param tableName specify the name of table to insert data
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void insertdata(Map<String, Object> dataMap, String tableName) throws SQLException, ClassNotFoundException {
        
        Connection c = JdbcManager.getInstance().getConnection();
        
        String fKV = "select id from session where metric_session_id="+Sessionid;
        int id = 0;
        PreparedStatement preparedStmt = c.prepareStatement(fKV);
        ResultSet rs = preparedStmt.executeQuery();
        while (rs.next()) {id= rs.getInt("id");}
   
        if(id == 0){
            String insertsession = "INSERT INTO session (metric_session_id) values ('"+Sessionid+"')";
            preparedStmt = c.prepareStatement(insertsession,new String [] {"id"});
            preparedStmt.executeUpdate();
            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
            if (null != generatedKeys && generatedKeys.next()) {
                 id = generatedKeys.getInt(1);
            }
        }        
        
        //Inserting Foreign Key
        dataMap.put("sid", id);
        //Constructing a query to insert data to associated Event Table
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder placeholders = new StringBuilder();

        for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
            sql.append(iter.next());
            placeholders.append("?");

            if (iter.hasNext()) {
                sql.append(",");
                placeholders.append(",");
            }
        }

        sql.append(") VALUES (").append(placeholders).append(")");
        preparedStmt = c.prepareStatement(sql.toString());
        int i = 1;

        for (Object value : dataMap.values()) {
            if(value.getClass().equals(Integer.class)){
                preparedStmt.setObject(i, (Integer)value);
            }else if(value.getClass().equals(Timestamp.class)){
                preparedStmt.setObject(i, (Timestamp)value);
            }
            else
                preparedStmt.setObject(i, value);
            i++;
        }
        
        preparedStmt.executeUpdate();
        
    }
    
}
