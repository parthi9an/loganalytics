package com.metron.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.metron.common.ApplicationContext;
import com.metron.service.JmsService;

public class ApplicationContextListener implements ServletContextListener {
    
    private Thread thread = null;

    public void contextDestroyed(ServletContextEvent arg0) {
        thread.stop();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ApplicationContext.getInstance().setServletContext(servletContextEvent.getServletContext());
        thread = new Thread(new JmsService());
        thread.start();
    }

}
