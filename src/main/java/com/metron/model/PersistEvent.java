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
            String tableName) throws SQLException {

        metricValueAttributes.put("timestamp", new Timestamp((Long) attributes.get("timestamp")));

        Map<String, Object> sessionprops = new HashMap<String, Object>();

        sessionprops.put("session_id", attributes.get("session_id"));
        sessionprops.put("source", attributes.get("source"));
        sessionprops.put("server_id", attributes.get("server_id"));
        sessionprops.put("user_id", attributes.get("user_id"));
        sessionprops.put("domain_type", attributes.get("domain_type"));
        sessionprops.put("version", (attributes.get("version") != null ? attributes.get("version") : null));

        int id = getRowId(sessionprops, "CisEvent");
        if (id == 0) {
            id = insertdata(sessionprops, "CisEvent");
        }

        // Inserting Foreign Key
        metricValueAttributes.put("sid", id);

        insertdata(metricValueAttributes, tableName);
    }
    
    private int getRowId(Map<String, Object> sessionprops, String tableName) throws SQLException {

        Connection c = null;
        int rowid = 0;
        PreparedStatement preparedStmt = null;
        try {
            c = JdbcManager.getInstance().getConnection();

            StringBuilder sql = new StringBuilder("select id from "+ tableName +" where ");
            for (Iterator<Entry<String, Object>> iter = sessionprops.entrySet().iterator(); iter
                    .hasNext();) {
                Entry<String, Object> pair = iter.next();
                sql.append(pair.getKey());
                if(pair.getValue() != null){
                    sql.append("= '");
                    sql.append(pair.getValue());
                    sql.append("'");
                }else{
                    sql.append(" IS ");
                    sql.append(pair.getValue());
                }

                if (iter.hasNext()) {
                    sql.append(" and ");
                }
            }

            preparedStmt = c.prepareStatement(sql.toString());
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                rowid = rs.getInt("id");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (preparedStmt != null)
                preparedStmt.close();
            if (c != null)
                c.close();
        }
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
    public int insertdata(Map<String, Object> dataMap, String tableName) throws SQLException {

        Connection c = null;
        int rowid = 0;
        PreparedStatement preparedStmt = null;
        try {
            c = JdbcManager.getInstance().getConnection();

            // Constructing a query to insert data to associated Event Table
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
            preparedStmt = c.prepareStatement(sql.toString(), new String[]{"id"});
            int i = 1;

            for (Object value : dataMap.values()) {
                if (value == null) {
                    preparedStmt.setNull(i,java.sql.Types.VARCHAR);
                } else if (value.getClass().equals(Integer.class)) {
                    preparedStmt.setObject(i, (Integer) value);
                } else if (value.getClass().equals(Timestamp.class)) {
                    preparedStmt.setObject(i, (Timestamp) value);
                } else
                    preparedStmt.setObject(i, value);
                i++;
            }

            preparedStmt.executeUpdate();
            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
            if (null != generatedKeys && generatedKeys.next()) {
                rowid = generatedKeys.getInt(1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (preparedStmt != null)
                preparedStmt.close();
            if (c != null)
                c.close();
        }
        return rowid;
    }

    public void save(Map<String, Object> attributes, Map<String, Object> metricValueAttributes,
            Map<String, Object> contextType, Map<String, Object> contextAttributes, String tableName) throws SQLException {
        
        int viewContextId = getRowId(contextAttributes, "ViewContext");
        if (viewContextId == 0) {
            viewContextId = insertdata(contextAttributes, "ViewContext");
        }
        contextType.put("viewcontextid", viewContextId);
        
        int contextTypeId = getRowId(contextType, "ContextType");
        if (contextTypeId == 0) {
            contextTypeId = insertdata(contextType, "ContextType");
        }
        metricValueAttributes.put("contexttypeid", contextTypeId);
        
        save(attributes, metricValueAttributes, tableName);
    }

    public void save(Map<String, Object> attributes, Map<String, Object> metricValueAttributes,
            Map<String, Object> contextType, Map<String, Object> contextAttributes,
            Map<String, Object> dialogContextType, Map<String, Object> dialogContextAttributes,
            String tableName) throws SQLException {
        
        int dialogViewContextId = getRowId(dialogContextAttributes, "ViewContext");
        if (dialogViewContextId == 0) {
            dialogViewContextId = insertdata(dialogContextAttributes, "ViewContext");
        }
        dialogContextType.put("dialogsourceid", dialogViewContextId);
        
        int dialogContextTypeId = getRowId(dialogContextType, "ContextType");
        if (dialogContextTypeId == 0) {
            dialogContextTypeId = insertdata(dialogContextType, "ContextType");
        }
        contextAttributes.put("contexttypeid", dialogContextTypeId);
        
        save(attributes, metricValueAttributes, contextType, contextAttributes, tableName);
    }
    
}
