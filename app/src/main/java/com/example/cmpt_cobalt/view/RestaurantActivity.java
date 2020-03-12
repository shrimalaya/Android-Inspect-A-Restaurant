package com.example.cmpt_cobalt.view;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import com.example.cmpt_cobalt.model.Inspection;
import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmpt_cobalt.R;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    private RestaurantManager manager;
    private Restaurant restaurant;
    private int size = 0;
    private String []inspectionStrings = new String[size];
    private static final String EXTRA_MESSAGE = "Extra";
    private String restaurantString;    // Name of calling restaurant object
    private ArrayList<Inspection> inspectionList;

    List<Inspection> inspections = new ArrayList<>();


    public static Intent makeLaunchIntent(Context c, String message) {
        Intent intent = new Intent(c, RestaurantActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        populateInspectionList();
        registerClickCallback();
    }

    private void populateInspectionList() {
        manager = RestaurantManager.getInstance();
        // Process inspections
        processInspections();
        size = restaurant.getInspectionSize();
        inspectionStrings = new String[size];

        // Start populating string
        int i=0;
        for(Inspection inspection: inspectionList) {
            //inspectionStrings[i++] = inspection.toString();
            inspections.add(inspection);
        }

        // Build Adapter
        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                this,           // Context for view
                R.layout.layout_listview,     // Layout to use
                inspectionStrings);               // Items to be displayed

        // Configure the list view
        ListView list = (ListView) findViewById(R.id.restaurant_view);
        list.setAdapter(adapter);
        */

        ArrayAdapter<Inspection> adapter = new CustomAdapter();
        ListView list = (ListView) findViewById(R.id.restaurant_view);
        list.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<Inspection> {
        public CustomAdapter() {
            super(RestaurantActivity.this, R.layout.layout_inspection, inspections);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.layout_inspection, parent, false);
            }

            Inspection currentInspection = inspections.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.inspectionimage);
            imageView.setImageResource(currentInspection.getHazardIcon());

            TextView textView = (TextView) itemView.findViewById(R.id.inspectiontext);
            textView.setText(currentInspection.toString());

            return itemView;
        }

    }

    
    private void processInspections() {
        // Receive message from MainActivity
        // Message contains details of selected Restaurant
        Intent intent2 = getIntent();
        restaurantString = intent2.getStringExtra(EXTRA_MESSAGE);

        // Find the Restaurant and assign it to our local Restaurant object
        for (Restaurant temp: manager) {
            if(temp.toString().equals(restaurantString)) {
                restaurant = temp;
            }
        }

        // Populate the list of inspections for the selected restaurant
        inspectionList = restaurant.getInspections();
        TextView name = findViewById(R.id.name_resActivity);
        name.setText(restaurant.getName());

        TextView address = findViewById(R.id.address_resActivity);
        address.setText(restaurant.getStreetAddress());

        TextView lat = findViewById(R.id.latitude_resActivity);
        lat.setText(restaurant.getLatAddress() + "");

        TextView lon = findViewById(R.id.longitude_resActivity);
        lon.setText(restaurant.getLongAddress() + "");
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.restaurant_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View itemView = view;
                TextView textView = view.findViewById(R.id.inspectiontext);
                String message = textView.getText().toString();

                //TextView textView = (TextView) view;
                //String message = view.getText().toString();
                String restaurantTracking = restaurant.getTracking();

                Intent intent = InspectionActivity.makeLaunchIntent(RestaurantActivity.this, "InspectionActivity");
                intent.putExtra("Extra", message);
                intent.putExtra("Restaurant", restaurantTracking);
                startActivity(intent);
            }
        });
    }
}
