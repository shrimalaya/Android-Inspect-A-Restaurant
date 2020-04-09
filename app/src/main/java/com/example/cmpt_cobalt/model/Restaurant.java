package com.example.cmpt_cobalt.model;

import com.example.cmpt_cobalt.R;

import java.util.ArrayList;

// restaurant class holding all info related to a restaurant
// mainly stored in the restaurant manager class
public class Restaurant {

    private String name;
    private String streetAddress;
    private String cityAddress;
    private String tracking;

    private double latAddress;
    private double longAddress;

    private int icon;
    private int criticalViolationCount;

    private boolean isFavourite;

    public ArrayList<Inspection> inspections;

    public Restaurant(String name, String streetAddress, String cityAddress, double latAddress, double longAddress, String tracking) {
        this.name = name;
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.latAddress = latAddress;
        this.longAddress = longAddress;
        this.tracking = tracking;
        this.icon = matchLogo();
        this.inspections = new ArrayList<>();
        this.criticalViolationCount = countCriticalViolation();
        this.isFavourite = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public double getLatAddress() {
        return latAddress;
    }

    public double getLongAddress() {
        return longAddress;
    }

    public String getTracking() {
        return tracking;
    }

    public int getIcon() {
        return icon;
    }

    public int getCriticalViolationCount() { return this.criticalViolationCount; }

    public String getLastHazardLevel() {
        if (inspections.isEmpty()) return "None";
        return inspections.get(0).getHazardRating();
    }

    private int countCriticalViolation() {
        int count = 0;
        for (Inspection inspection : inspections) {
            if (inspection.getDiffInDay() <= 365) {
                count = count + inspection.getNumCritical();
            }
        }
        return count;
    }

    private int matchLogo(){
        name = this.getName();
        //Log.e("Woot", name);
        if (name.matches("^(McDonald's).*")){
            return R.drawable.mcdonalds;
        }
        else if (name.matches("^(A&W).*")){
            return R.drawable.a_and_w;
        }
        else if (name.matches("Boiling Point")){
            return R.drawable.boiling_point;
        }
        else if (name.matches("^(Burger King).*")){
            return R.drawable.burgerking;
        }
        else if (name.matches("Chipotle Mexican Grill")){
            return R.drawable.chipotle;
        }
        else if (name.matches("^(Church's Chicken).*")){
            return R.drawable.church_chicken;
        }
        else if (name.matches("^(KFC).*")){
            return R.drawable.kfc;
        }
        else if (name.matches("^(Red Robin).*")){
            return R.drawable.redrobin;
        }
        else if (name.matches("^(Subway).*")){
            return R.drawable.subway;
        }
        else if (name.matches("^(7-Eleven).*")){
            return R.drawable.seven_eleven;
        }
        else if (name.matches("^(Blenz Coffee).*")){
            return R.drawable.blenz;
        }
        else if (name.matches("^(Boston Pizze).^")){
            return R.drawable.boston;
        }
        return R.drawable.log;
    }

    public ArrayList<Inspection> getInspections() {
        return inspections;
    }

    public Inspection getInspection(int inspection) {
        if (inspections.size() <= inspection || inspection < 0){
            return null;
        }

        return inspections.get(inspection);
    }

    public int getInspectionSize() {
        return inspections.size();
    }

    public boolean getFavourite(){
        return isFavourite;
    }

    public int getFavouriteImage() {
        if(isFavourite){
            return android.R.drawable.btn_star_big_on;
        }
        else
        {
            return android.R.drawable.btn_star_big_off;
        }
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public String toString() {
        boolean empty = false;
        Inspection first = new Inspection("", "", "", 0, 0, "", "");

        if (inspections.isEmpty()) {

            empty = true;

        } else {

            first = inspections.get(0);

        }


        if (!empty) {

            return tracking + ", "
                    + name + ", "
                    + (first.getNumCritical() + first.getNumNonCritical())
                    + ", "
                    + first.getHazardRating() + ", "
                    + first.dateFormatter();

        } else {

            return tracking + " "
                    + name + "\nNo inspections";

        }

    }
}
