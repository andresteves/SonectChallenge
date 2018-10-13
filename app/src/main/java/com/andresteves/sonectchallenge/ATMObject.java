package com.andresteves.sonectchallenge;

/**
 * Created by andresteves on 12/10/2018.
 */

public class ATMObject {

    private String address;
    private String name;
    private double latitude;
    private double longitude;

    public ATMObject() {
    }

    public ATMObject(String address, String name, double latitude, double longitude) {
        this.address = address;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
