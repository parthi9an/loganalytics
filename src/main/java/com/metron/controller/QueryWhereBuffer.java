package com.metron.controller;

public class QueryWhereBuffer {

    StringBuffer buffer = null;

    public QueryWhereBuffer() {
        this.buffer = new StringBuffer();
    }

    public void append(String where, String operator) {
        this.buffer.append(((this.buffer.toString().equals("")) ? " " : " " + operator + " "));
        this.buffer.append(where);
    }

    public void append(String where) {
        this.append(where, "AND");
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.buffer.toString();
    }

}
