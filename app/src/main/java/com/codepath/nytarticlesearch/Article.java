package com.codepath.nytarticlesearch;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam on 7/25/16.
 */
public class Article implements Parcelable {
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

    public String getNewsDesk() {
        return newsDesk;
    }

    public String getPubDate() {
        return pubDate;
    }

    private String headLine;
    private String thumbNail;
    private String pubDate;
    private String newsDesk;

    public Article() {
    }

    // Returns a Article given the expected JSON
    public static Article fromJson(JSONObject jsonObject) {
        Article article = new Article();
        try {
            article.webUrl = jsonObject.getString("web_url");
            article.headLine = jsonObject.getJSONObject("headline").getString("main");
            article.pubDate = jsonObject.getString("pub_date").substring(0, 10).replace("-", "");
            article.newsDesk = jsonObject.getString("news_desk");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediajson = multimedia.getJSONObject(0);
                article.thumbNail = "http://www.nytimes.com/" + multimediajson.getString("url");
            } else {
                article.thumbNail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return article;
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(Article.fromJson(array.getJSONObject(x)));
            } catch (JSONException ex) {

            }
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webUrl);
        dest.writeString(this.headLine);
        dest.writeString(this.thumbNail);
    }

    protected Article(Parcel in) {
        this.webUrl = in.readString();
        this.headLine = in.readString();
        this.thumbNail = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}