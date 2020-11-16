package com.example.helloapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private static final String TAG = "MyActivity";
    private RoutingEngine routingEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a MapView instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.setOnReadyListener(new MapView.OnReadyListener() {
            @Override
            public void onMapViewReady() {
                // This will be called each time after this activity is resumed.
                // It will not be called before the first map scene was loaded.
                // Any code that requires map data may not work as expected beforehand.
                Log.d(TAG, "HERE Rendering Engine attached.");
            }
        });
//        MapMarker defaultMarker = new MapMarker();
//        defaultMarker.setCoordinate(new GeoCoordinate(37.7397, -121.4252, 0.0));
//        mapView.addMapObject(defaultMarker);
    }


    //Tunja 2 triangulos
    ////Inferior ( (5.5016, -73.375), (5.5379, -73.3696), (5.5385, -73.3397))
    ////Inferior ( (5.5865, -73.331), (5.5379, -73.3696), (5.5385, -73.3397))
    private void routeMap() {
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }
        GeoCoordinates startGeoCoordinates = new GeoCoordinates(4.6262298,-74.090634);
        GeoCoordinates destinationGeoCoordinates = new GeoCoordinates(4.732382, -74.104702);
        Waypoint startWaypoint = new Waypoint(startGeoCoordinates);
        Waypoint destinationWaypoint = new Waypoint(destinationGeoCoordinates);

        List<Waypoint> waypoints =
                new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

        routingEngine.calculateRoute(
                waypoints,
                new CarOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                        if (routingError == null) {


                            ArrayList<GeoPolyline> routeGeoPolylines = new ArrayList<GeoPolyline>();
                            try {
                                for(Route route: routes){
                                    routeGeoPolylines.add(new GeoPolyline(route.getPolyline()));
                                }

                            } catch (InstantiationErrorException e) {
                                // It should never happen that a route polyline contains less than two vertices.
                                return;
                            }

                            float widthInPixels = 20;
                            ArrayList<MapPolyline> routeMapPolylines = new ArrayList<MapPolyline>();

                            for(GeoPolyline geo: routeGeoPolylines){
                                MapPolyline routeMapPolyline = new MapPolyline(geo,
                                        widthInPixels,
                                        Color.valueOf(0f, 0.56f, 0.54f, 0.63f)); // RGBA
                                mapView.getMapScene().addMapPolyline(routeMapPolyline);
                            }


                        } else {
                            new AlertDialog.Builder(MainActivity.this).setTitle("Error while calculating a route:").setMessage(routingError.toString()).show();
                        }
                    }
                });
    }
    private void showRouteDetails(Route route) {
        long estimatedTravelTimeInSeconds = route.getDurationInSeconds();
        int lengthInMeters = route.getLengthInMeters();

        String routeDetails =
                "Travel Time: " + formatTime(estimatedTravelTimeInSeconds)
                        + ", Length: " + formatLength(lengthInMeters);
        new AlertDialog.Builder(MainActivity.this).setTitle("Detalles de la ruta:").setMessage(routeDetails).show();
    }


    private String formatLength(int meters) {
        int kilometers = meters / 1000;
        int remainingMeters = meters % 1000;

        return String.format(Locale.getDefault(), "%02d.%02d km", kilometers, remainingMeters);
    }
    private String formatTime(long sec) {
        int hours = (int) (sec / 3600);
        int minutes = (int) ((sec % 3600) / 60);

        return String.format(Locale.getDefault(), "%02d horas, %02d minutos", hours, minutes);
    }

    public void createRandomCoordinatesTunja(View v){

        double lat = 5.5016;
        double lng = -73.375;

        loadMapScene(lat, lng);
    }
    public void createRandomCoordinatesMotavita(View v){

        double lat = 5.578000;
        double lng = -73.368144;

        loadMapScene(lat, lng);
    }
    public void createRandomCoordinatesSomaca(View v){

        double lat = 5.492345;
        double lng = -73.486189;

        loadMapScene(lat, lng);
    }
    private void loadMapScene(double lat, double lng) {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    GeoCoordinates coordenadas = new GeoCoordinates(lat,lng);
                    MapImage mapImage = null;
                    try {
                        mapImage = new MapImage("~/HelloApp/app/src/img/marker.png",20,20);
                    } catch (InstantiationErrorException e) {
                        e.printStackTrace();
                    }
                    MapMarker mapMarker = new MapMarker(coordenadas, mapImage);
                    double distanceInMeters = 1000 ; //Distancia de la cámara a la tierra
                    mapView.getCamera().lookAt(
                            coordenadas, distanceInMeters);
                    mapView.getMapScene().addMapMarker(mapMarker);
                } else {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}