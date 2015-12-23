package com.metron.model.event;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.metron.model.Host;
import com.metron.orientdb.OrientUtils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public class HostStatus extends Event {

    private String logData = null;

    public HostStatus(String logData) {
        this.logData = logData;
    }

    public void setTimeStamp(String timestamp) {
        this.setAttribute("timestamp",
                OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(timestamp)));
        // this.setAttribute("timestamp", timestamp); // timestamp format issue
        // when insert into rawEvent
    }

    @Override
    public void process() {
        host = new Host(this.getStringAttr("hostname"), this.getGraph());
        this.saveRawEvent();
        this.associateRawEventToHost();
        this.saveHostStatus();
        this.updateHost();
        this.associateRawEvent();
        this.associateHost();
        this.associateTimeWindow();

        // TBD-T1 : save rawEvent
    }

    private void associateTimeWindow() {
        // TODO Auto-generated method stub

        DURATION duration = DURATION.ONEMIN;
        this.addEdge(this.getTimeWindow(duration), "HostStatus_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        this.addEdge(this.getTimeWindow(duration), "HostStatus_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        this.addEdge(this.getTimeWindow(duration), "HostStatus_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        this.addEdge(this.getTimeWindow(duration), "HostStatus_" + duration.getTable());

    }

    private void associateHost() {
        this.addEdge(host, "HostStatus_Host");
    }

    private void associateRawEvent() {
        this.addEdge(rawEvent, "HostStatus_Event");
    }

    private void updateHost() {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("OS", this.getAttribute("Operating_System"));
        props.put("numOfProcessors", this.getAttribute("Number_of_processors"));
        props.put("totalMemory", this.getAttribute("Total_Memory"));
        host.update(props);
    }

    private void saveHostStatus() {
        OrientBaseGraph graph = this.getGraph();
        this.vertex = graph.addVertex("class:HostStatus");
        HashMap<String, Object> hostStatus = new HashMap<String, Object>();
        hostStatus.put("timestamp", this.getAttribute("timestamp"));
        hostStatus.put("hostname", this.getAttribute("hostname"));
        hostStatus.put("totalMemoryUsed", this.getAttribute("Total_Memory_Used"));
        hostStatus.put("totalMemoryUsedPer", this.getAttribute("Total_Memory_Used_Percentage"));
        hostStatus.put("totalSessions", this.getAttribute("Total_Sessions"));
        hostStatus.put("totalServerRequests", this.getAttribute("Total_Server_Requests"));
        hostStatus.put("totalDataSourceRequests", this.getAttribute("Total_Data_Source_Requests"));
        hostStatus.put("privilegeCacheAccess", this.getAttribute("Privilege_Cache_Access"));
        hostStatus.put("privilegeCacheAccessPer",
                this.getAttribute("Privilege_Cache_Access_Percentage"));
        hostStatus.put("privilegeCacheCapacity", this.getAttribute("Privilege_Cache_Capacity"));
        hostStatus.put("privilegeCacheCapacityPer",
                this.getAttribute("Privilege_Cache_Capacity_Percentage"));
        hostStatus.put("userCacheAccess", this.getAttribute("User_Cache_Access"));
        hostStatus.put("userCacheAccessPer", this.getAttribute("User_Cache_Access_Percentage"));
        hostStatus.put("userCacheCapacity", this.getAttribute("User_Cache_Capacity"));
        hostStatus.put("userCacheCapacityPer", this.getAttribute("User_Cache_Capacity_Percentage"));
        hostStatus.put("repositoryCacheAccess", this.getAttribute("Repository_Cache_Access"));
        hostStatus.put("repositoryCacheAccessPer",
                this.getAttribute("Repository_Cache_Access_Percentage"));
        hostStatus.put("repositoryCacheCapacity", this.getAttribute("Repository_Cache_Capacity"));
        hostStatus.put("repositoryCacheCapacityPer",
                this.getAttribute("Repository_Cache_Capacity_Percentage"));
        this.setProperties(hostStatus);
        this.save();
    }

    @Override
    public void parse() {
        // TBDHS : split the data by : and form the attributes
        Pattern serverStatusPattern = Pattern.compile("(.*?--{4,}\\n)(.*?)(\\n+--{4,}.*)",
                Pattern.DOTALL);
        Matcher m = serverStatusPattern.matcher(this.logData.trim());
        if (m.matches()) {
            String[] statusElements = m.group(2).split("\\n");
            for (String element : statusElements) {
                String[] detail = element.split(":\\s+");
                if (detail.length > 1) {
                    String key = detail[0].trim();
                    String value = detail[1].trim();
                    key = key.replaceAll("\\s+", "_").replace("Server_Name", "hostname");
                    value = value.replaceFirst(":\\d+\\s*$", "");
                    // Get the totalMemory from Total_Memory_Used
                    if (key.equals("Total_Memory_Used")) {
                        this.setAttribute("Total_Memory",
                                value.substring(value.indexOf("of ") + 3, value.lastIndexOf(")")));
                    }

                    if (value.matches("\\s*\\d+\\s*")) {
                        this.setAttribute(key, Long.parseLong(value));
                        continue;
                    }

                    this.setAttribute(key, value);
                    // Set the percentage key/value pair if the value matched %
                    if (value.matches(".*%.*")) {
                        this.setAttribute(key + "_Percentage",
                                Integer.parseInt(value.substring(0, value.indexOf("%"))));
                    }

                }
            }
        }

    }

}
