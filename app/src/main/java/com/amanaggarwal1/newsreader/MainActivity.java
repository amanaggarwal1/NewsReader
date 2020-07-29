package com.amanaggarwal1.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {

    private List<String> titles = new ArrayList<>();
    private List<String> urls = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private ListView titlesLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titlesLV = findViewById(R.id.titlesLV);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,  titles );
        titlesLV.setAdapter(arrayAdapter);
        fetchNews();

        titlesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), NewsWebActivity.class);
                intent.putExtra("url", urls.get(i));
                startActivity(intent);
            }
        });

    }

    private void fetchNews(){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            fetchTitles(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchTitles(JSONArray jsonArray) {
       int listSize = min(40, jsonArray.length());

       for(int i = 0; i < listSize; i++){
           try {
               updateList(jsonArray.getString(i));
           }catch (JSONException e){
               e.printStackTrace();
           }
       }
    }

    private void updateList(String id) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String webLink = "https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, webLink, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            titles.add(response.getString("title"));
                            urls.add(response.getString("url"));
                            while(titles.size() != urls.size()){
                                if (titles.size() > urls.size())
                                    urls.add("https://www.google.com");
                                else
                                    titles.add("https://www.google.com");
                        }
                           arrayAdapter.notifyDataSetChanged();

                           Log.i("LOGCAT", response.getString("title"));
                           Log.i("LOGCAT",response.getString("url"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Couldn't fetch latest news", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }
}