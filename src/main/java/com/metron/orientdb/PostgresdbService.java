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

    private static void setUpDb() throws ClassNotFoundException, SQLException {
                
        Statement st = JdbcManager.getInstance().getStatement();
        
        String qs = "CREATE TABLE IF NOT EXISTS session(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " session_id text," +
                " domain_id text ," +
                " source text, " +
                " domain_type text, " +
                " server_id text)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ActionEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " key text," +
                " command text ," +
                " view text, " +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT actionevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS KeyboardEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " command text," +
                " target text ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT keyboardevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ViewEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " name text," +
                " event text ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT viewevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS FieldEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " field text," +
                " parent text ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT fieldevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ErrorEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " err_type text," +
                " message text ," +
                " trace text ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT errorevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
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
                "CONSTRAINT environmentevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS ConfigurationEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " name text," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT configurationevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
        
        qs = "CREATE TABLE IF NOT EXISTS WindowEvent(" +
                " id SERIAL NOT NULL PRIMARY KEY," +
                " length int," +
                " height int ," +
                " view text ," +
                " timestamp timestamp without time zone, "+
                " sid INT, "+
                "CONSTRAINT windowevent_sid_fkey FOREIGN KEY (sid) REFERENCES public.session (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
        st.executeUpdate(qs);
    }

}
