package com.example.cmpt_cobalt.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.cmpt_cobalt.model.Inspection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmpt_cobalt.R;

import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InspectionActivity extends AppCompatActivity {

    private static final String EXTRA_MESSAGE = "Extra";
    private static final String RESTAURANT_MESSAGE = "Restaurant";

    public static Intent makeLaunchIntent(Context c, String message) {
        Intent intent = new Intent(c, InspectionActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    private Inspection mInspection;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getInspection();
        displayDetails();
    }

    private void displayDetails(){
        violationListView();

        TextView trackingNumberText= findViewById(R.id.trackingNumber);
        TextView inspectionDateText= findViewById(R.id.inspectionDate);
        TextView inspectionTypeText= findViewById(R.id.inspectionType);
        TextView numCriticalText= findViewById(R.id.numCritical);
        TextView numNonCriticalText= findViewById(R.id.numNonCritical);
        TextView hazardRatingText= findViewById(R.id.hazardRating);

        trackingNumberText.setText(mInspection.getTrackingNumber());
        inspectionDateText.setText(getFormatDate());
        inspectionTypeText.setText(mInspection.getInspectionType());
        numCriticalText.setText(Integer.toString(mInspection.getNumCritical()));
        numNonCriticalText.setText(Integer.toString(mInspection.getNumNonCritical()));
        hazardRatingText.setText(mInspection.getHazardRating());

    }

    // Return a date formated in "May 12, 2019"
    private String getFormatDate(){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            Date inspectionDate = sdf.parse(mInspection.getInspectionDate());
            Calendar inspectionCalendar = Calendar.getInstance();
            inspectionCalendar.setTime(inspectionDate);
            String[] indexToMonth = new DateFormatSymbols().getMonths();
            return indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                    + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH)
                    + ", " + inspectionCalendar.get(Calendar.YEAR);
        }
        catch (Exception e) {
            // Handle it.
        }
        return "N/A";
    }

    private void getInspection() {
        RestaurantManager manager = RestaurantManager.getInstance();
        Intent i = getIntent();
        String messageRestaurant = i.getStringExtra(RESTAURANT_MESSAGE);
        String message = i.getStringExtra(EXTRA_MESSAGE);

        for(Restaurant temp: manager) {
            if(messageRestaurant.equals(temp.getTracking())) {
                restaurant = temp;
            }
        }

        for(Inspection temp: restaurant.inspections) {
            if(temp.toString().equals(message)) {
                mInspection = temp;
            }
        }
    }

    private void violationListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,            // Context for the activity.
                R.layout.violation_item,      // Layout to use.
                this.mInspection.getViolations()
        );

        ListView violationsList = findViewById(R.id.violationsList);
        violationsList.setAdapter(adapter);
    }


}
