package com.example.cmpt_cobalt.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class MainActivity extends AppCompatActivity {

    private RestaurantManager manager;
    private int size = 0;
    private String []restaurantStrings = new String[size];
    //private Scanner in = new Scanner(System.in); //Read from keyboard

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        populateListView();
        registerClickCallback();
    }

    private void populateListView() {
        manager = RestaurantManager.getInstance();
        populateManager();
        size = manager.getManagerSize();
        restaurantStrings = new String[size];

        int i=0;
        for(Restaurant restaurant: manager) {
            restaurantStrings[i++] = restaurant.toString();
        }

        if(size==0) {
            restaurantStrings = new String[1];
            TextView textView = findViewById(R.id.textViewMain);
            textView.setText("");
            restaurantStrings[0] = "\n\n\n\nWelcome to the Restaurant Inspector!" +
                    "\n\nTo start, load a CSV file.\n\n";
        } else {
          TextView textView = findViewById(R.id.textViewMain);
          textView.setText(R.string.txt_select_a_restaurant);
        }

        // Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                this,           // Context for view
                R.layout.layout_listview,     // Layout to use
                restaurantStrings);               // Items to be displayed

        // Configure the list view
        ListView list = (ListView) findViewById(R.id.listViewMain);
        list.setAdapter(adapter);
    }

    private void populateManager() {
        // need to change the file path
        InputStream is1 = getResources().openRawResource(R.raw.restaurants_itr1);
        ParseCSV csv = new ParseCSV(is1);

        // start row index at 1 to ignore the titles
        for (int i = 1; i < csv.getRowSize(); i++) {
            Restaurant restaurant = new Restaurant(csv.getVal(i, 1),
                    csv.getVal(i, 2),
                    csv.getVal(i, 3),
                    Float.valueOf(csv.getVal(i, 5)),
                    Float.valueOf(csv.getVal(i, 6)),
                    csv.getVal(i, 0),
                    csv.getVal(i, 4));

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
                TextView textView = (TextView) view;
                String message = textView.getText().toString();

                Intent intent = RestaurantActivity.makeLaunchIntent(MainActivity.this, "RestaurantActivity");
                intent.putExtra("Extra", message);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
