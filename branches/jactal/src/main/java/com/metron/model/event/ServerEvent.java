package com.metron.model.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.metron.model.Host;

/**
 * @author satheesh
 */

public class ServerEvent extends Event {

    protected String hostName = null;
    protected String data = null;

    public ServerEvent(String data) {
        this.data = data;
    }

    public void setHost(String host) {
        this.hostName = host;
        this.setAttribute("hostname", host);
    }

    @Override
    public void process() {
        // TBD-T1 : save rawEvent
        host = new Host(this.getStringAttr("hostname"), this.getGraph());
        this.saveRawEvent();
        this.associateRawEventToHost();
    }

    @Override
    public void parse() {
        // TDBEXCEP : get the timestamp and necessary data...
        Pattern serverEventPattern = Pattern.compile(
                "(.*?)(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} -\\d{4})\\s(.*)",
                Pattern.DOTALL);
        Matcher m = serverEventPattern.matcher(this.data.trim());
        this.setAttribute("rawData",this.data.trim());
        if (m.matches()) {
            this.setAttribute("timestamp", m.group(2));
            String data =  m.group(3);
            String[] datas = data.split(" - ");
            this.setAttribute("component", datas[0]);
            this.setAttribute("content", data.replace(datas[0]+" - ", ""));
        }
    }

}
