/**
 * Rest API client 
 * 
 * Reference: http://lukencode.com/2010/04/27/calling-web-services-in-android-using-httpclient/
 * 
 */

package com.sachin.core.api.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;



public class RestClient {
	
    // Holds params to pass to the Rest URL
    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;

    private String url;

    private int responseCode;
    private String message;

    private String response;
    
    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url)
    {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void addParam(String name, String value) 
    {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) 
    {
        headers.add(new BasicNameValuePair(name, value));
    }

    /**
     * Heart of the RestClient. The method sends the data to the set
     * URL and gets the response
     *
     * 
     * @param RequestMethod method 
     * @throws Exception
     */
    public void execute(RequestMethod method) throws Exception
    {
        switch(method) {
            case GET: {
                //add parameters
                String combinedParams = "";
                if(!params.isEmpty()){
                    combinedParams += "?";
                    for(NameValuePair p : params)
                    {
                        String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                        if(combinedParams.length() > 1)
                        {
                            combinedParams  +=  "&" + paramString;
                        }
                        else
                        {
                            combinedParams += paramString;
                        }
                    }
                }
                //Log.i("RestClient", url + combinedParams) ;
                HttpGet request = new HttpGet(url + combinedParams);

                //add headers
                for(NameValuePair h : headers) {
                    request.addHeader(h.getName(), h.getValue());
                }
                executeRequest(request, url);
                break;
            }

            case POST: {
                HttpPost request = new HttpPost(url);

                //add headers
                for(NameValuePair h : headers) {
                    request.addHeader(h.getName(), h.getValue());
                }
                if(!params.isEmpty()) {
                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                }
                executeRequest(request, url);
                break;
            }
        }
    }

    private void executeRequest(HttpUriRequest request, String url) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse httpResponse;

        try {
        	//Log.i("RestClient executeRequest", url) ;
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
            }
        } 
        catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } 
        catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                is.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
