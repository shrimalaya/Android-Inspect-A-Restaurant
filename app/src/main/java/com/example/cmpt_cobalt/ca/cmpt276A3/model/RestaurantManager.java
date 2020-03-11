package com.example.cmpt_cobalt.ca.cmpt276A3.model;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RestaurantManager implements Iterable<Restaurant>{
    private List<Restaurant> restaurants = new ArrayList<>();

    public void add(Restaurant restaurant) {restaurants.add(restaurant);}

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    /*
        Singleton Support
         */
    private static RestaurantManager instance;

    private RestaurantManager() {
        // prevent anyone else from instantiating object
    }

    public static RestaurantManager getInstance() {
        if(instance == null) {
            instance = new RestaurantManager();
            // need to change the file path
            ParseCSV csv = new ParseCSV("raw/restaurants_itr1.csv");

            // start row index at 1 to ignore the titles
            for (int i = 1; i < csv.getRowSize(); i++) {
                Restaurant restaurant = new Restaurant(csv.getVal(i, 1),
                                                        csv.getVal(i, 2),
                                                        csv.getVal(i, 3),
                                                        Float.valueOf(csv.getVal(i, 5)),
                                                        Float.valueOf(csv.getVal(i, 6)),
                                                        csv.getVal(i, 0),
                                                        csv.getVal(i, 4));

                instance.add(restaurant);
            }

            Collections.sort(instance.getRestaurants(), new Comparator<Restaurant>() {
                @Override
                public int compare(Restaurant o1, Restaurant o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });


        }
        return instance;
    }

    /*
    Normal Object Code
     */
    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }

    public int getManagerSize() {
        int count = 0;
        for(Restaurant restaurant: restaurants)
            count++;

        return count;
    }
}
