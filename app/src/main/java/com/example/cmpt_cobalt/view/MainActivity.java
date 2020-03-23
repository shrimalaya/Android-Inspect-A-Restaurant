package com.example.cmpt_cobalt.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.Inspection;
import com.example.cmpt_cobalt.model.ParseCSV;
import com.example.cmpt_cobalt.model.Restaurant;
import com.example.cmpt_cobalt.model.RestaurantManager;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;

// main screen activity
// displays the initial list of restaurants
public class MainActivity extends AppCompatActivity {

    private RestaurantManager manager;
    private int size = 0;
    private String []restaurantStrings = new String[size];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        populateListView();
        registerClickCallback();
        setupMapsActivityButton();
    }

    private void setupMapsActivityButton() {
        Button button = (Button) findViewById(R.id.buttonMain);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void populateListView() {
        manager = RestaurantManager.getInstance();
        populateManager();


        if (size == 0) {

            restaurantStrings = new String[1];
            restaurantStrings[0] = "\n\n\n\nWelcome to the Restaurant Inspector!" +
                    "\n\nTo start, load a CSV file.\n\n";

        } else {

          TextView textView = findViewById(R.id.textViewMain);
          textView.setText(R.string.txt_select_a_restaurant);

        }


        ArrayAdapter<Restaurant> adapter = new RestaurantAdapter();
        ListView restaurantList = findViewById(R.id.listViewMain);
        restaurantList.setAdapter(adapter);
    }

    private class RestaurantAdapter extends ArrayAdapter<Restaurant> {

        public RestaurantAdapter() {
            super(MainActivity.this, R.layout.restaurant_item, manager.getRestaurants());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_item, parent, false);
            }

            // Find the restaurant to work with.
            Restaurant currentRestaurant = manager.getRestaurants().get(position);

            // Fill the view
            ImageView logo = itemView.findViewById(R.id.item_restaurantLogo);
            logo.setImageResource(currentRestaurant.getIcon());

            TextView restaurantNameText = itemView.findViewById(R.id.item_restaurantName);
            restaurantNameText.setText(currentRestaurant.getName());


            Inspection mostRecentInspection = currentRestaurant.getInspection(0);
            if (mostRecentInspection != null) {
                TextView numNonCriticalText = itemView.findViewById(R.id.item_numNonCritical);
                numNonCriticalText.setText(Integer.toString(mostRecentInspection.getNumNonCritical()));

                TextView numCriticalText = itemView.findViewById(R.id.item_numCritical);
                numCriticalText.setText(Integer.toString(mostRecentInspection.getNumCritical()));

                TextView lastInspectionText = itemView.findViewById(R.id.item_lastInspection);
                lastInspectionText.setText(mostRecentInspection.getFormattedDate());

                ImageView hazard = itemView.findViewById(R.id.item_hazardImage);
                hazard.setImageResource(mostRecentInspection.getHazardIcon());

            }


            return itemView;
        }

    }

    private void populateManager() {
        InputStream is1 = getResources().openRawResource(R.raw.restaurants_itr1);
        ParseCSV csv = new ParseCSV(is1);

        // start row index at 1 to ignore the titles
        for (int row = 1; row < csv.getRowSize(); row++) {
            Restaurant restaurant = new Restaurant(
                    csv.getVal(row, 1),
                    csv.getVal(row, 2),
                    csv.getVal(row, 3),
                    Float.valueOf(csv.getVal(row, 5)),
                    Float.valueOf(csv.getVal(row, 6)),
                    csv.getVal(row, 0));

            populateWithInspections(restaurant);


            manager.add(restaurant);
        }

        Collections.sort(manager.getRestaurants(), new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

    }

    private void populateWithInspections(Restaurant restaurant) {
        InputStream is2 = getResources().openRawResource(R.raw.inspectionreports_itr1);
        ParseCSV csv2 = new ParseCSV(is2);
        String viol = "";

        // start at 1 to skip titles
        for (int row = 1; row < csv2.getRowSize(); row++) {
            if (csv2.getVal(row, 0).equals(restaurant.getTracking())) {

                // check if there are more than one violation
                // if so, then append them all together in one string to be
                // parsed later
                if (csv2.getColSize(row) > 7) {
                    for (int col = 6; col < csv2.getColSize(row); col++) {
                        viol += csv2.getVal(row, col) + " ";
                    }

                    Inspection inspect = new Inspection(
                            csv2.getVal(row, 0),
                            csv2.getVal(row, 1),
                            csv2.getVal(row, 2),
                            Integer.valueOf(csv2.getVal(row, 3)),
                            Integer.valueOf(csv2.getVal(row, 4)),
                            csv2.getVal(row, 5),
                            viol);
                    restaurant.inspections.add(inspect);
                    viol = "";


                } else {
                    Inspection inspect = new Inspection(
                            csv2.getVal(row, 0),
                            csv2.getVal(row, 1),
                            csv2.getVal(row, 2),
                            Integer.valueOf(csv2.getVal(row, 3)),
                            Integer.valueOf(csv2.getVal(row, 4)),
                            csv2.getVal(row, 5),
                            csv2.getVal(row, 6));
                    restaurant.inspections.add(inspect);
                }


            }
        }

        Collections.sort(restaurant.inspections, new Comparator<Inspection>() {
            @Override
            public int compare(Inspection o1, Inspection o2) {
                return o2.getInspectionDate().compareTo(o1.getInspectionDate());
            }
        });
    }

    /**
     * Calback register for RestaurantActivity
     */
    private void registerClickCallback() {
        ListView list = findViewById(R.id.listViewMain);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String message = manager.getRestaurants().get(position).toString();

                Intent intent = RestaurantActivity.makeLaunchIntent(MainActivity.this, "RestaurantActivity");
                intent.putExtra("Extra", message);
                MainActivity.this.startActivity(intent);
            }
        });
    }


}
