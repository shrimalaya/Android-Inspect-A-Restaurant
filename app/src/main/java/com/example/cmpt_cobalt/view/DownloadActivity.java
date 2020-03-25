package com.example.cmpt_cobalt.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cmpt_cobalt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.callback.Callback;

public class DownloadActivity extends AppCompatActivity {

    String rURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    String iURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        FetchAPI rFetch = new FetchAPI(rURL);
        FetchAPI iFetch = new FetchAPI(iURL);

        String rDownLink = rFetch.getUrl();
        String iDownLink = iFetch.getUrl();

        TextView textView = findViewById(R.id.txt_download);

        String rLastModified = rFetch.getLastModified().substring(0, 19);
        String iLastModified = iFetch.getLastModified().substring(0, 19);

        new DownloadFileFromURL(DownloadActivity.this).execute(iDownLink,"restaurants_itr1.csv");
        new DownloadFileFromURL(DownloadActivity.this).execute(iDownLink,"inspectionreports_itr1.csv");

    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        Context context;
        private DownloadFileFromURL(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                URL url = new URL(f_url[0]);

                URLConnection connection = url.openConnection();
                connection.connect();
                int lOf = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 2048);

                File file = method(DownloadActivity.this, f_url[1]);

                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);

                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            Intent intent = new Intent(context,MainActivity.class);
            context.startActivity(intent);
            DownloadActivity.this.finish();
        }

    }

    static File method(Context obj, String filename){
        File myFile = new File (obj.getFilesDir(), filename );
        return myFile;
    }
}
