package com.example.cmpt_cobalt.ca.cmpt276A3.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Restaurant {

    private String name;
    private String streetAddress;
    private String cityAddress;
    private float latAddress;
    private float longAddress;
    private String tracking;
    private String icon;
    private ArrayList<Inspection> inspections;

    public Restaurant(String name, String streetAddress, String cityAddress, float latAddress, float longAddress, String tracking, String icon) {
        this.name = name;
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.latAddress = latAddress;
        this.longAddress = longAddress;
        this.tracking = tracking;
        this.icon = icon;
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

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCityAddress() {
        return cityAddress;
    }

    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }

    public float getLatAddress() {
        return latAddress;
    }

    public void setLatAddress(float latAddress) {
        this.latAddress = latAddress;
    }

    public float getLongAddress() {
        return longAddress;
    }

    public void setLongAddress(float longAddress) {
        this.longAddress = longAddress;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setInspections(Inspection inspection) {
        inspections.add(inspection);

    }

    public ArrayList<Inspection> getInspections() {
        return inspections;
    }

    public Inspection getInspection(int inspection) {
        return inspections.get(inspection);

    }

    @Override
    public String toString() {
        return tracking + ' '
                + name;
    }

    /* TODO: 1. Address
             2. List of Inspections
             3. getInspection()
             4. getViolation()
     */
}
