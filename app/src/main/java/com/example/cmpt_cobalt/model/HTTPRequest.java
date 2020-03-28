package com.example.cmpt_cobalt.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {

    private java.net.URL URL;
    private final int timeOutLimitInMS = 5000;

    public HTTPRequest(String URL) {
        try {
            this.URL = new URL(URL);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }


    /*
      Fetches the content from the HTTP request.
      @return content in StringBuffer (Can be converted into JSON)

     */
    public StringBuffer getRequest() {
        try {
            HttpURLConnection connection = (HttpURLConnection) this.URL.openConnection();
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(timeOutLimitInMS);
            connection.setReadTimeout(timeOutLimitInMS);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            String inputLine;
            StringBuffer content = new StringBuffer();

            while((inputLine = reader.readLine()) != null){
                content.append(inputLine);
            }

            reader.close();
            return content;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
