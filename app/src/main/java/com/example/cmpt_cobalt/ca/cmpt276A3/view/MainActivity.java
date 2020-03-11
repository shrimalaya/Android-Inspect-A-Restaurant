package com.example.cmpt_cobalt.ca.cmpt276A3.view;

import android.content.Intent;
import android.os.Bundle;

import com.example.cmpt_cobalt.ca.cmpt276A3.model.Restaurant;
import com.example.cmpt_cobalt.ca.cmpt276A3.model.RestaurantManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmpt_cobalt.R;

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
