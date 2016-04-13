package com.metron.orientdb;

import java.sql.SQLException;
import java.sql.Statement;

import com.metron.postgres.JdbcManager;

public class PostgresdbService {
    
    public static void main(String[] args) {
        try {
            setUpDb();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }   
    }

    public static void setUpDb() throws ClassNotFoundException, SQLException {
                
        Statement st = JdbcManager.getInstance().getStatement();
        
        String qs = "CREATE TABLE IF NOT EXISTS CisEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " session_id text," +
                " user_id text ," +
                " source text, " +
                " domain_type text, " +
                " version text, " +
                " server_id text)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ViewContext(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " editor text," +
                " view text ," +
                " element text, " +
                " source text)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ContextType(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " type text," +
                " viewcontextid INT)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ActionEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " key text," +
                " command text ," +
                " contexttypeid INT, " +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT actionevent_contexttypeid_fkey FOREIGN KEY (contexttypeid) REFERENCES public.contexttype (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
                + "CONSTRAINT actionevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS KeyboardEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " command text," +
                " contexttypeid INT ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT keyboardevent_contexttypeid_fkey FOREIGN KEY (contexttypeid) REFERENCES public.contexttype (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
                + "CONSTRAINT keyboardevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ViewEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " name text," +
                " event text ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT viewevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS FieldEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " field text," +
                " contexttypeid INT ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT fieldevent_contexttypeid_fkey FOREIGN KEY (contexttypeid) REFERENCES public.contexttype (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
                + "CONSTRAINT fieldevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ErrorEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " err_type text," +
                " message text ," +
                " trace text ," +
                " contexttypeid INT ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT errorevent_contexttypeid_fkey FOREIGN KEY (contexttypeid) REFERENCES public.contexttype (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
                + "CONSTRAINT errorevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS EnvironmentEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " os text," +
                " screen_x int ," +
                " screen_y int," +
                " app_x int ," +
                " app_y int," +
                " browser_type text ," +
                " browser_version text," +
                " cpu_type text ," +
                " cpu_clock double precision," +
                " cpu_cores int ," +
                " mem int," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT environmentevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ConfigurationEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " name text," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT configurationevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS WindowEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " length int," +
                " height int ," +
                " contexttypeid INT ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT windowevent_contexttypeid_fkey FOREIGN KEY (contexttypeid) REFERENCES public.contexttype (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
                + "CONSTRAINT windowevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS WindowScrollEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " orientation text," +
                " direction text," +
                " contexttypeid INT," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT windowscrollevent_contexttypeid_fkey FOREIGN KEY (contexttypeid) REFERENCES public.contexttype (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
                + "CONSTRAINT windowscrollevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.cisevent (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        if(st != null){
            st.close();
        }
    }

    public void deleteTablesContent(String table) throws SQLException {
         
        Statement st = null;
        try {
            st = JdbcManager.getInstance().getStatement();
            st.executeUpdate("truncate "+ table + " restart identity cascade");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(st != null){
                st.close();
            }
        }
    }

}
