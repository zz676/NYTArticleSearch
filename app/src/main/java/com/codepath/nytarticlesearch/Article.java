package com.codepath.nytarticlesearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sam on 7/25/16.
 */
public class Article implements Serializable{
    private String webUrl;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    private String headLine;
    private String thumbNail;

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediajson = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediajson.getString("url");
            } else {
                this.thumbNail = "";
            }

        } catch (JSONException ex) {

        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new Article(array.getJSONObject(x)));
            } catch (JSONException ex) {

            }
        }

        return results;
    }
}