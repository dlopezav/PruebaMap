package com.example.helloapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.here.sdk.core.Anchor2D;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private static final String TAG = "MyActivity";
    private RoutingEngine routingEngine;

    private List<Policia> policias = new ArrayList<Policia>();;

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

        createPolicias(5.5016, -73.375);
//        defaultMarker.setCoordinate(new GeoCoordinate);
//        mapView.getMapScene().addMapMarker(defaultMarker);
    }


    //Tunja 2 triangulos
    ////Inferior ( (5.5016, -73.375), (5.5379, -73.3696), (5.5385, -73.3397))
    ////Inferior ( (5.5865, -73.331), (5.5379, -73.3696), (5.5385, -73.3397))
    private void routeMap(GeoCoordinates startGeoCoordinates, GeoCoordinates destinationGeoCoordinates, Policia policia) {
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }
//        GeoCoordinates startGeoCoordinates = new GeoCoordinates(4.6262298,-74.090634);
//        GeoCoordinates destinationGeoCoordinates = new GeoCoordinates(4.732382, -74.104702);
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
                                    showRouteDetails(route);
                                    policia.distT = route.getDurationInSeconds();
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
//                                mapView.getMapScene().addMapPolyline(routeMapPolyline);
                            }


                        } else {
                            new AlertDialog.Builder(MainActivity.this).setTitle("Error while calculating a route:").setMessage(routingError.toString()).show();
                        }
                    }
                });
    }



    private void matcher(){
        Collections.sort(policias);

        List<Policia> elegibles = new ArrayList<Policia>();
        int i = 0;
        for(Policia p : policias){
            elegibles.add(p);
            i++;
            if(i == 10) break;
        }


        for(Policia p : elegibles){


        }

    }
    public void createPolicias(double lat, double lng){

//        5.509604, -73.373164 CAI 1

        double i = 5.5016;
        double j = -73.375;
        while(i < 5.5865){

            j += 0.01;
            i += 0.01;

            GeoCoordinates coordenadas = new GeoCoordinates(i, j);
//            MapImage mapImage = MapImageFactory.fromResource(getApplicationContext().getResources(), R.drawable.police);
            MapImage mapImage = MapImageFactory.fromBitmap(resizeImage(getApplicationContext(), R.drawable.police, 50, 50));
//            Anchor2D anchor2D = new Anchor2D(0.5F, .5F);
            MapMarker mapMarker = new MapMarker(coordenadas, mapImage);
            mapMarker.setImage(mapImage);
            mapView.getMapScene().addMapMarker(mapMarker);
            Policia p = new Policia(coordenadas);
            double lat2 = p.coor.latitude;
            double lng2 = p.coor.longitude;
            p.dist = Math.sqrt( (lat2 - lat)*(lat2 - lat)  + (lng2 - lng)*(lng2 - lng) );
            policias.add(p);
        }

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
        createPolicias(lat, lng);
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
                    mapImage = MapImageFactory.fromResource(getApplicationContext().getResources(), R.drawable.policiacarro);
                    Anchor2D anchor2D = new Anchor2D(0.5F, .5F);
                    MapMarker mapMarker = new MapMarker(coordenadas, mapImage, anchor2D);
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


    public static Bitmap resizeImage(Context ctx, int resId, int w, int h) {

        // cargamos la imagen de origen
        Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),
                resId);

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        // calculamos el escalado de la imagen destino
        float scaleWidth =
                ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // para poder manipular la imagen
        // debemos crear una matriz

        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,
                width, height, matrix, true);

        // si queremos poder mostrar nuestra imagen tenemos que crear un
        // objeto drawable y así asignarlo a un botón, imageview...
        return resizedBitmap;

    }
}