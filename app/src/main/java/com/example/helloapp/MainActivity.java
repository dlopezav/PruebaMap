package com.example.helloapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private TextView infoRutas;
    private static final String TAG = "MyActivity";
    private RoutingEngine routingEngine;

    private List<Policia> policias = new ArrayList<Policia>();;
    private List<Policia> elegibles = new ArrayList<Policia>();;
    private MapMarker mapMarker;

    private List<List<GeoCoordinates>> ubicacionServicios = new ArrayList<>();
    private int numRutasCalculadas = 0;


    private void generateLocationsServices(){
        ubicacionServicios.add(new ArrayList<>()); //TUNJA
        ubicacionServicios.add(new ArrayList<>()); //SAMACA
        ubicacionServicios.add(new ArrayList<>()); //MOTAVITA
        ubicacionServicios.add(new ArrayList<>()); //SOTAQUIRA

        //TUNJA
        GeoCoordinates coor;
        coor = new GeoCoordinates( 5.548561, -73.365390);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.552694, -73.348190);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.544833, -73.354330);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.558430, -73.352955);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.572863, -73.342985);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.574484, -73.334638);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.532453, -73.368012);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.525407, -73.359631);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.509021, -73.367932);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.526163, -73.359035);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.525300, -73.364041);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.519119, -73.352261);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.532671, -73.344053);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.545124, -73.371545);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.525267, -73.373406);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.549446, -73.353959);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.536870, -73.360883);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates( 5.529256, -73.346527);
        ubicacionServicios.get(0).add(coor);
        //ZONA RURAL TUNJA
        coor = new GeoCoordinates(5.527026, -73.380459);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates(5.511030, -73.343574);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates(5.521546, -73.334337);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates(5.586959, -73.345015);
        ubicacionServicios.get(0).add(coor);
        coor = new GeoCoordinates(5.491047, -73.386332);
        ubicacionServicios.get(0).add(coor);


        //SAMACA
        coor = new GeoCoordinates(5.492094, -73.487034);
        ubicacionServicios.get(1).add(coor);
        coor = new GeoCoordinates(5.493341, -73.465349);
        ubicacionServicios.get(1).add(coor);

        //MOTAVITA
        coor = new GeoCoordinates(5.576905, -73.369377);
        ubicacionServicios.get(2).add(coor);
        coor = new GeoCoordinates(5.585306, -73.363003);
        ubicacionServicios.get(2).add(coor);


        //SOTAQUIRÁ
        coor = new GeoCoordinates(5.764929, -73.246165);
        ubicacionServicios.get(3).add(coor);
        coor = new GeoCoordinates(5.759378, -73.236898);
        ubicacionServicios.get(3).add(coor);

    }

    public void addMarker(int img, GeoCoordinates coordenadas){
        MapImage mapImage = MapImageFactory.fromBitmap(resizeImage(getApplicationContext(), img, 50, 80));
        MapMarker mapMarker = new MapMarker(coordenadas, mapImage);
        mapMarker.setImage(mapImage);
        mapView.getMapScene().addMapMarker(mapMarker);
    }
    public double calcEuclideanDist(GeoCoordinates pos1, GeoCoordinates pos2){
        return Math.sqrt( (pos1.latitude - pos2.latitude)*(pos1.latitude - pos2.latitude)  + (pos1.longitude - pos2.longitude)*(pos1.longitude - pos2.longitude));
    }
    public void createPolicia(GeoCoordinates coordenadas){
        Policia p = new Policia(coordenadas);
        policias.add(p);
        addMarker(R.drawable.police, coordenadas);
    }
    public void createPolicias(){

        //TUNJA
        GeoCoordinates coordenadas = new GeoCoordinates(5.509604, -73.373164);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.536094, -73.361819);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.540132, -73.354164);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.546875, -73.366125);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.515821, -73.371851);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.508934, -73.368204);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.514326, -73.356530);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.526024, -73.356364);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.556059, -73.350545);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.575580, -73.339574);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.509013, -73.368297);
        createPolicia(coordenadas);


//        double i = 5.5016;
//        double j = -73.375;

//        Policia p = new Policia(coordenadas);
//        while(i < 5.5565){
//
//            j += 0.001;
//            i += 0.002;
//            coordenadas = new GeoCoordinates(i, j);
//            createPolicia(coordenadas);
//        }

        //SAMACA
        coordenadas = new GeoCoordinates(5.493094, -73.487034);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.491094, -73.487034);
        createPolicia(coordenadas);


        //SOTAQUIRA
        coordenadas = new GeoCoordinates(5.764929, -73.246165);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.764929, -73.248165);
        createPolicia(coordenadas);

        //MOTAVITA
        coordenadas = new GeoCoordinates(5.576905, -73.369377);
        createPolicia(coordenadas);
        coordenadas = new GeoCoordinates(5.576905, -73.367377);
        createPolicia(coordenadas);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoRutas = (TextView) findViewById(R.id.infoRutas);
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
        generateLocationsServices();
        createPolicias();

//        defaultMarker.setCoordinate(new GeoCoordinate);
//        mapView.getMapScene().addMapMarker(defaultMarker);
    }


    //Tunja 2 triangulos
    ////Inferior ( (5.5016, -73.375), (5.5379, -73.3696), (5.5385, -73.3397))
    ////Inferior ( (5.5865, -73.331), (5.5379, -73.3696), (5.5385, -73.3397))
    private void routeMap(GeoCoordinates destinationGeoCoordinates, int i) {
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }
//        GeoCoordinates startGeoCoordinates = new GeoCoordinates(4.6262298,-74.090634);
//        GeoCoordinates destinationGeoCoordinates = new GeoCoordinates(4.732382, -74.104702);
        Waypoint startWaypoint = new Waypoint(elegibles.get(i).coor);
        System.out.println(elegibles.get(i).coor.latitude+"START");
        Waypoint destinationWaypoint = new Waypoint(destinationGeoCoordinates);
        System.out.println(destinationGeoCoordinates.latitude+"END");
        List<Waypoint> waypoints =
                new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

        routingEngine.calculateRoute(
                waypoints,
                new CarOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                        if (routingError == null) {
                                 elegibles.get(i).setDistT(routes.get(0).getDurationInSeconds());
                                 elegibles.get(i).setRoute(routes.get(0));
//                                 showRouteOnMap(elegibles.get(i).Route, 20);
                                 numRutasCalculadas++;
                                 if(numRutasCalculadas == 10){
                                     showRutas();
                                     numRutasCalculadas = 0;
                                 }
                        } else {
                            new AlertDialog.Builder(MainActivity.this).setTitle("Error while calculating a route:").setMessage(routingError.toString()).show();
                        }

                    }
                });


    }


    private void matcher(double lat, double lng) {
        Collections.sort(policias);
        System.out.println(policias.size()+"AASDDA");
        for (int i = 0; i < 10; ++i) {
            elegibles.add(policias.get(i));
        }
        for (int j = 0; j < elegibles.size(); j++) {
            routeMap (new GeoCoordinates(lat,lng), j);
        }

    }
    public void recalcPolicias(double lat, double lng){
        GeoCoordinates destino = new GeoCoordinates(lat, lng);
        numRutasCalculadas = 0;
        for(Policia p : policias){
            if(p.mapPolyline != null) mapView.getMapScene().removeMapPolyline(p.mapPolyline);
            if(mapMarker != null) mapView.getMapScene().removeMapMarker(mapMarker);
            p.dist = calcEuclideanDist(destino, p.coor);
            p.distT = 0;
            p.mapPolyline = null;
            p.setRoute(null);
        }
        elegibles.clear();
    }


    private String showRouteDetails(Route route) {
        long estimatedTravelTimeInSeconds = route.getDurationInSeconds();
        int lengthInMeters = route.getLengthInMeters();

        String routeDetails =
                "Tiempo de demora: " + formatTime(estimatedTravelTimeInSeconds)
                        + ", Distancia: " + formatLength(lengthInMeters);
//        new AlertDialog.Builder(MainActivity.this).setTitle("Detalles de la ruta:").setMessage(routeDetails).show();
        return routeDetails;
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
        List<GeoCoordinates> posibleServices = ubicacionServicios.get(0);
        java.util.Random random = new java.util.Random();
        int randomLocationServiceIdx = random.nextInt(posibleServices.size());
        GeoCoordinates coor = posibleServices.get(randomLocationServiceIdx);
        double lat = coor.latitude;
        double lng = coor.longitude;
        loadMapScene(lat, lng);
    }
    public void createRandomCoordinatesSamaca(View v){

        List<GeoCoordinates> posibleServices = ubicacionServicios.get(1);
        java.util.Random random = new java.util.Random();
        int randomLocationServiceIdx = random.nextInt(posibleServices.size());
        GeoCoordinates coor = posibleServices.get(randomLocationServiceIdx);
        double lat = coor.latitude;
        double lng = coor.longitude;
        loadMapScene(lat, lng);
    }
    public void createRandomCoordinatesMotavita(View v){
        List<GeoCoordinates> posibleServices = ubicacionServicios.get(2);
        java.util.Random random = new java.util.Random();
        int randomLocationServiceIdx = random.nextInt(posibleServices.size());
        GeoCoordinates coor = posibleServices.get(randomLocationServiceIdx);
        double lat = coor.latitude;
        double lng = coor.longitude;
        loadMapScene(lat, lng);
    }
    public void createRandomCoordinatesSotaquira(View v){
        List<GeoCoordinates> posibleServices = ubicacionServicios.get(3);
        java.util.Random random = new java.util.Random();
        int randomLocationServiceIdx = random.nextInt(posibleServices.size());
        GeoCoordinates coor = posibleServices.get(randomLocationServiceIdx);
        double lat = coor.latitude;
        double lng = coor.longitude;
        loadMapScene(lat, lng);
    }

    private void loadMapScene(double lat, double lng) {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView = findViewById(R.id.map_view);
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    GeoCoordinates coordenadas = new GeoCoordinates(lat,lng);
                    MapImage mapImage = null;
                    mapImage = MapImageFactory.fromBitmap(resizeImage(getApplicationContext(), R.drawable.person, 100, 100));
                    Anchor2D anchor2D = new Anchor2D(0.5F, .5F);
                    mapMarker = new MapMarker(coordenadas, mapImage, anchor2D);
                    double distanceInMeters = 1000 ; //Distancia de la cámara a la tierra
                    mapView.getCamera().lookAt(
                            coordenadas, distanceInMeters);
                    mapView.getMapScene().addMapMarker(mapMarker);
                } else {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }

        });
//        mapView.getMapScene().
        recalcPolicias(lat, lng);
        matcher(lat,lng);
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


    private void showRutas(){
        Collections.sort(elegibles);

        for(int i = elegibles.size() - 1; i > 0 ; --i){

            if(elegibles.get(i).Route != null) showRouteOnMap(elegibles.get(i).Route, i, elegibles.get(i));
        }
        if(elegibles.get(0).Route != null) showRouteOnMap(elegibles.get(0).Route, -1, elegibles.get(0));


        String info = "";
        for(int i = 0; i < elegibles.size(); ++i){
            info += "\n" + "La ruta " + (i+1) + " consta de:\n" + showRouteDetails(elegibles.get(i).Route);
            if(i == 5) break;
        }
        infoRutas.setText(info);
    }


    float widthInPixels = 10;
    private void showRouteOnMap(Route route, int puesto, Policia p) {
        // Show route as polyline.
        GeoPolyline routeGeoPolyline;
        try {
            routeGeoPolyline = new GeoPolyline(route.getPolyline());
        } catch (InstantiationErrorException e) {
            // It should never happen that a route polyline contains less than two vertices.
            return;
        }
        if(puesto == -1) widthInPixels = 20;
        widthInPixels = widthInPixels;
        MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline,
                widthInPixels,
                Color.valueOf((puesto*20)/255f, (255-puesto*10)/255f, (puesto*20)/255f, puesto*10/255f)); // RGBA
        p.mapPolyline = routeMapPolyline;
        mapView.getMapScene().addMapPolyline(routeMapPolyline);
//        mapView.getMapScene().

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