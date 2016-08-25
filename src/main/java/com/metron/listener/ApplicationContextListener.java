package com.metron.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.metron.common.ApplicationContext;

public class ApplicationContextListener implements ServletContextListener {
    
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ApplicationContext.getInstance().setServletContext(servletContextEvent.getServletContext());
    }

}
