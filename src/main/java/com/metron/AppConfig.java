package com.metron;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metron.common.ApplicationContext;

public class AppConfig {

    private Properties prop = null;

    public static AppConfig _instance = null;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public AppConfig() {

    }

    public static AppConfig getInstance() {
        if (_instance == null) {
            _instance = new AppConfig();
            _instance.init();
        }
        return _instance;
    }

    public void init() {
        try {
            InputStream stream = ApplicationContext.getInstance().getResourceStream(
                    "conf/application.properties");
            prop = new Properties();
            prop.load(stream);
            System.out.println(prop.getProperty("db.host"));
            System.out.println(prop.getProperty("db.userName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  String getString(String key) {
        return _instance.get(key).toString();
    }

    public Object get(String key) {
        return prop.getProperty(key);
    }
    
    public int getInt(String key) {
        return _instance.get(key) == null ? 0 : Integer.valueOf(_instance.get(key).toString());
    }
    

}
