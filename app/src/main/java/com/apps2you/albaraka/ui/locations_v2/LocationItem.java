package com.apps2you.albaraka.ui.locations_v2;


public class LocationItem {
    private String name;
    private String address;
    private double distance;

    public LocationItem(String name, String address, double distance) {
        this.name = name;
        this.address = address;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getDistance() {
        return distance;
    }
}
