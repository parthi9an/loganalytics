package com.metron.common;

import java.io.InputStream;

import javax.servlet.ServletContext;


public class ApplicationContext {

    private static ApplicationContext _instance = null;

    private ServletContext servletContext = null;
    private String realPath = "";

    public static ApplicationContext getInstance() {
        if (_instance == null) {
            _instance = new ApplicationContext();
        }
        return _instance;
    }

    public InputStream getResourceStream(String fileName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(fileName);
    }

    public void setServletContext(ServletContext context) {
        this.servletContext = context;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

}
