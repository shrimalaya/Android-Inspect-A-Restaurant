package com.example.cmpt_cobalt.model;

import com.example.cmpt_cobalt.R;

import java.text.ParseException;
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

    public ArrayList<Inspection> inspections;

    public Restaurant(String name, String streetAddress, String cityAddress, double latAddress, double longAddress, String tracking) {
        this.name = name;
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.latAddress = latAddress;
        this.longAddress = longAddress;
        this.tracking = tracking;
        this.icon = R.drawable.log;
        this.inspections = new ArrayList<>();
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
                    + name + "\n"
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
