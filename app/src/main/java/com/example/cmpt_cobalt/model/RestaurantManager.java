package com.example.cmpt_cobalt.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// class that manages all restaurants
// in an ArrayList
public class RestaurantManager implements Iterable<Restaurant>{

    private List<Restaurant> restaurants = new ArrayList<>();

    public void add(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

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
        }
        return instance;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }

    // may need to use this code for later iterations
//    public int getManagerSize() {
//        int count = 0;
//        for(Restaurant restaurant: restaurants)
//            count++;
//
//        return count;
//    }
}
