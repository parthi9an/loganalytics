package com.metron.model;

public class EdgeKey {
    private String name = "";
    private String key = "";

    public EdgeKey(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "[" + this.name + ":" + this.key + "]";
    }
}
