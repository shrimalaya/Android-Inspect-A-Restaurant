package com.example.cmpt_cobalt.view;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.cmpt_cobalt.R;

public class DownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String rURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants\n";
        String iURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports\n";

        FetchAPI rFetch = newFetchAPI(rURL);
        FetchAPI iFetch = newFetchAPI(iURL);

        String rDownLink = rFetch.getUrl();
        String iDownLink = iFetch.getUrl();

        

        }
    }

}
