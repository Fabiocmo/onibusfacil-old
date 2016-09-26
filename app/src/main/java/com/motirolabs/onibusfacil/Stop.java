package com.motirolabs.onibusfacil;

import java.io.Serializable;
import java.util.ArrayList;

public class Stop implements Serializable {

    private static final long serialVersionUID = 6737955318393184781L;

    private double latitude;

    private double longitude;

    private double distance;

    private String address;

    private ArrayList<Route> routes;

    public Stop() {

        super();

    }

    public Stop(double latitude, double longitude, double distance, String address) {

        super();

        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.address = address;

        this.routes = new ArrayList<Route>();
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

    public double getDistance() {

        return distance;

    }

    public void setDistance(double distance) {

        this.distance = distance;

    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public ArrayList<Route> getRoutes() {

        return routes;

    }

    public void setRoutes(ArrayList<Route> routes) {

        this.routes = routes;

    }

}
