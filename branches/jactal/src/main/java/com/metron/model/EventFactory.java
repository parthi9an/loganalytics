package com.metron.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metron.model.event.CisUiEvent;
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
            return this.parseServerEventLog(line);
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
            setHostnameFromLog(hostKeyInfo, line, ".*?Canonical Host:\\s*([^:]+):\\d{4}.*");
        } else if (fileName.contains("server.log")) {
            // TDBEXCEP : do an regexp match to find is it server.log
            // information
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

    private Event parseServerEventLog(String eventString) {

        String[] eventData = eventString.split("\t");
        // TASK: 1
        String eventName = this.computeEventName(eventData);

        Event e = this.createEventByName(eventName, eventData);
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
            LogHostManager.addHostInfo(hostKeyInfo, event.getAttribute("hostname").toString());
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
            String hostName = (LogHostManager.getHostInfo(hostKeyInfo) != null) ? LogHostManager
                    .getHostInfo(hostKeyInfo) : "anonymous_" + hostKeyInfo;
            event.setHost(hostName);
            event.parse();
        }

        return event;
    }

    // TASK: 1
    private Event createEventByName(String eventName, String[] eventData) {

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

    public Event parseCISEvent(JSONObject event) {
        Event e = new CisUiEvent(event);
        return e;
    }

}