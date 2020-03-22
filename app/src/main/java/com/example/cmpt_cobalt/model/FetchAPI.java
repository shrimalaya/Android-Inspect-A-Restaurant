package com.example.cmpt_cobalt.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FetchAPI {
    private JsonParser parser;
    private HTTPRequest request;
    private String lastModified;
    private String url;
    private String format;

    public FetchAPI(String sourceUrl){
        parser = new JsonParser();
        this.request = new HTTPRequest(sourceUrl);
        fetchData();
    }

    public void fetchData(){
        String content = this.request.getRequest().toString();
        JsonObject json = (JsonObject) this.parser.parse(content);
        json = (JsonObject) json.get("result");
        JsonArray resources = (JsonArray) json.get("resources");
        json = (JsonObject) resources.get(0);

        this.lastModified = json.get("last_modified").getAsString();
        this.url = json.get("url").getAsString();
        this.format = json.get("format").getAsString();
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getUrl() {
        return url;
    }

    public String getFormat() {
        return format;
    }


}
