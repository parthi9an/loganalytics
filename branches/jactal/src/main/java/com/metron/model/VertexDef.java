package com.metron.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VertexDef {
	
	public String name, keyFieldName;
	public Map<String, EdgeDef> ins, outs;
	
	public VertexDef(String name, String keyFieldName) {
		this.ins = new HashMap<String, EdgeDef>();
		this.outs = new HashMap<String, EdgeDef>();
		
		this.name = name;
		this.keyFieldName = keyFieldName;
	}

	public void addOut(EdgeDef edgeDef) {
		
		this.outs.put(edgeDef.name, edgeDef);
		
	}

	public void addIn(EdgeDef edgeDef) {
		this.ins.put(edgeDef.name, edgeDef);
		
	}

	public Collection<EdgeDef> getInEdges() {
		return this.ins.values();
	}

	public Collection<EdgeDef> getOutEdges() {
		return this.outs.values();
	}

}
