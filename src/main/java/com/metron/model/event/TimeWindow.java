package com.metron.model.event;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.metron.util.TimeWindowUtil.DURATION;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class TimeWindow extends BaseModel{
    
    public TimeWindow(Date date, DURATION duration, OrientBaseGraph graph) {
        
        this.vertex = find(date, duration, graph);
        
    }
    
    public OrientVertex find(Date date, DURATION duration, OrientBaseGraph graph) {
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        System.out.println("Calender - Time in milliseconds : " + calendar.getTimeInMillis());
        long timeinSec = calendar.getTimeInMillis() / 1000;
        long timeFrameInSec = (duration.getMinutes() * 60);

        // get the timebucket which belogs to
        long timeBucket = timeinSec / timeFrameInSec;
        System.out.println("BUCKET: " + timeBucket);

        long startTimeStamp = timeBucket * (duration.getMinutes() * 60);

        long EndTimeStamp = startTimeStamp + (duration.getMinutes() * 60);

        System.out.println("START TS " + startTimeStamp + " END TS " + EndTimeStamp);

        String query = "select * from " + duration.getTable() + " where startTime = "
                + startTimeStamp + " AND endTime = " + EndTimeStamp;
        
        vertex =  OrientUtils.getVertex(graph, query);
        if (vertex == null) {
            vertex = graph.addVertex("class:" + duration.getTable());
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("startTime", startTimeStamp);
            props.put("endTime", EndTimeStamp);
            vertex.setProperties(props);
            vertex.save();
        }
        return vertex;
    }
    
}
