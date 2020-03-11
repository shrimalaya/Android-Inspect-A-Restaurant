package com.example.cmpt_cobalt.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.cmpt_cobalt.model.Inspection;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;

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

        getInspection();
        displayDetails();
    }

    private void displayDetails() {
        TextView trackingNumberText= findViewById(R.id.trackingNumber);
        TextView inspectionDateText= findViewById(R.id.inspectionDate);
        TextView inspectionTypeText= findViewById(R.id.inspectionType);
        TextView numCriticalText= findViewById(R.id.numCritical);
        TextView numNonCriticalText= findViewById(R.id.numNonCritical);
        TextView hazardRatingText= findViewById(R.id.hazardRating);
        violationListView();

        trackingNumberText.setText(this.mInspection.getTrackingNumber());
        inspectionDateText.setText(this.mInspection.getInspectionDate());
        inspectionTypeText.setText(this.mInspection.getInspectionType());
        numCriticalText.setText(this.mInspection.getNumCritical());
        numNonCriticalText.setText(this.mInspection.getNumNonCritical());
        hazardRatingText.setText(this.mInspection.getHazardRating());

    }

    //TODO: Need more details on how to receive an inspection instance from the other activity.
    private void getInspection() {
        RestaurantManager manager = RestaurantManager.getInstance();
        Intent i = getIntent();
        String messageRestaurant = i.getStringExtra(RESTAURANT_MESSAGE);
        String message = i.getStringExtra(EXTRA_MESSAGE);

        for(Restaurant temp: manager) {
            if(messageRestaurant.equals(restaurant.getTracking())) {
                restaurant = temp;
            }
        }

        for(Inspection temp: restaurant.inspections) {
            if(temp.toString().equals(message)) {
                mInspection = temp;
            }
        }

        /*Inspection inspection = new Inspection(
                "SDFO-8HKP7E",
                "20191002",
                "Routine",
                0,
                0,
                "Low",
                "205,Critical,Cold potentially hazardous food stored/displayed above 4 Â°C. [s. 14(2)],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat|304,Not Critical,Premises not free of pests [s. 26(a)],Not Repeat|305,Not Critical,Conditions observed that may allow entrance/harbouring/breeding of pests [s. 26(b)(c)],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat|401,Critical,Adequate handwashing stations not available for employees [s. 21(4)],Not Repeat"
        );
        this.mInspection = inspection;
         */
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
