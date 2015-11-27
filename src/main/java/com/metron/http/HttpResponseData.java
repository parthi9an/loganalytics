package com.metron.http;

import org.apache.http.Header;

public class HttpResponseData {
	private String result ;
	private Header[] headers;


	
	
	public HttpResponseData(String string,Header[] headers){
		this.result = string;
		this.headers = headers;
	}
	
	@Override
	public  String toString(){
		return result.toString();
	}

	
	public String getHeaderValue(String headerName){
		if(this.headers.length > 0){
			for(int i=0;i<this.headers.length;i++){
				if(this.headers[i].getName().contentEquals(headerName)){
					return this.headers[i].getValue();
				}
			}
		}
		return "";
	}
	
	public String getHeaders(){
		String headersString = "";
		if(this.headers.length > 0){
			for(int i=0;i<this.headers.length;i++){
				headersString += this.headers[i].getName()+":"+this.headers[i].getValue()+"\n";
				
			}
		}
		return headersString;
	}
}
