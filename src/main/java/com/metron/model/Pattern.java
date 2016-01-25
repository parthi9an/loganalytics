package com.metron.model;

import java.util.HashMap;

import com.metron.orientdb.OrientUtils;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * Creates a vertex pattern if doesn't exist
 * @author Metron
 *
 */
public class Pattern extends BaseModel {
    
    public Pattern(String pattern,OrientBaseGraph graph) {
        super(graph);
        this.vertex = find(graph,pattern);
        if (vertex == null) {
            this.vertex = graph.addVertex("class:Pattern");
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("pattern_type", pattern);
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
    public OrientVertex find(OrientBaseGraph graph, String patternType) {
        OrientVertex pattern = OrientUtils.getVertex(graph,
                "select *  from Pattern where pattern_type = '" + patternType
                        + "'");
        return pattern;
    }

}
