package com.example.cmpt_cobalt.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.Inspection;
import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;

import java.util.ArrayList;
import java.util.List;

// displays the list of all inspections
// pertaining to a single restaurant
public class RestaurantActivity extends AppCompatActivity {

    private RestaurantManager manager;
    private Restaurant restaurant;
    private int size = 0;
    private String[] inspectionStrings = new String[size];
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateInspectionList();
        registerClickCallback();
        setupDefaultIntent();
    }

    private void setupDefaultIntent() {
        Intent i = new Intent();
        i.putExtra("result", 0);
        setResult(Activity.RESULT_OK, i);
    }

    private void goToMapsActivity() {
        Intent i = new Intent();
        i.putExtra("result", 1);
        i.putExtra("resID", restaurant.getTracking());
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    private void populateInspectionList() {

        manager = RestaurantManager.getInstance();

        processInspections();
        size = restaurant.getInspectionSize();
        inspectionStrings = new String[size];

        // Start populating string
        for (Inspection inspection : inspectionList) {
            inspections.add(inspection);
        }

        ArrayAdapter<Inspection> adapter = new CustomAdapter();
        ListView list = (ListView) findViewById(R.id.restaurant_view);
        list.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<Inspection> {
        public CustomAdapter() {
            super(RestaurantActivity.this, R.layout.layout_inspection, inspections);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

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
        for (Restaurant temp : manager) {
            if (temp.toString().equals(restaurantString)) {
                restaurant = temp;
            }
        }

        // Populate the list of inspections for the selected restaurant
        inspectionList = restaurant.getInspections();
        TextView name = findViewById(R.id.name_resActivity);
        name.setText(restaurant.getName());

        TextView address = findViewById(R.id.address_resActivity);
        address.setText(restaurant.getStreetAddress());

        TextView latLng = findViewById(R.id.latLng_resActivity);
        latLng.setText((float) restaurant.getLatAddress() + ", " + (float) restaurant.getLongAddress());

    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.restaurant_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.inspectiontext);
                String message = textView.getText().toString();

                String restaurantTracking = restaurant.getTracking();

                Intent intent = InspectionActivity.makeLaunchIntent(RestaurantActivity.this, "InspectionActivity");
                intent.putExtra("Extra", message);
                intent.putExtra("Restaurant", restaurantTracking);
                startActivity(intent);
            }
        });

        TextView latLng = findViewById(R.id.latLng_resActivity);
        latLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case (R.id.restaurant_map_icon):
                goToMapsActivity();
                return true;
            case (android.R.id.home):
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
