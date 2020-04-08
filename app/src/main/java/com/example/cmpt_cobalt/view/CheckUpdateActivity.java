package com.example.cmpt_cobalt.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckUpdateActivity extends AppCompatActivity {

    List<Restaurant> updatedRestaurants = new ArrayList<>();
    private String []restaurantStrings = new String[0];
    private RestaurantManager manager;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_update);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        compareForUpdate();
        registerClickCallback();
    }

    private void compareForUpdate() {
        // To decide whether to hold on before launching MapsActivity
        manager = RestaurantManager.getInstance();
        mSharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        Set<String> favourites = new HashSet<String>(mSharedPreferences.getStringSet("Favourites", new HashSet<String>()));

        for(String OldJson: favourites) {
            for(Restaurant newRes: manager) {
                Gson gson = new Gson();
                Restaurant oldRes = gson.fromJson(OldJson, Restaurant.class);

                String newJson = new Gson().toJson(newRes);
                if (oldRes.getTracking().equals(newRes.getTracking())) {
                    if(!OldJson.equals(newJson)) {
                        updatedRestaurants.add(newRes);
                        favourites.remove(OldJson);
                        favourites.add(newJson);
                    }
                }
            }
        }

        mSharedPreferences.edit().putStringSet("Favourites", favourites).apply();

        populate();
    }

    private void populate() {
        manager = RestaurantManager.getInstance();


        if (updatedRestaurants.isEmpty()) {

            restaurantStrings = new String[1];
            restaurantStrings[0] = getResources().getString(R.string.greeting_check_update);

        } else {

            TextView textView = findViewById(R.id.textViewUpdate);
            textView.setText(R.string.txt_select_a_restaurant);
            ArrayAdapter<Restaurant> adapter = new CheckUpdateActivity.RestaurantAdapter();
            ListView restaurantList = findViewById(R.id.listViewUpdate);
            restaurantList.setAdapter(adapter);
        }

    }

    private class RestaurantAdapter extends ArrayAdapter<Restaurant> {

        public RestaurantAdapter() {
            super(CheckUpdateActivity.this, R.layout.restaurant_item, updatedRestaurants);
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

            //Favorites view
            final ImageView favourite = itemView.findViewById(R.id.item_favourite);
            favourite.setImageResource(currentRestaurant.getFavouriteImage());
            favourite.setTag(position);
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Restaurant currentRestaurant = manager.getRestaurants().get((Integer) v.getTag());
                    if(currentRestaurant.getFavourite())
                    {
                        currentRestaurant.setFavourite(false);
                        favourite.setImageResource(currentRestaurant.getFavouriteImage());
                        System.out.println("DD> " + currentRestaurant.getName() + "set to false\n");
                        removeFromFavourites(currentRestaurant);
                    }
                    else if(!currentRestaurant.getFavourite())
                    {
                        currentRestaurant.setFavourite(true);
                        favourite.setImageResource(currentRestaurant.getFavouriteImage());
                        System.out.println("DD> " + currentRestaurant.getName() + "set to true\n");
                        saveToFavourites(currentRestaurant);
                    }
                }
            });

            TextView restaurantNameText = itemView.findViewById(R.id.item_restaurantName);
            String temp = currentRestaurant.getName();
            if(temp.length() > 30) {
                restaurantNameText.setText(temp.substring(0, 30) + "...");
            } else {
                restaurantNameText.setText(temp);
            }


            Inspection mostRecentInspection = currentRestaurant.getInspection(0);
            if (mostRecentInspection != null) {
                TextView numNonCriticalText = itemView.findViewById(R.id.item_numNonCritical);
                numNonCriticalText.setText(Integer.toString(mostRecentInspection.getNumNonCritical()));

                TextView numCriticalText = itemView.findViewById(R.id.item_numCritical);
                numCriticalText.setText(Integer.toString(mostRecentInspection.getNumCritical()));

                TextView lastInspectionText = itemView.findViewById(R.id.item_lastInspection);
                lastInspectionText.setText(mostRecentInspection.getFormattedDate());

                ImageView hazard = itemView.findViewById(R.id.item_hazard);
                hazard.setImageResource(mostRecentInspection.getHazardIcon());

            }
            return itemView;
        }

    }

    // Learned from: https://medium.com/@anupamchugh/a-nightmare-with-shared-preferences-and-stringset-c53f39f1ef52
    private void removeFromFavourites(Restaurant currentRestaurant) {
        mSharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        Set<String> favourites = new HashSet<String>(mSharedPreferences.getStringSet("Favourites", new HashSet<String>()));
        Gson gson = new Gson();
        String json = gson.toJson(currentRestaurant);
        favourites.remove(json);
        mSharedPreferences.edit().putStringSet("Favourites", favourites).apply();
    }

    private void saveToFavourites(Restaurant currentRestaurant) {
        mSharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        Set<String> favourites = new HashSet<String>(mSharedPreferences.getStringSet("Favourites", new HashSet<String>()));
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentRestaurant);
        favourites.add(json);
        editor.putStringSet("Favourites", favourites).apply();
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.listViewUpdate);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String message = manager.getRestaurants().get(position).toString();

                Intent intent = RestaurantActivity.makeLaunchIntent(CheckUpdateActivity.this, "RestaurantActivity");
                intent.putExtra("Extra", message);
                startActivityForResult(intent, 45);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case (R.id.update_OK_icon):
                Intent intent = new Intent(CheckUpdateActivity.this, MapsActivity.class);
                this.finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
