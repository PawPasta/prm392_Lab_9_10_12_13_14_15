package com.prm392_sp26.se182138_lab13;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.prm392_sp26.se182138_lab13.location.LocationService;
import com.prm392_sp26.se182138_lab13.map.MapHelper;
import com.prm392_sp26.se182138_lab13.network.OSMService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MapView map;

    ImageButton btnLocate;
    Button btnSearch;
    EditText inputFrom;
    EditText inputTo;

    private static final int LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        btnLocate = findViewById(R.id.btnLocate);
        btnSearch = findViewById(R.id.btnSearch);
        inputFrom = findViewById(R.id.inputFrom);
        inputTo = findViewById(R.id.inputTo);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        btnLocate.setOnClickListener(v -> locateUser());

        btnSearch.setOnClickListener(v -> searchRoute());

        requestPermission();
    }

    private void requestPermission(){

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        }
    }

    private void locateUser(){

        Location location = LocationService.getLocation(this);

        if(location == null) return;

        MapHelper.showLocation(
                map,
                location.getLatitude(),
                location.getLongitude(),
                "My Location"
        );
    }

    private void searchRoute(){

        String fromAddress = inputFrom.getText().toString().trim();
        String toAddress = inputTo.getText().toString().trim();

        if(fromAddress.isEmpty() || toAddress.isEmpty()){
            Toast.makeText(this,"Please enter both addresses",Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {

            try{

                GeoPoint start = geocodeFirst(fromAddress);
                GeoPoint end = geocodeFirst(toAddress);

                if(start == null || end == null){
                    runOnUiThread(() ->
                            Toast.makeText(this,"Address not found",Toast.LENGTH_SHORT).show());
                    return;
                }

                String routeResponse = OSMService.route(
                        start.getLatitude(),
                        start.getLongitude(),
                        end.getLatitude(),
                        end.getLongitude()
                );

                if(routeResponse == null){
                    runOnUiThread(() ->
                            Toast.makeText(this,"Route not available",Toast.LENGTH_SHORT).show());
                    return;
                }

                JSONObject routeObj = new JSONObject(routeResponse);
                JSONArray routes = routeObj.getJSONArray("routes");
                if(routes.length() == 0){
                    runOnUiThread(() ->
                            Toast.makeText(this,"Route not found",Toast.LENGTH_SHORT).show());
                    return;
                }

                JSONObject firstRoute = routes.getJSONObject(0);
                double distance = firstRoute.getDouble("distance");
                double duration = firstRoute.getDouble("duration");
                JSONObject geometry = firstRoute.getJSONObject("geometry");
                JSONArray coords = geometry.getJSONArray("coordinates");

                List<GeoPoint> path = new ArrayList<>();
                for(int i = 0; i < coords.length(); i++){
                    JSONArray coord = coords.getJSONArray(i);
                    double lon = coord.getDouble(0);
                    double lat = coord.getDouble(1);
                    path.add(new GeoPoint(lat, lon));
                }

                runOnUiThread(() -> {

                    MapHelper.showRoute(
                            map,
                            start,
                            end,
                            path,
                            "Start",
                            "End"
                    );
                    showRoutePopup(fromAddress,toAddress,distance,duration);

                });

            }catch(Exception e){

                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,"Route error",Toast.LENGTH_SHORT).show());
            }

        }).start();

    }

    private GeoPoint geocodeFirst(String query){

        try{
            String result = OSMService.search(query);
            if(result == null) return null;

            JSONArray arr = new JSONArray(result);
            if(arr.length() == 0) return null;

            JSONObject obj = arr.getJSONObject(0);
            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lon");
            return new GeoPoint(lat,lon);

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void showRoutePopup(String from,String to,double distance,double duration){

        double km = distance / 1000.0;
        long minutes = Math.round(duration / 60.0);

        String message =
                "From: " + from + "\n" +
                "To: " + to + "\n" +
                "Distance: " + String.format("%.2f", km) + " km\n" +
                "Duration: " + minutes + " min";

        new AlertDialog.Builder(this)
                .setTitle("Route")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
