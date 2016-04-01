package com.metron.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

public class WSClient {

	public HttpResponseData get(String url) {
		return this.get(url, null, null);
	}

	public HttpResponseData get(String url, String username, String password) {
		System.out.println("URL :"+url);
		StringBuffer result = new StringBuffer();
		Header[] headers = null;
		try {
			HttpClient client = new DefaultHttpClient();
			url = url.replace(" ", "%20");
			HttpGet request = new HttpGet(url);
			if (username != null && password != null)
				request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(
						username, password), "UTF-8", false));

			HttpResponse response;

			response = client.execute(request);
			headers = response.getAllHeaders();
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			result = new StringBuffer();
			String line = "";

			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println(" ---- URL " + url);
		HttpResponseData responseData = new HttpResponseData(result.toString(), headers);
		return responseData;

	}

	public HttpResponseData post(String url, String username, String password) throws ClientProtocolException, IOException {
	    
        System.out.println("URL :" + url);
        StringBuffer result = new StringBuffer();
        Header[] headers = null;
        int responsecode = 0;

        HttpClient client = new DefaultHttpClient();
        url = url.replace(" ", "%20");
        HttpPost request = new HttpPost(url);
        if (username != null && password != null)
            // request.setHeader("Content-Type","application/x-www-form-urlencoded");
            request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,
                    password), "application/x-www-form-urlencoded", false));

        HttpResponse response;

        response = client.execute(request);
        headers = response.getAllHeaders();
        responsecode = response.getStatusLine().getStatusCode();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity()
                .getContent()));

        result = new StringBuffer();
        String line = "";

        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        // System.out.println(" ---- URL " + url);
        HttpResponseData responseData = new HttpResponseData(result.toString(), headers,
                responsecode);
        return responseData;

    }
	
	public String post(String url, List<NameValuePair> urlParameters) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		System.out.println("--- URL :" + url);
		StringBuffer result = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(post);
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}

}
