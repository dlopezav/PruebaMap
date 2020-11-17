package com.example.helloapp;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.routing.*;
public class Policia implements Comparable{
    public GeoCoordinates coor;
    public double dist;
    public double distT = 0;
    public boolean t = true;
    public Route Route = null;
    public MapPolyline mapPolyline = null;


    Policia(GeoCoordinates coor){
        this.coor = coor;
        distT = 0;
    }


    @Override
    public int compareTo(Object o) {
        Policia p1 = (Policia) o;
        if(p1.distT == 0 && this.distT == 0){
            if(p1.dist > this.dist) return -1;
            else if(p1.dist < this.dist) return 1;
            return 0;
        }

        if(p1.distT > this.distT) return -1;
        else if(p1.distT > this.distT) return 1;
        else return 0;
    }

    public void setRoute(com.here.sdk.routing.Route route) {
        Route = route;
    }

    public void setDistT(double distT) {
        this.distT = distT;
    }


}
