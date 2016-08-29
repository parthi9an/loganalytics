package com.metron.model;

public class EdgeDef {
	
	public String name;
	public String in, out, fromKey, toKey;
	
	public EdgeDef(String name, String in, String out, String fromKey, String toKey) {
		this.name = name;
		this.in = in;
		this.out = out;
		this.fromKey = fromKey;
		this.toKey = toKey;
	}


}
