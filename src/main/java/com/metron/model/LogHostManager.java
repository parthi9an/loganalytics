package com.metron.model;

import java.util.HashMap;

/**
 * @author satheesh
 */

public class LogHostManager {

    private static HashMap<String, String> hostInfo = new HashMap<String, String>();

    public static void addHostInfo(String logstashHost, String host) {
        hostInfo.put(logstashHost, host);
    }

    public static String getHostInfo(String logstashHost) {

        return (hostInfo.get(logstashHost) != null) ? hostInfo.get(logstashHost) : "anonymous_"
                + logstashHost;
        // return hostInfo.get(logstashHost);
    }

}
