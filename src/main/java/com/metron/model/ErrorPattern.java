package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class ErrorPattern extends BaseModel{
    
    public ErrorPattern(String pattern,String errorType,OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph,pattern,errorType);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:ErrorPattern");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("pattern_type", pattern);
            props.put("error_type",errorType);
            props.put("association_count", 1);
            this.setProperties(props);
            this.save();
        }else{
            int count = this.vertex.getProperty("association_count");
            count++;
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("association_count", count);
            this.setProperties(props);
            this.save();
        }
    }

    /**
     * Find the pattern vertex matches patternType
     * @param graph
     * @param patternType
     * @return
     */
    public OrientVertex find(OrientBaseGraph graph, String patternType,String errorType) {
        OrientVertex pattern = OrientUtils.getVertex(graph,
                "select *  from ErrorPattern where pattern_type = '" + patternType
                + "' and error_type='" + errorType  + "'");
        
        return pattern;
        
    }

}
