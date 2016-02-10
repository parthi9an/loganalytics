package com.metron.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metron.model.event.CisActionEvent;
import com.metron.model.event.CisConfigurationEvent;
import com.metron.model.event.CisDomainEvent;
import com.metron.model.event.CisEnvironmentEvent;
import com.metron.model.event.CisErrorEvent;
import com.metron.model.event.CisEvent;
import com.metron.model.event.CisFieldEvent;
import com.metron.model.event.CisKeyboardEvent;
import com.metron.model.event.CisViewCloseEvent;
import com.metron.model.event.CisViewOpenEvent;
import com.metron.model.event.CisWindowEvent;
import com.metron.model.event.Event;
import com.metron.model.event.HostStatus;
import com.metron.model.event.RequestCancel;
import com.metron.model.event.RequestEnd;
import com.metron.model.event.RequestFail;
import com.metron.model.event.RequestStart;
import com.metron.model.event.ServerEvent;
import com.metron.model.event.ServerExpection;
import com.metron.model.event.SessionEnd;
import com.metron.model.event.SessionStart;
import com.metron.model.event.TransactionRollback;

public class EventFactory {

    private static EventFactory instance;

    protected Logger log = LoggerFactory.getLogger(EventFactory.class);

    public static EventFactory getInstance() {
        if (instance == null) {
            instance = new EventFactory();
        }
        return instance;
    }

    public Event parseLine(String line, String fileName, String ipAddress) {

        String hostKeyInfo = fileName.substring(fileName.indexOf("/") + 1,
                fileName.lastIndexOf("/")).replaceAll("/", "_");
        hostKeyInfo = ipAddress + "_" + hostKeyInfo;

        line = line.trim();
        if (line.matches("^\\W*$")) {
            // Ignorable Line
            return null;
        }

        if (fileName.contains("server_events")) {
            // Event Line
            return this.parseServerEventLog(line, hostKeyInfo);
        } else if (fileName.contains("server_status")) {
            // status
            // TBDLATER : Change the logstch multiline for server_status
            // return HostStatus.getInstance().parse(line, ipAddress);
            // TDBEXCEP : do an regexp match to find is it server.log
            // information
            Pattern timestampPattern = Pattern.compile(
                    "(.*)(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} -\\d{4})\\s(.*)",
                    Pattern.DOTALL);
            Matcher timeMatch = timestampPattern.matcher(line);
            if (timeMatch.matches()) {
                ServerStatusTimestampManager.addTSInfo(hostKeyInfo, timeMatch.group(2));
                // System.out.println("cs_server_status from host : " +
                // ipAddress + " , TIMESTAMP : " + m.group(2));
            }
            return this.parseServerStatusLog(line, hostKeyInfo);

        } else if (fileName.contains("csmonitor_server.log")) {
            // find and set the hostname
            setHostnameFromLog(hostKeyInfo, line, ".*?Canonical Host:\\s*([^:]+):\\d{4}.*");
        } else if (fileName.contains("server.log")) {
            // TDBEXCEP : do an regexp match to find is it server.log
            // information
            // find and set the hostname
            setHostnameFromLog(hostKeyInfo, line, "[^']+Host name is '([^']+).*");
            return this.parseServerLog(line, hostKeyInfo);
        }
        return null;
    }

    // TDBEXCEP :
    private Event parseServerStatusLog(final String eventString, final String hostKeyInfo) {

        String eventName = "";

        // define an pattern to get the heading of the summary info

        if (eventString.startsWith("| Server Stats |")) {
            eventName = "HostStatus";
        }
        return this.createServerStausEventByName(eventName, eventString, hostKeyInfo);
    }

    // TDBEXCEP :
    private Event parseServerLog(final String eventString, final String hostKeyInfo) {

        String eventName = "";

        if (eventString.startsWith("ERROR")) {
            eventName = "ServerError";
        }
        // .. do the same for ERROR , DEBUG AND WARN
        return this.createServerEventByName(eventName, eventString, hostKeyInfo);
    }

    private Event parseServerEventLog(String eventString, String hostKeyInfo) {

        String[] eventData = eventString.split("\t");
        // TASK: 1
        String eventName = this.computeEventName(eventData);

        Event e = this.createEventByName(eventName, eventData, hostKeyInfo);
        return e;
    }

    // TASK: 1
    private Event createServerStausEventByName(String eventName, String eventString,
            String hostKeyInfo) {

        Event event = null;

        if (eventName.equals("HostStatus")) {
            event = new HostStatus(eventString);
        }
        // TDBEXCEP : .. do for remaining
        if (event != null) {
            event.setTimeStamp(ServerStatusTimestampManager.getTSInfo(hostKeyInfo));
            event.parse();
            LogHostManager.addHostInfo(hostKeyInfo, event.getStringAttr("hostname"));
        }

        return event;
    }

    // TASK: 1
    private Event createServerEventByName(String eventName, String eventString, String hostKeyInfo) {

        ServerEvent event = null;

        if (eventName.equals("ServerError")) {
            event = new ServerExpection(eventString);
        }
        // TDBEXCEP : .. do for remaining
        if (event != null) {
            event.setHost(LogHostManager.getHostInfo(hostKeyInfo));
            event.parse();
        }

        return event;
    }

    // TASK: 1
    private Event createEventByName(String eventName, String[] eventData, String hostKeyInfo) {

        Event event = null;

        if (eventName.equals("RequestStart")) {
            event = new RequestStart(eventData);

        } else if (eventName.equals("RequestEnd")) {
            event = new RequestEnd(eventData);

        } else if (eventName.equals("SessionStart")) {
            event = new SessionStart(eventData);

        } else if (eventName.equals("SessionEnd")) {
            event = new SessionEnd(eventData);

        } else if (eventName.equals("RequestCancel")) {
            event = new RequestCancel(eventData);

        } else if (eventName.equals("RequestFail")) {
            event = new RequestFail(eventData);
        } else if (eventName.equals("TransactionRollback")) {
            event = new TransactionRollback(eventData);           
            event.setHost(LogHostManager.getHostInfo(hostKeyInfo));
        }

        if (event != null) {
            event.parse();
        }

        return event;
    }

    // TASK: 1
    private String computeEventName(String[] eventData) {

        // the eventName is a concatenation of the event status [2] and a prefix
        // of description [3]
        // the valid prefixes are request, session, transaction
        // e.g. StartRequest, FailRequest ...

        String status = eventData[2].toLowerCase();
        String desc = eventData[3].toLowerCase();

        if (desc.startsWith("request")) {

            if (status.startsWith("start")) {
                return "RequestStart";

            } else if (status.startsWith("end")) {
                return "RequestEnd";

            } else if (status.startsWith("cancel")) {
                return "RequestCancel";

            } else if (status.startsWith("fail")) {
                return "RequestFail";
            }

        } else if (desc.startsWith("session")) {

            if (status.startsWith("start")) {
                return "SessionStart";

            } else if (status.startsWith("end")) {
                return "SessionEnd";

            }
        } else if (desc.startsWith("transaction")) {
            if (status.startsWith("rollback")) {
                return "TransactionRollback";

            }

        }
        return "";
    }

    private void setHostnameFromLog(String hostKeyInfo, String line, String pattern) {

        Pattern hostNamePattern = Pattern.compile(pattern);
        Matcher m = hostNamePattern.matcher(line);
        if (m.matches()) {
            LogHostManager.addHostInfo(hostKeyInfo, m.group(1));
        }
    }

    public CisEvent parseCISEvent(JSONObject event) throws JSONException {
                
        JSONObject value = (JSONObject)event.get("value");
        event.remove("value");
    
        if(event.get("type").toString().compareToIgnoreCase("action") == 0){
            return new CisActionEvent(event,value);
        } else if(event.get("type").toString().compareToIgnoreCase("keyb") == 0){
            return new CisKeyboardEvent(event,value);
        } else if(event.get("type").toString().compareToIgnoreCase("view") == 0){
            if(value.get("event").toString().compareToIgnoreCase("open") == 0){
                return new CisViewOpenEvent(event,value);
            }else if(value.get("event").toString().compareToIgnoreCase("close") == 0){
                return new CisViewCloseEvent(event,value);
            }
            //return new CisViewEvent(event,metric_value);
        } else if(event.get("type").toString().compareToIgnoreCase("domain") == 0){
            return new CisDomainEvent(event,value);
        } else if(event.get("type").toString().compareToIgnoreCase("field") == 0){
            return new CisFieldEvent(event,value);
        } else if(event.get("type").toString().compareToIgnoreCase("error") == 0){
            return new CisErrorEvent(event,value);
        } else if(event.get("type").toString().compareToIgnoreCase("env") == 0){
            return new CisEnvironmentEvent(event,value);
        } else if(event.get("type").toString().compareToIgnoreCase("config") == 0){
            return new CisConfigurationEvent(event,value);
        } else if(event.get("type").toString().compareToIgnoreCase("window") == 0){
            return new CisWindowEvent(event,value);
        }
        
        return null;
       
        //Event e = new CisUiEvent(event);
        //return e;
    }

}
