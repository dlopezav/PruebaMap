package com.example.helloapp;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.routing.*;
public class Policia implements Comparable{
    public GeoCoordinates coor;
    public double dist;
    public double distT = 0;
    public boolean t = true;
    public Route Route = null;

    Policia(GeoCoordinates coor){
        this.coor = coor;
    }


    @Override
    public int compareTo(Object o) {
        Policia p1 = (Policia) o;

        return (int) (p1.dist - this.dist);

    }

    public void setRoute(com.here.sdk.routing.Route route) {
        Route = route;
    }

    public void setDistT(double distT) {
        this.distT = distT;
    }


}
