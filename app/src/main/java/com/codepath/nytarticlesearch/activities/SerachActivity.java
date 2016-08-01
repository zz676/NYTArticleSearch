package com.codepath.nytarticlesearch.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.codepath.nytarticlesearch.Article;
import com.codepath.nytarticlesearch.ArticleArrayAdapter;
import com.codepath.nytarticlesearch.EndlessScrollListener;
import com.codepath.nytarticlesearch.Filters;
import com.codepath.nytarticlesearch.R;
import com.codepath.nytarticlesearch.Receivers.ConnectivityReceiver;
import com.codepath.nytarticlesearch.Receivers.NYTApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class SerachActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener, FilterFragment.FilterDialogListener {

    private final String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

    private GridView gvResults;
    private ArrayList<Article> articles;
    private ArticleArrayAdapter articleArrayAdapter;
    private CoordinatorLayout coordinatorLayout;

    private Map<String, String> parameters = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
        checkNetWorkManually();
    }

    private void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(articleArrayAdapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                intent.putExtra("theArticle", article);
                startActivity(intent);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });


    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serach, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                //gvResults.setAdapter(null);
                parameters.clear();
                parameters.put("q", searchView.getQuery().toString());
                fetchBooks(parameters);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatementandr
        if (id == R.id.action_filter) {
            FragmentManager fm = getSupportFragmentManager();
            FilterFragment filterFragment = FilterFragment.newInstance("Filter");
            filterFragment.show(fm, "fragment_filter");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private void fetchBooks(Map<String, String> parameters) {
        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("api-key", getResources().getString(R.string.api_key));
        //params.put("page", 0);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articlesResults = null;

                try {
                    articlesResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articlesResults));
                    articleArrayAdapter.clear();
                    articleArrayAdapter.addAll(Article.fromJSONArray(articlesResults));
                    articleArrayAdapter.notifyDataSetChanged();

                    //articleArrayAdapter.notifyDataSetChanged();
                    //Log.d("DEBUG", articles.toString());
                } catch (JSONException ex) {

                }
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });
    }

    // Method to manually check connection status
    private void checkNetWorkManually() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Not connected to internet";
            color = Color.RED;
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        NYTApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onFinishEditDialog(Filters filters) {

        if (!filters.getBeginDate().isEmpty())
            parameters.put("begin_date", filters.getBeginDate());
        else {
            parameters.remove("begin_date");
        }
        parameters.put("sort", filters.getSorter());

        if (!filters.getDesks().isEmpty())
            parameters.put("fq", "fq=news_desk:(" + filters.getDesks() + ")");
        else {
            parameters.remove("fq");
        }

        if (articles.size() == 0) {
            fetchBooks(parameters);
        } else {
            if(filters.getBeginDate().isEmpty() && filters.getDesks().isEmpty())
                fetchBooks(parameters);
            updatesArticles(parameters, filters);
        }

        Log.d("String", filters.getBeginDate());
    }

    private void updatesArticles(Map<String, String> parameters, Filters filters) {
        ArrayList<Article> newArticles = new ArrayList<>();
        for (Article a : articles) {
            if(!parameters.containsKey("begin_date")){
                if(!parameters.containsKey("fq")) {
                    newArticles.add(a);
                } else if (isIncluedInNewsDesk(a.getNewsDesk(), filters.getDesks()))
                {
                    newArticles.add(a);
                }
            } else {
                if(compareTwoDates(parameters.get("begin_date"), a.getPubDate())){
                    newArticles.add(a);
                } else {
                    if(!parameters.containsKey("fq")) {
                        newArticles.add(a);
                    } else if (isIncluedInNewsDesk(a.getNewsDesk(), filters.getDesks()))
                    {
                        newArticles.add(a);
                    }
                }
            }
        }
        articles.clear();
        articles.addAll(newArticles);
        articleArrayAdapter.clear();
        articleArrayAdapter.addAll(newArticles);
        articleArrayAdapter.notifyDataSetChanged();
    }


    private boolean compareTwoDates(String target, String compared) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            return sdf.parse(target).before(sdf.parse(compared));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isIncluedInNewsDesk(String targetdesk, String desks) {
        if(desks.isEmpty()) return true;

        String[] newsdesks = desks.split(" ");

        for(int i = 0; i < newsdesks.length; i++) {
            if(newsdesks[i].equals(targetdesk)) return true;
        }
        return false;
    }
}
