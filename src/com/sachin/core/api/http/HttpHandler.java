/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.api.http;

import com.sachin.core.interfaces.IDataSource;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 *
 * @author sachin
 */
public class HttpHandler implements Serializable, IDataSource {
    private String _URL ;

    public HttpHandler(String url) {
        _URL = url ;
    }

    public String pullData(String command, String[] args, int page, int totalRecords) {
        HttpClient httpclient = new DefaultHttpClient();
        String responseBody = "" ;
        try {
            HttpGet httpget = new HttpGet(_URL);

            System.out.println("executing request " + httpget.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");

        }
        catch (IOException ex) {
            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return responseBody ;
    }

    public String pullAds() {
        return "" ;
    }
}
