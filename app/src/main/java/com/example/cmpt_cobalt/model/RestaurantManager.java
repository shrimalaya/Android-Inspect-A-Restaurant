package com.example.cmpt_cobalt.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// class that manages all restaurants
// in an ArrayList
public class RestaurantManager implements Iterable<Restaurant>{

    private List<Restaurant> restaurants = new ArrayList<>();
    private String searchTerm = "";
    private String hazardLevelFilter = "All";
    private String comparator = "All";
    private boolean favouriteOnly = false;
    private int violationLimit;

    public void add(Restaurant restaurant) {
        restaurants.add(restaurant);
    }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public void setHazardLevelFilter(String hazardLevelFilter) { this.hazardLevelFilter = hazardLevelFilter; }
    public void setComparator(String comparator) { this.comparator = comparator; }
    public void setFavouriteOnly(Boolean favouriteOnly) { this.favouriteOnly = favouriteOnly; }
    public void setViolationLimit(int violationLimit) { this.violationLimit = violationLimit; }

    public Restaurant find(String tracking){
        for(Restaurant restaurant: restaurants){
            if(restaurant.getTracking().equals(tracking))
            {
                return restaurant;
            }
        }
        return null;
    }

    public List<Restaurant> getRestaurants() {
        searchTerm = searchTerm.trim();
        if (searchTerm.isEmpty() &&
                hazardLevelFilter.equalsIgnoreCase("All") &&
                comparator.equalsIgnoreCase("All") &&
                !favouriteOnly) return restaurants; // O(1) when search term is empty.

        List<Restaurant> filteredRestaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            if (qualifies(restaurant)) filteredRestaurants.add(restaurant);
        }
        return filteredRestaurants;
    }

    private boolean qualifies(Restaurant restaurant) {
        String restaurantName = restaurant.getName();
        restaurantName = restaurantName.toLowerCase();
        String hazardLevel = restaurant.getLastHazardLevel();
        int criticalViolationCount = restaurant.getCriticalViolationCount();

        if (restaurantName.contains(searchTerm) &&
                ((hazardLevelFilter.equalsIgnoreCase("All")) ||
                        (hazardLevel.equalsIgnoreCase(hazardLevelFilter))) &&
                (inRange(criticalViolationCount)) &&
                (!favouriteOnly || restaurant.getFavourite())) return true;
        else return false;
    }

    boolean inRange(int count) {
        if ((comparator.equalsIgnoreCase("All")) ||
                ((comparator.equalsIgnoreCase("Greater or Equal")) && (count >= violationLimit)) ||
                ((comparator.equalsIgnoreCase("Lesser or Equal")) && (count <= violationLimit))){
            return true;
        }
        return false;
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

    public Restaurant findRestaurantByLatLng(double latitude, double longitude) {
        for(Restaurant res: restaurants) {
            if(res.getLatAddress() == latitude && res.getLongAddress() == longitude){
                return res;
            }
        }
        return null;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return getRestaurants().iterator();
    }

    public int getManagerSize() { return restaurants.size(); }
}
