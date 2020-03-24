package com.example.cmpt_cobalt.view;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cmpt_cobalt.model.CSVDowloader;
import com.example.cmpt_cobalt.model.FetchAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.example.cmpt_cobalt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.callback.Callback;

public class DownloadActivity extends AppCompatActivity {

    String rURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    String iURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    FetchAPI rFetch = new FetchAPI(rURL);

    String rDownlink;
    String iDownlink;

    private TextView textView;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        textView = findViewById(R.id.txt_download);

        mQueue = Volley.newRequestQueue(this);
        jsonParse(rURL, 0);
        jsonParse(iURL, 0);


    }

    private void jsonParse(String url, int num) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("result");
                            JSONArray jsonArray = object.getJSONArray("resources");
                            JSONObject urlObject = jsonArray.getJSONObject(1);
                            String output = urlObject.getString("url");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }


        /*

        //FetchAPI rFetch = new FetchAPI(rURL);
        //FetchAPI iFetch = new FetchAPI(iURL);

        //String rDownLink = rFetch.getUrl();
        TextView textView = findViewById(R.id.txt_download);
        textView.setText("rDownLink");
        String iDownLink = iFetch.getUrl();

        String rLastModified = rFetch.getLastModified().substring(0,19);
        String iLastModified = iFetch.getLastModified().substring(0,19);

        Date oldRDate = new Date(rLastModified);
        Date oldIDate = new Date(iLastModified);


        File rFile = new File("restaurants_itr1.csv");
        File iFile = new File("inspectionreports_itr1.csv");

        Date rDate = new Date(rFile.lastModified());
        Date iDate = new Date(iFile.lastModified());


        CSVDowloader rDownloader = new CSVDowloader(rDownLink, "restaurants_itr1.csv");
        CSVDowloader iDownloader = new CSVDowloader(iDownLink, "inspectionreports_itr1.csv");
         */

}

