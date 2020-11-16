package com.example.helloapp;

import com.here.sdk.core.GeoCoordinates;

public class Policia implements Comparable{
    public GeoCoordinates coor;
    public double dist;
    public double distT;

    Policia(GeoCoordinates coor){
        this.coor = coor;
    }


    @Override
    public int compareTo(Object o) {
        Policia p1 = (Policia) o;
        return Integer.parseInt(dist - p1.dist);
    }
}
