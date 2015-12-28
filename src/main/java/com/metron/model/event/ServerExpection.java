package com.metron.model.event;

import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;

import com.metron.model.ExceptionElement;
import com.metron.orientdb.OrientUtils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.metron.util.Utils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;

public class ServerExpection extends ServerEvent {

    private String[] exceptionElems = null;

    private ExceptionElement exceptionElement;

    public ServerExpection(String data) {
        super(data);
    }

    public void parse() {
        // TDBEXCEP :Store into the exception Object
        // TDBEXCEP : seperate the exception stacktrace into each line and store
        // as an exception element
        super.parse();
        this.exceptionElems = this.getStringAttr("content").split("\\sat\\s");
        this.setAttribute("heading", this.exceptionElems[0]);

    }

    private void associateTimeWindow() {
        // TODO Auto-generated method stub
        // ONE MIN Window
        DURATION duration = DURATION.ONEMIN;
        this.addEdge(this.getTimeWindow(duration), "Exception_" + duration.getTable());

        // FIVE MIN Window
        duration = DURATION.FIVEMIN;
        this.addEdge(this.getTimeWindow(duration), "Exception_" + duration.getTable());

        // ONE HOUR Window
        duration = DURATION.ONEHOUR;
        this.addEdge(this.getTimeWindow(duration), "Exception_" + duration.getTable());

        // ONEDAY Window
        duration = DURATION.ONEDAY;
        this.addEdge(this.getTimeWindow(duration), "Exception_" + duration.getTable());
    }

    public void process() {
        super.process();
        this.saveException();
        this.saveExceptionElements();
        this.associateRawEvent();
        this.associateTimeWindow();
        this.associateHost();
        // TDBEXCEP :Store into the exception Object

        // TDBEXCEP : seperate the exception stacktrace into each line and store
        // as an exception element

    }

    private void associateHost() {
        // Object[] props = new Object[]{
        // "timestamp",
        // OrientUtils.convertDatetoorientDbDate(Utils.parseEventDate(this.getStringAttr(
        // "timestamp")))};
        // this.addEdge(host, "Exception_Host", props);
        
        this.addEdge(host, "Exception_Host", "timestamp", OrientUtils
                .convertDatetoorientDbDate(Utils.parseEventDate(this.getStringAttr("timestamp"))));
    }

    private void associateRawEvent() {
        this.addEdge(rawEvent, "Exception_Event");
    }

    private void saveExceptionElements() {

        HashMap<String, Object> propsExceptionElement = null;
        for (String elem : this.exceptionElems) {
            exceptionElement = new ExceptionElement(StringEscapeUtils.escapeJavaScript(elem),
                    this.getGraph());
            propsExceptionElement = new HashMap<String, Object>();
            propsExceptionElement.put("value", elem);
            exceptionElement.setProperties(propsExceptionElement);
            exceptionElement.save();
            this.addEdge(exceptionElement, "Exception_ExceptionElement");
        }
    }

    private void saveException() {
        // TODO Auto-generated method stub
        OrientBaseGraph graph = this.getGraph();
        vertex = graph.addVertex("class:Exception");
        HashMap<String, Object> propsException = new HashMap<String, Object>();
        propsException.put("heading", this.getAttribute("heading"));
        propsException.put("content", this.getAttribute("content"));
        propsException.put("component", this.getAttribute("component"));
        propsException.put("timestamp", this.getAttribute("timestamp"));
        this.setProperties(propsException);
        this.save();
    }

}
