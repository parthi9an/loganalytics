package com.metron.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.metron.postgres.JdbcManager;

public class PersistEvent {
    
    public void save(Map<String, Object> attributes, Map<String, Object> metricValueAttributes,
            String tableName) throws ClassNotFoundException, SQLException {
        
        metricValueAttributes.put("timestamp", new Timestamp((Long)attributes.get("timestamp")));
                        
        Map<String, Object> sessionprops = new HashMap<String, Object>();
        
        sessionprops.put("session_id", attributes.get("session_id"));
        sessionprops.put("source", attributes.get("source"));
        sessionprops.put("server_id", attributes.get("server_id"));
        sessionprops.put("domain_id", attributes.get("domain_id"));
        sessionprops.put("domain_type", attributes.get("domain_type"));
        
        int id = getSessionId(sessionprops,"session");
        if(id == 0){
            id = insertdata(sessionprops,"session");
        }   
        
        //Inserting Foreign Key
        metricValueAttributes.put("sid", id);
        
        insertdata(metricValueAttributes,tableName);
    }
    
    private int getSessionId(Map<String, Object> sessionprops, String string) throws ClassNotFoundException, SQLException {
        
        Connection c = JdbcManager.getInstance().getConnection();
        StringBuilder sql = new StringBuilder("select id from session where ");
        for (Iterator<Entry<String, Object>> iter = sessionprops.entrySet().iterator(); iter
                .hasNext();) {
            Entry<String, Object> pair = iter.next();
            sql.append(pair.getKey());
            sql.append("= '");
            sql.append(pair.getValue());
            sql.append("'");

            if (iter.hasNext()) {
                sql.append(" and ");
            }
        }
        
        int rowid = 0;
        PreparedStatement preparedStmt = c.prepareStatement(sql.toString());
        ResultSet rs = preparedStmt.executeQuery();
        while (rs.next()) {rowid= rs.getInt("id");}
        return rowid;
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
    public int insertdata(Map<String, Object> dataMap, String tableName) throws SQLException, ClassNotFoundException {
        
        Connection c = JdbcManager.getInstance().getConnection();
        int rowid = 0;
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
        PreparedStatement preparedStmt = c.prepareStatement(sql.toString(),new String [] {"id"});
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
        ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
        if (null != generatedKeys && generatedKeys.next()) {
             rowid = generatedKeys.getInt(1);
        }
        return rowid;
    }
    
}
