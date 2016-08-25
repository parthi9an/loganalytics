package com.metron.model;

import java.util.HashMap;

/**
 * @author satheesh
 */

public class ServerStatusTimestampManager {

    private static HashMap<String, String> timeInfo = new HashMap<String, String>();

    public static void addTSInfo(String logstashHost, String timestamp) {
        timeInfo.put(logstashHost, timestamp);
    }

    public static String getTSInfo(String logstashHost) {
        return timeInfo.get(logstashHost);
    }

}
